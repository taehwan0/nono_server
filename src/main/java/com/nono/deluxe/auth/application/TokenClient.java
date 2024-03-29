package com.nono.deluxe.auth.application;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.nono.deluxe.auth.presentation.dto.auth.TokenResponseDTO;
import com.nono.deluxe.common.exception.InvalidTokenException;
import com.nono.deluxe.common.exception.NoAuthorityException;
import com.nono.deluxe.common.exception.NotFoundException;
import com.nono.deluxe.user.domain.Role;
import com.nono.deluxe.user.domain.User;
import com.nono.deluxe.user.domain.repository.UserRepository;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TokenClient {

    private static final String TOKEN_TYPE = "bearer";
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000L * 60 * 60 * 2;
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000L * 60 * 60 * 24 * 30;

    private final String accessKey;
    private final String refreshKey;
    private final String issuer;
    private final UserRepository userRepository;

    public TokenClient(
        @Value("${auth.accessKey}") String accessKey,
        @Value("${auth.refreshKey}") String refreshKey,
        @Value("${auth.issuer}") String issuer,
        UserRepository userRepository) {
        this.accessKey = accessKey;
        this.refreshKey = refreshKey;
        this.issuer = issuer;
        this.userRepository = userRepository;
    }

    /**
     * access Token 유효시간 2시간, refresh Token 유효시간 30일
     */
    public TokenResponseDTO createToken(User user) {
        if (!user.isActive()) {
            throw new IllegalStateException("Not Active User");
        }
        String accessToken = createAccessToken(user.getName(), user.getId(), user.getRole());
        String refreshToken = createRefreshToken(user.getName(), user.getId(), user.getRole());

        return new TokenResponseDTO(
            TOKEN_TYPE,
            accessToken,
            decodeJwt(accessToken, accessKey).getExpiresAt().getTime(),
            refreshToken,
            decodeJwt(refreshToken, refreshKey).getExpiresAt().getTime()
        );
    }

    private String createAccessToken(String username, long userId, Role userRole) {
        log.info("AccessToken Created By: {}", userId);
        Algorithm algorithm = getAlgorithm(accessKey);
        return JWT.create().withIssuedAt(new Date())
            .withExpiresAt(new Date(System.currentTimeMillis() + (ACCESS_TOKEN_EXPIRE_TIME))).withIssuer(issuer)
            .withClaim("username", username).withClaim("userId", userId).withClaim("ROLE", userRole.toString())
            .sign(algorithm);
    }

    private String createRefreshToken(String username, long userId, Role userRole) {
        log.info("RefreshToken Created By: {}", userId);
        Algorithm algorithm = getAlgorithm(refreshKey);
        return JWT.create().withIssuedAt(new Date())
            .withExpiresAt(new Date(System.currentTimeMillis() + (REFRESH_TOKEN_EXPIRE_TIME))).withIssuer(issuer)
            .withClaim("username", username).withClaim("userId", userId).withClaim("ROLE", userRole.toString())
            .sign(algorithm);
    }

    public long validateRefreshToken(String refreshToken) {
        JWTVerifier verifier = getVerifier(refreshKey);
        DecodedJWT decodedJwt = verifier.verify(refreshToken);
        return getUserIdByDecodedToken(decodedJwt);
    }

    private long getUserIdByDecodedToken(DecodedJWT jwt) {
        return Long.parseLong(jwt.getClaim("userId").toString());
    }

    private DecodedJWT decodeJwt(String token, String key) {
        JWTVerifier verifier = getVerifier(key);
        try {
            return verifier.verify(token);
        } catch (Exception e) {
            throw new RuntimeException("invalid token");
        }
    }

    private long validateAdminToken(String token) {
        return validateAdminRole(decodeAccessJwtByRequestHeader(token));
    }

    private long validateManagerToken(String token) {
        return validateManagerRole(decodeAccessJwtByRequestHeader(token));
    }

    private long validateParticipantToken(String token) {
        return validateParticipantRole(decodeAccessJwtByRequestHeader(token));
    }

    private DecodedJWT decodeAccessJwtByRequestHeader(String token) {
        String extractedToken = extractToken(token);

        return getVerifier(accessKey).verify(extractedToken);
    }

    private String extractToken(String token) {
        if (token.matches("(^([Bb]earer) [A-Za-z0-9-_]*\\.[A-Za-z0-9-_]*\\.[A-Za-z0-9-_]*$)")) {
            return token.split(" ")[1];
        } else {
            throw new InvalidTokenException("InvalidTokenForm");
        }
    }

    private JWTVerifier getVerifier(String key) {
        return JWT.require(Algorithm.HMAC256(key)).withIssuer(issuer).build();
    }

    private Algorithm getAlgorithm(String key) {
        return Algorithm.HMAC256(key);
    }

    private long validateAdminRole(DecodedJWT jwt) {
        if (!isAdmin(jwt)) {
            throw new NoAuthorityException("Forbidden API");
        }
        return getUserIdByDecodedToken(jwt);
    }

    private long validateManagerRole(DecodedJWT jwt) {
        if (!isManager(jwt) && !isAdmin(jwt)) {
            throw new NoAuthorityException("Forbidden API");
        }
        return getUserIdByDecodedToken(jwt);
    }

    private long validateParticipantRole(DecodedJWT jwt) {
        if (!isParticipant(jwt) && !isManager(jwt) && !isAdmin(jwt)) {
            throw new NoAuthorityException("Forbidden API");
        }
        return getUserIdByDecodedToken(jwt);
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

    public boolean isActiveParticipant(String token) {
        return isActiveUser(validateParticipantToken(token));
    }

    public boolean isActiveManager(String token) {
        return isActiveUser(validateManagerToken(token));
    }

    public boolean isActiveAdmin(String token) {
        return isActiveUser(validateAdminToken(token));
    }

    private boolean isActiveUser(long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("Not Found User"));

        return user.isActive();
    }

    public long getUserIdByToken(String token) {
        return getUserIdByDecodedToken(decodeAccessJwtByRequestHeader(token));
    }
}
