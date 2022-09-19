package com.nono.deluxe.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.nono.deluxe.controller.dto.MessageResponseDTO;
import com.nono.deluxe.controller.dto.auth.EmailRequestDTO;
import com.nono.deluxe.controller.dto.auth.JoinRequestDTO;
import com.nono.deluxe.controller.dto.auth.JoinResponseDTO;
import com.nono.deluxe.controller.dto.auth.VerifyEmailRequestDTO;
import com.nono.deluxe.domain.checkemail.CheckType;
import com.nono.deluxe.domain.checkemail.CheckEmail;
import com.nono.deluxe.domain.checkemail.CheckEmailRepository;
import com.nono.deluxe.domain.user.Role;
import com.nono.deluxe.domain.user.User;
import com.nono.deluxe.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {

    @Value("${auth.key}")
    private String key;
    @Value("${auth.issuer}")
    private String issuer;

    private final UserRepository userRepository;
    private final CheckEmailRepository checkEmailRepository;

    private final JavaMailSender javaMailSender;

    @Transactional
    public JoinResponseDTO joinUser(JoinRequestDTO requestDTO) {
        String email = requestDTO.getEmail();
        CheckEmail checkEmail = checkEmailRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email Not Checked"));

        if (checkEmail.isVerified() && checkEmail.getType().equals(CheckType.JOIN)) {
            User user = requestDTO.toEntity();
            User savedUser = userRepository.save(user);
            return new JoinResponseDTO(savedUser);
        }
        throw new RuntimeException("Email Not Verified");
    }

    /**
     * 입력 값을 받아 회원 여부를 판별하고, tokenActiveSeconds 만큼의 (초단위) 유효기간으로 토큰을 생성하고 반환
     * @param email
     * @param password
     * @param tokenActiveSeconds
     * @return
     */
    @Transactional(readOnly = true)
    public String loginUser(String email, String password, long tokenActiveSeconds) {
        User user = userRepository.findByEmailAndPassword(email, password)
                .orElseThrow(() -> new RuntimeException("Not Found User"));
        return createToken(user.getName(), user.getId(), user.getRole(), tokenActiveSeconds);
    }

    @Transactional(readOnly = true)
    public MessageResponseDTO checkDuplicateEmail(EmailRequestDTO requestDTO) {
        String email = requestDTO.getEmail();
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if(optionalUser.isEmpty()) return new MessageResponseDTO(true, "enable email");
        return new MessageResponseDTO(false, "already used email");
    }

    @Transactional
    public MessageResponseDTO checkEmail(EmailRequestDTO requestDTO) {
        String email = requestDTO.getEmail();
        deleteLegacyEmailCode(email); // 이전 인증 메일이 있다면 최신화를 위해 삭제시킴
        SimpleMailMessage message = createCheckEmailMessage(email); // checkEmail 객체 생성 및 메세지 반환
        javaMailSender.send(message);

        return new MessageResponseDTO(true, "mail posted");
    }

    @Transactional
    public MessageResponseDTO verifyEmail(VerifyEmailRequestDTO requestDTO) {
        String email = requestDTO.getEmail();
        String code = requestDTO.getCode();

        CheckEmail checkEmail = checkEmailRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Not Found Check Email"));

        if(checkEmail.getEmail().equals(email) && verifyValidTime(checkEmail)) {
            // code 가 맞았을 경우, 시간 또한 제한시간 안쪽일때
            checkEmail.verify();
            return new MessageResponseDTO(true, "success");
        }
        return new MessageResponseDTO(false, "fail");
    }

    private boolean verifyValidTime(CheckEmail checkEmail) {
        LocalDateTime createdAt = checkEmail.getCreatedAt();
        long milliOfCreatedAt = ZonedDateTime.of(createdAt, ZoneId.systemDefault()).toInstant().toEpochMilli();
        return milliOfCreatedAt + (1000 * 60 * 10) >= System.currentTimeMillis();
    }

    private SimpleMailMessage createCheckEmailMessage(String email) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);

        Optional<User> optionalUser = userRepository.findByEmail(email);
        CheckType type;
        if(optionalUser.isEmpty()) {
            // join check email
            message.setSubject("노노 Deluxe 회원가입 인증번호 메일입니다.");
            type = CheckType.JOIN;
        } else {
            // password reissue email
            message.setSubject("노노 Deluxe 비밀번호 초기화 메일입니다.");
            type = CheckType.REISSUE;
        }

        String verifyCode = UUID.randomUUID().toString().substring(0, 8);
        CheckEmail checkEmail = CheckEmail.builder()
                .email(email)
                .type(type)
                .verifyCode(verifyCode)
                .build();
        checkEmailRepository.save(checkEmail);
        message.setText(verifyCode);
        return message;
    }

    private void deleteLegacyEmailCode(String email) {
        // 이미 이메일에 발송된 코드라면 삭제하고 최신화
        Optional<CheckEmail> byEmail = checkEmailRepository.findByEmail(email);
        byEmail.ifPresent(checkEmailRepository::delete);
    }

    /**
     * 토큰을 생성하고 반환함
     * @param username
     * @param tokenActiveSeconds
     * @return
     */
    public String createToken(String username, long userId, Role userRole, long tokenActiveSeconds) {
        Algorithm algorithm = getAlgorithm(key);
        return JWT.create()
                .withExpiresAt(new Date(System.currentTimeMillis() + (1000 * tokenActiveSeconds)))
                .withIssuer(issuer)
                .withClaim("username", username)
                .withClaim("userId", userId)
                .withClaim("ROLE", userRole.toString())
                .sign(algorithm);
    }

    /**
     * 유효한 유저인지 확인하고, User 객체를 반환
     * @param token
     * @return
     */
    public DecodedJWT decodeToken(String token) {
        Algorithm algorithm = getAlgorithm(key);
        JWTVerifier verifier = getVerifier(algorithm);
        try {
            String extractedToken = extractToken(token);
            DecodedJWT decodedJWT = verifier.verify(extractedToken);
            long userId = Long.parseLong(decodedJWT.getClaim("userId").toString());
            userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Not Found User"));
            log.info("user Login : {}", decodedJWT.getClaim("username").toString());
            return decodedJWT;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("invalid token");
        }
    }

    public long getUserIdByDecodedToken(DecodedJWT jwt) {
        return Long.parseLong(jwt.getClaim("userId").toString());
    }

    public boolean isParticipant(DecodedJWT jwt) {
        return jwt.getClaim("ROLE").toString().replaceAll("\"", "").equals(Role.ROLE_PARTICIPANT.toString());
    }

    public boolean isManager(DecodedJWT jwt) {
        return jwt.getClaim("ROLE").toString().replaceAll("\"", "").equals(Role.ROLE_MANAGER.toString());
    }

    public boolean isAdmin(DecodedJWT jwt) {
        return jwt.getClaim("ROLE").toString().replaceAll("\"", "").equals(Role.ROLE_ADMIN.toString());
    }


    /**
     * bearer token 형식 검증 및 토큰 추출
     * @param token
     * @return
     */
    public String extractToken(String token) {
        if(token.matches("(^Bearer [A-Za-z0-9-_]*\\.[A-Za-z0-9-_]*\\.[A-Za-z0-9-_]*$)")) {
            return token.split(" ")[1];
        } else {
            throw new RuntimeException("is not bearer token: " + token);
        }
    }

    /**
     * algorithm 생성기
     * @param key
     * @return
     */
    private Algorithm getAlgorithm(String key) {
        return Algorithm.HMAC256(key);
    }


    /**
     * verifier 생성기
     * @param algorithm
     * @return
     */
    private JWTVerifier getVerifier(Algorithm algorithm) {
        return JWT.require(algorithm)
                .withIssuer(issuer)
                .build();
    }
}
