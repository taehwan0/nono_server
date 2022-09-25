package com.nono.deluxe.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.nono.deluxe.controller.dto.MessageResponseDTO;
import com.nono.deluxe.controller.dto.auth.*;
import com.nono.deluxe.domain.checkemail.CheckType;
import com.nono.deluxe.domain.checkemail.CheckEmail;
import com.nono.deluxe.domain.checkemail.CheckEmailRepository;
import com.nono.deluxe.domain.authcode.AuthCode;
import com.nono.deluxe.domain.authcode.AuthCodeRepository;
import com.nono.deluxe.domain.user.Role;
import com.nono.deluxe.domain.user.User;
import com.nono.deluxe.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@EnableAsync
@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {

    @Value("${auth.accessKey}")
    private String accessKey;
    @Value("${auth.refreshKey}")
    private String refreshKey;
    @Value("${auth.issuer}")
    private String issuer;

    private final UserRepository userRepository;
    private final CheckEmailRepository checkEmailRepository;
    private final AuthCodeRepository authCodeRepository;
    private final MailService mailService;

    @Transactional
    public JoinResponseDTO joinUser(JoinRequestDTO requestDTO) {
        String email = requestDTO.getEmail();
        String code = requestDTO.getCode();
        CheckEmail checkEmail = checkEmailRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email Not Checked"));

        if (checkEmail.isVerified() &&
                checkEmail.getVerifyCode().equals(code) &&
                checkEmail.getType().equals(CheckType.JOIN)) {
            User user = requestDTO.toEntity();
            User savedUser = userRepository.save(user);
            return new JoinResponseDTO(savedUser);
        }
        throw new RuntimeException("Email Not Verified OR Verify Code Not Collect");
    }

    /**
     * authorization_code 생성
     * @param userCode
     * @return
     */
    @Transactional
    public AuthCodeResponseDTO createAuthCode(long userCode) {
        deleteLegacyLoginCode(userCode);
        User user = userRepository.findById(userCode)
                .orElseThrow(() -> new RuntimeException("Not Found User"));
        String authCode = createRandomString("1234567890", 6);

        AuthCode loginCode = AuthCode.builder()
                .user(user)
                .verifyCode(authCode)
                .build();
        authCodeRepository.save(loginCode);

        return new AuthCodeResponseDTO(loginCode);
    }

    @Transactional(readOnly = true)
    public AuthCodeResponseDTO createAuthCode(CreateAuthCodeRequestDTO requestDTO) {
        String email = requestDTO.getEmail();
        String password = requestDTO.getPassword();
        User user = userRepository.findByEmailAndPassword(email, password)
                .orElseThrow(() -> new RuntimeException("Not Found User"));

        return createAuthCode(user.getId());
    }

    private TokenResponseDTO createTokenResponseDTO(User user) {
        String accessToken = createAccessToken(user.getName(), user.getId(), user.getRole());
        String refreshToken = createRefreshToken(user.getName(), user.getId(), user.getRole());

        TokenResponseDTO responseDTO = new TokenResponseDTO();
        responseDTO.setToken_type("bearer");
        responseDTO.setAccess_token(accessToken);
        responseDTO.setRefresh_token(refreshToken);
        responseDTO.setExpires_in(decodeAccessToken(accessToken).getExpiresAt().getTime());
        responseDTO.setRefresh_token_expires_in(decodeRefreshToken(refreshToken).getExpiresAt().getTime());
        return responseDTO;
    }

    @Transactional
    public TokenResponseDTO createToken(TokenRequestDTO requestDTO) {
        String grant_type = requestDTO.getGrant_type().toLowerCase();
        if(grant_type.equals("authorization_code")) return createTokenByAuthCode(requestDTO.getCode());
        else if(grant_type.equals("refresh_token")) return createTokenByRefreshToken(requestDTO.getRefresh_token());
        else throw new RuntimeException("invalid grant_type");
    }

    @Transactional
    public TokenResponseDTO createTokenByAuthCode(String authCode) {
        AuthCode loginCode = authCodeRepository.findByAuthCode(authCode)
                .orElseThrow(() -> new RuntimeException("Not Found LoginCode"));

        authCodeRepository.delete(loginCode);
        User user = loginCode.getUser();

        return createTokenResponseDTO(user);
    }

    @Transactional
    public TokenResponseDTO createTokenByRefreshToken(String refreshToken) {
        DecodedJWT decodedJWT = decodeRefreshToken(refreshToken);
        long userId = Long.parseLong(decodedJWT.getClaim("userId").toString());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Not Found User"));

        return createTokenResponseDTO(user);
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

        String verifyCode = getVerifyCode();

        CheckType type;
        if(isNewUser(email)) {
            // 신규 유저의 경우 회원가입 체크 메일 발송
            type = CheckType.JOIN;
            mailService.postJoinCheckMail(email, verifyCode);
        } else {
            // 기존 유저의 경우 재발급 체크 메일 발송
            type = CheckType.REISSUE;
            mailService.postReissueCheckMail(email, verifyCode);
        }

        CheckEmail checkEmail = CheckEmail.builder()
                .email(email)
                .verifyCode(verifyCode)
                .type(type)
                .build();
        checkEmailRepository.save(checkEmail);

        return new MessageResponseDTO(true, "mail posted");
    }

    private boolean isNewUser(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        return optionalUser.isEmpty();
    }

    @Transactional
    public MessageResponseDTO verifyEmail(VerifyEmailRequestDTO requestDTO) {
        String email = requestDTO.getEmail();
        String code = requestDTO.getCode();

        CheckEmail checkEmail = checkEmailRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Not Found Check Email"));

        if(checkEmail.getVerifyCode().equals(code) && verifyValidTime(checkEmail)) {
            // code 가 맞았을 경우, 시간 또한 제한시간 안쪽일때
            checkEmail.verify();
            return new MessageResponseDTO(true, "success");
        }
        return new MessageResponseDTO(false, "fail");
    }

    @Transactional
    public MessageResponseDTO reissueUser(ReissueUserRequestDTO requestDTO) {
        String email = requestDTO.getEmail();
        String code = requestDTO.getCode();

        CheckEmail checkEmail = checkEmailRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Not Found Check Email"));

        if(checkEmail.isVerified() &&
                checkEmail.getVerifyCode().equals(code) &&
                checkEmail.getType().equals(CheckType.REISSUE)) {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Not Found User"));

            String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";
            String newPassword = createRandomString(chars, 12);

            user.updatePassword(newPassword);

            mailService.postReissuePasswordMail(email, newPassword);

            return new MessageResponseDTO(true, "password reset");
        }
        throw new RuntimeException("Email Not Verified OR Verify Code Not Collect");
    }





    private void deleteLegacyLoginCode(long userCode) {
        List<AuthCode> authCodeList = authCodeRepository.findByUserCode(userCode);
        authCodeRepository.deleteAll(authCodeList);
    }

    private String getVerifyCode() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    private String createRandomString(String charsTable, int randomStringLength) {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < randomStringLength; i ++) {
            int randomInt = random.nextInt(charsTable.length());
            sb.append(charsTable.charAt(randomInt));
        }

        return sb.toString();
    }

    private boolean verifyValidTime(CheckEmail checkEmail) {
        LocalDateTime createdAt = checkEmail.getCreatedAt();
        long milliOfCreatedAt = ZonedDateTime.of(createdAt, ZoneId.systemDefault()).toInstant().toEpochMilli();
        return milliOfCreatedAt + (1000 * 60 * 10) >= System.currentTimeMillis();
    }

    private void deleteLegacyEmailCode(String email) {
        // 이미 이메일에 발송된 코드라면 삭제하고 최신화
        Optional<CheckEmail> byEmail = checkEmailRepository.findByEmail(email);
        byEmail.ifPresent(checkEmailRepository::delete);
    }

    /**
     * 토큰 생성 후 반환 유효시간 2시간
     * @param username
     * @param userId
     * @param userRole
     * @return
     */
    private String createAccessToken(String username, long userId, Role userRole) {
        log.info("AccessToken Created By: {}", userId);
        Algorithm algorithm = getAlgorithm(accessKey);
        return JWT.create()
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + (1000L * 60 * 60 * 2)))
                .withIssuer(issuer)
                .withClaim("username", username)
                .withClaim("userId", userId)
                .withClaim("ROLE", userRole.toString())
                .sign(algorithm);
    }

    // 1년짜리 리프레쉬 토큰 -> 시간 변경 필요
    private String createRefreshToken(String username, long userId, Role userRole) {
        log.info("RefreshToken Created By: {}", userId);
        Algorithm algorithm = getAlgorithm(refreshKey);
        return JWT.create()
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 365)))
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
    public DecodedJWT decodeAccessTokenByRequestHeader(String token) {
        Algorithm algorithm = getAlgorithm(accessKey);
        JWTVerifier verifier = getVerifier(algorithm);
        try {
            String extractedToken = extractToken(token);
            DecodedJWT decodedJWT = verifier.verify(extractedToken);
            long userId = Long.parseLong(decodedJWT.getClaim("userId").toString());
            userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Not Found User"));
            log.info("user Login : {}", decodedJWT.getClaim("userId").toString());
            return decodedJWT;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("invalid token");
        }
    }

    private DecodedJWT decodeAccessToken(String token) {
        Algorithm algorithm = getAlgorithm(accessKey);
        JWTVerifier verifier = getVerifier(algorithm);
        try {
            return verifier.verify(token);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("invalid token");
        }
    }

    private DecodedJWT decodeRefreshToken(String token) {
        Algorithm algorithm = getAlgorithm(refreshKey);
        JWTVerifier verifier = getVerifier(algorithm);
        try {
            return verifier.verify(token);
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
    private String extractToken(String token) {
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