package com.nono.deluxe.application.service;

import com.amazonaws.services.kms.model.NotFoundException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.nono.deluxe.application.client.MailClient;
import com.nono.deluxe.application.client.TokenClient;
import com.nono.deluxe.domain.authcode.AuthCode;
import com.nono.deluxe.domain.authcode.AuthCodeRepository;
import com.nono.deluxe.domain.checkemail.CheckEmail;
import com.nono.deluxe.domain.checkemail.CheckEmailRepository;
import com.nono.deluxe.domain.checkemail.CheckType;
import com.nono.deluxe.domain.user.User;
import com.nono.deluxe.domain.user.UserRepository;
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
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@EnableAsync
@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {

    private static final long CHECK_MAIL_VALID_MILLISECOND = 1000L * 60 * 30;
    private static final long LOGIN_CODE_VALID_MILLISECOND = 1000L * 60 * 10;

    private final TokenClient tokenClient;
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

            checkEmailRepository.delete(checkEmail);

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

    @Transactional
    public TokenResponseDTO createToken(TokenRequestDTO requestDTO) {
        String grantType = requestDTO.getGrant_type().toLowerCase();

        if (grantType.equalsIgnoreCase("authorization_code")) {
            return createTokenByAuthCode(requestDTO.getCode());
        } else if (grantType.equalsIgnoreCase("refresh_token")) {
            return createTokenByRefreshToken(requestDTO.getRefresh_token());
        }
        throw new IllegalArgumentException("invalid grant_type");
    }

    private TokenResponseDTO createTokenByAuthCode(String authCode) {
        AuthCode loginCode = authCodeRepository.findByAuthCode(authCode)
            .orElseThrow(() -> new RuntimeException("Not Found LoginCode"));

        authCodeRepository.delete(loginCode);

        validateLoginCodeExpireTime(loginCode);

        User user = loginCode.getUser();

        return tokenClient.createToken(user);
    }

    private void validateLoginCodeExpireTime(AuthCode loginCode) {
        long createdMilliSeconds = loginCode.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        if (System.currentTimeMillis() > createdMilliSeconds + LOGIN_CODE_VALID_MILLISECOND) {
            throw new RuntimeException("로그인 코드 유효시간 만료");
        }
    }

    private TokenResponseDTO createTokenByRefreshToken(String refreshToken) {
        long userId = tokenClient.validateRefreshToken(refreshToken);

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("Not Found User"));

        return tokenClient.createToken(user);
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
        String verifyCode = createVerifyCode();

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

        if (checkEmail.getVerifyCode().equals(code)) {
            validateEmailCodeExpireTime(checkEmail);

            checkEmail.verify();
            return new MessageResponseDTO(true, "success");
        }
        return new MessageResponseDTO(false, "fail");
    }

    private void validateEmailCodeExpireTime(CheckEmail checkEmail) {
        long createdAt = checkEmail.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        if (System.currentTimeMillis() > createdAt + CHECK_MAIL_VALID_MILLISECOND) {
            throw new RuntimeException("인증메일 유효시간 만료");
        }
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

            String newPassword = createRandomPassword();

            mailClient.postReissuePasswordMail(email, newPassword);

            user.updatePassword(newPassword);
            user.encodePassword(encoder);

            checkEmailRepository.delete(checkEmail);

            return new MessageResponseDTO(true, "password reset");
        }
        throw new RuntimeException("Email Not Verified OR Verify Code Not Collect");
    }

    private String createRandomPassword() {
        String upperChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerChars = "abcdefghijklmnopqrstuvwxyz";
        String numbers = "0123456789";
        String marks = "!@#$%^&*()-_=+";

        String newWords = createRandomString(upperChars, 3)
            + createRandomString(lowerChars, 3)
            + createRandomString(numbers, 3)
            + createRandomString(marks, 3);

        List<String> newPassword = Arrays.asList(newWords.split(""));
        Collections.shuffle(newPassword);

        return String.join("", newPassword);
    }

    private boolean validateReissueEmail(CheckEmail checkEmail, String verifyCode) {
        return checkEmail.isVerified()
            && checkEmail.getVerifyCode().equals(verifyCode)
            && checkEmail.getType().equals(CheckType.REISSUE);
    }

    private void deleteLegacyLoginCode(long userCode) {
        List<AuthCode> authCodeList = authCodeRepository.findAllByUserCode(userCode);
        authCodeRepository.deleteAll(authCodeList);
    }

    private String createVerifyCode() {
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

    private void deleteLegacyEmailCode(String email) {
        // 이미 이메일에 발송된 코드라면 삭제하고 최신화
        List<CheckEmail> checkEmailList = checkEmailRepository.findAllByEmail(email);
        checkEmailRepository.deleteAll(checkEmailList);
    }

    public DecodedJWT decodeJwt(String token) {
        return tokenClient.decodeAccessJwtByRequestHeader(token);
    }

    public long validateTokenOverParticipantRole(String token) {
        return tokenClient.validateParticipantToken(token);
    }

    public long validateTokenOverManagerRole(String token) {
        return tokenClient.validateManagerToken(token);
    }

    public long validateTokenOverAdminRole(String token) {
        return tokenClient.validateAdminToken(token);
    }
}
