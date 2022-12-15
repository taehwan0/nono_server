package com.nono.deluxe.application;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.nono.deluxe.domain.user.Role;
import com.nono.deluxe.domain.user.User;
import com.nono.deluxe.exception.InvalidTokenException;
import com.nono.deluxe.exception.NoAuthorityException;
import com.nono.deluxe.presentation.dto.auth.TokenResponseDTO;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TokenClient {

    private static final long accessTokenExpireTime = 1000L * 60 * 60 * 2;
    private static final long refreshTokenExpireTime = 1000L * 60 * 60 * 24 * 30;

    private final String accessKey;
    private final String refreshKey;
    private final String issuer;

    public TokenClient(
        @Value("${auth.accessKey}") String accessKey,
        @Value("${auth.refreshKey}") String refreshKey,
        @Value("${auth.issuer}") String issuer) {
        this.accessKey = accessKey;
        this.refreshKey = refreshKey;
        this.issuer = issuer;
    }

    /**
     * access Token 유효시간 2시간, refresh Token 유효시간 30일
     */
    public TokenResponseDTO createToken(User user) {
        String accessToken = createAccessToken(user.getName(), user.getId(), user.getRole());
        String refreshToken = createRefreshToken(user.getName(), user.getId(), user.getRole());

        TokenResponseDTO responseDTO = new TokenResponseDTO();
        responseDTO.setToken_type("bearer");
        responseDTO.setAccess_token(accessToken);
        responseDTO.setRefresh_token(refreshToken);
        responseDTO.setExpires_in(decodeJwt(accessToken, accessKey).getExpiresAt().getTime());
        responseDTO.setRefresh_token_expires_in(decodeJwt(refreshToken, refreshKey).getExpiresAt().getTime());

        return responseDTO;
    }

    private String createAccessToken(String username, long userId, Role userRole) {
        log.info("AccessToken Created By: {}", userId);
        Algorithm algorithm = getAlgorithm(accessKey);
        return JWT.create().withIssuedAt(new Date())
            .withExpiresAt(new Date(System.currentTimeMillis() + (accessTokenExpireTime))).withIssuer(issuer)
            .withClaim("username", username).withClaim("userId", userId).withClaim("ROLE", userRole.toString())
            .sign(algorithm);
    }

    private String createRefreshToken(String username, long userId, Role userRole) {
        log.info("RefreshToken Created By: {}", userId);
        Algorithm algorithm = getAlgorithm(refreshKey);
        return JWT.create().withIssuedAt(new Date())
            .withExpiresAt(new Date(System.currentTimeMillis() + (refreshTokenExpireTime))).withIssuer(issuer)
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

    public long validateAdminToken(String token) {
        return validateAdminRole(decodeAccessJwtByRequestHeader(token));
    }

    public long validateManagerToken(String token) {
        return validateManagerRole(decodeAccessJwtByRequestHeader(token));
    }

    public long validateParticipantToken(String token) {
        return validateParticipantRole(decodeAccessJwtByRequestHeader(token));
    }

    public DecodedJWT decodeAccessJwtByRequestHeader(String token) {
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
}
