package com.nono.deluxe.application;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.nono.deluxe.domain.authcode.AuthCode;
import com.nono.deluxe.domain.authcode.AuthCodeRepository;
import com.nono.deluxe.domain.checkemail.CheckEmail;
import com.nono.deluxe.domain.checkemail.CheckEmailRepository;
import com.nono.deluxe.domain.checkemail.CheckType;
import com.nono.deluxe.domain.user.Role;
import com.nono.deluxe.domain.user.User;
import com.nono.deluxe.domain.user.UserRepository;
import com.nono.deluxe.exception.InvalidTokenException;
import com.nono.deluxe.exception.NoAuthorityException;
import com.nono.deluxe.presentation.dto.MessageResponseDTO;
import com.nono.deluxe.presentation.dto.auth.AuthCodeResponseDTO;
import com.nono.deluxe.presentation.dto.auth.CreateAuthCodeRequestDTO;
import com.nono.deluxe.presentation.dto.auth.EmailRequestDTO;
import com.nono.deluxe.presentation.dto.auth.JoinRequestDTO;
import com.nono.deluxe.presentation.dto.auth.JoinResponseDTO;
import com.nono.deluxe.presentation.dto.auth.ReissueUserRequestDTO;
import com.nono.deluxe.presentation.dto.auth.TokenRequestDTO;
import com.nono.deluxe.presentation.dto.auth.TokenResponseDTO;
import com.nono.deluxe.presentation.dto.auth.VerifyEmailRequestDTO;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final MailClient mailClient;
    private final BCryptPasswordEncoder encoder;

    @Transactional
    public JoinResponseDTO joinUser(JoinRequestDTO requestDTO) {
        String email = requestDTO.getEmail();
        String code = requestDTO.getCode();
        CheckEmail checkEmail = checkEmailRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Email Not Checked"));

        if (checkEmail.isVerified()
            && checkEmail.getVerifyCode().equals(code)
            && checkEmail.getType().equals(CheckType.JOIN)) {

            User user = requestDTO.toEntity();
            user.encodePassword(encoder);

            return new JoinResponseDTO(userRepository.save(user));
        }
        throw new RuntimeException("Email Not Verified OR Verify Code Not Collect");
    }

    @Transactional(readOnly = true)
    public AuthCodeResponseDTO createAuthCode(CreateAuthCodeRequestDTO requestDTO) {
        String email = requestDTO.getEmail();
        String password = requestDTO.getPassword();

        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Not Found User"));

        if (encoder.matches(password, user.getPassword())) {
            return createAuthCode(user.getId());
        }
        throw new IllegalArgumentException("올바르지 않은 패스워드");
    }

    @Transactional
    public AuthCodeResponseDTO createAuthCode(long userCode) {
        deleteLegacyLoginCode(userCode);

        User user = userRepository.findById(userCode)
            .orElseThrow(() -> new RuntimeException("Not Found User"));

        String verifyCode = createRandomString("1234567890", 6);

        AuthCode loginCode = AuthCode.builder()
            .user(user)
            .verifyCode(verifyCode)
            .build();
        authCodeRepository.save(loginCode);

        return new AuthCodeResponseDTO(loginCode);
    }

    private TokenResponseDTO createTokenResponseDTO(User user) {
        String accessToken = createAccessToken(user.getName(), user.getId(), user.getRole());
        String refreshToken = createRefreshToken(user.getName(), user.getId(), user.getRole());

        TokenResponseDTO responseDTO = new TokenResponseDTO();
        responseDTO.setToken_type("bearer");
        responseDTO.setAccess_token(accessToken);
        responseDTO.setRefresh_token(refreshToken);
        responseDTO.setExpires_in(decodeJWT(accessToken, accessKey).getExpiresAt().getTime());
        responseDTO.setRefresh_token_expires_in(decodeJWT(refreshToken, refreshKey).getExpiresAt().getTime());
        return responseDTO;
    }

    @Transactional
    public TokenResponseDTO createToken(TokenRequestDTO requestDTO) {
        String grantType = requestDTO.getGrant_type().toLowerCase();
        if (grantType.equals("authorization_code")) {
            return createTokenByAuthCode(requestDTO.getCode());
        } else if (grantType.equals("refresh_token")) {
            return createTokenByRefreshToken(requestDTO.getRefresh_token());
        } else {
            throw new IllegalArgumentException("invalid grant_type");
        }
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
        DecodedJWT decodedJWT = decodeJWT(refreshToken, refreshKey);
        long userId = Long.parseLong(decodedJWT.getClaim("userId").toString());
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Not Found User"));

        return createTokenResponseDTO(user);
    }

    @Transactional(readOnly = true)
    public MessageResponseDTO checkDuplicateEmail(EmailRequestDTO requestDTO) {
        String email = requestDTO.getEmail();
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            return new MessageResponseDTO(true, "enable email");
        }
        return new MessageResponseDTO(false, "already used email");
    }

    @Transactional
    public MessageResponseDTO checkEmail(EmailRequestDTO requestDTO) {
        String email = requestDTO.getEmail();

        deleteLegacyEmailCode(email);

        CheckType type = CheckType.valueOf(requestDTO.getType().toUpperCase());
        String verifyCode = getVerifyCode();

        CheckEmail checkEmail = CheckEmail.builder()
            .email(email)
            .verifyCode(verifyCode)
            .type(type)
            .build();
        checkEmailRepository.save(checkEmail);

        postEmail(checkEmail);

        return new MessageResponseDTO(true, "mail posted");
    }

    private void postEmail(CheckEmail checkEmail) {
        String email = checkEmail.getEmail();
        CheckType type = checkEmail.getType();
        String verifyCode = checkEmail.getVerifyCode();
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (type.equals(CheckType.JOIN)) {
            if (optionalUser.isPresent()) {
                throw new RuntimeException("already exist email");
            }
            mailClient.postJoinCheckMail(email, verifyCode);
        } else if (type.equals(CheckType.REISSUE)) {
            if (optionalUser.isEmpty()) {
                throw new RuntimeException("not exist email");
            }
            mailClient.postReissueCheckMail(email, verifyCode);
        } else {
            throw new RuntimeException("invalid CheckType");
        }
    }

    @Transactional
    public MessageResponseDTO verifyEmail(VerifyEmailRequestDTO requestDTO) {
        String email = requestDTO.getEmail();
        String code = requestDTO.getCode();

        CheckEmail checkEmail = checkEmailRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Not Found Check Email"));

        if (checkEmail.getVerifyCode().equals(code) && verifyValidTime(checkEmail)) {
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

        if (validateReissueEmail(checkEmail, code)) {
            User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Not Found User"));

            String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";
            String newPassword = createRandomString(chars, 12);

            mailClient.postReissuePasswordMail(email, newPassword);

            user.updatePassword(newPassword);
            user.encodePassword(encoder);
            
            return new MessageResponseDTO(true, "password reset");
        }
        throw new RuntimeException("Email Not Verified OR Verify Code Not Collect");
    }

    private boolean validateReissueEmail(CheckEmail checkEmail, String verifyCode) {
        return checkEmail.isVerified()
            && checkEmail.getVerifyCode().equals(verifyCode)
            && checkEmail.getType().equals(CheckType.REISSUE);
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

        for (int i = 0; i < randomStringLength; i++) {
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
        List<CheckEmail> checkEmailList = checkEmailRepository.findAllByEmail(email);
        checkEmailRepository.deleteAll(checkEmailList);
    }

    // 토큰 생성 후 반환 유효시간 2시간
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

    public DecodedJWT decodeJwt(String token) {
        JWTVerifier verifier = getVerifier(accessKey);

        String extractedToken = extractToken(token);
        DecodedJWT decodedJWT = verifier.verify(extractedToken);

        Long userId = decodedJWT.getClaim("userId").asLong();
        userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Not Found User"));

        log.info("user Login : {}", decodedJWT.getClaim("userId").toString());
        return decodedJWT;
    }

    private DecodedJWT decodeJWT(String token, String key) {
        JWTVerifier verifier = getVerifier(key);
        try {
            return verifier.verify(token);
        } catch (Exception e) {
            throw new RuntimeException("invalid token");
        }
    }

    public long getUserIdByDecodedToken(DecodedJWT jwt) {
        return Long.parseLong(jwt.getClaim("userId").toString());
    }

    public void validateAdminToken(String token) {
        validateAdminRole(decodeJwt(token));
    }

    public void validateManagerToken(String token) {
        validateManagerRole(decodeJwt(token));
    }

    public void validateParticipantToken(String token) {
        validateParticipantRole(decodeJwt(token));
    }

    public void validateAdminRole(DecodedJWT jwt) {
        if (!isAdmin(jwt)) {
            throw new NoAuthorityException("Forbidden API");
        }
    }

    private void validateManagerRole(DecodedJWT jwt) {
        if (!isManager(jwt) && !isAdmin(jwt)) {
            throw new NoAuthorityException("Forbidden API");
        }
    }

    private void validateParticipantRole(DecodedJWT jwt) {
        if (!isParticipant(jwt) && !isManager(jwt) && !isAdmin(jwt)) {
            throw new NoAuthorityException("Forbidden API");
        }
    }

    private String getRoleByJwt(DecodedJWT jwt) {
        return jwt.getClaim("ROLE").toString().replaceAll("\"", "");
    }

    private boolean isAdmin(DecodedJWT jwt) {
        return getRoleByJwt(jwt).equals(Role.ROLE_ADMIN.toString());
    }

    private boolean isManager(DecodedJWT jwt) {
        return getRoleByJwt(jwt).equals(Role.ROLE_MANAGER.toString());
    }

    private boolean isParticipant(DecodedJWT jwt) {
        return getRoleByJwt(jwt).equals(Role.ROLE_PARTICIPANT.toString());
    }

    // bearer token 형식 검증 및 토큰 추출
    private String extractToken(String token) {
        if (token.matches("(^([Bb]earer) [A-Za-z0-9-_]*\\.[A-Za-z0-9-_]*\\.[A-Za-z0-9-_]*$)")) {
            return token.split(" ")[1];
        } else {
            throw new InvalidTokenException("InvalidTokenForm");
        }
    }

    private Algorithm getAlgorithm(String key) {
        return Algorithm.HMAC256(key);
    }

    private JWTVerifier getVerifier(String key) {
        return JWT.require(Algorithm.HMAC256(key))
            .withIssuer(issuer)
            .build();
    }
}
