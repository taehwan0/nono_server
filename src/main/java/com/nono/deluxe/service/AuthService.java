package com.nono.deluxe.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.nono.deluxe.domain.user.User;
import com.nono.deluxe.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {

    @Value("${auth.key}")
    private String key;
    @Value("${auth.issuer}")
    private String issuer;

    private final UserRepository userRepository;

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
        return getToken(user.getName(), user.getId(), tokenActiveSeconds);
    }

    /**
     * 토큰을 생성하고 반환함
     * @param username
     * @param tokenActiveSeconds
     * @return
     */
    public String getToken(String username, long userId, long tokenActiveSeconds) {
        Algorithm algorithm = getAlgorithm(key);
        return JWT.create()
                .withExpiresAt(new Date(System.currentTimeMillis() + (1000 * tokenActiveSeconds)))
                .withIssuer(issuer)
                .withClaim("username", username)
                .withClaim("userId", userId)
                .sign(algorithm);
    }

    /**
     * 유효한 유저인지 확인
     * @param token
     */
    public void isUser(String token) {
        Algorithm algorithm = getAlgorithm(key);
        JWTVerifier verifier = getVerifier(algorithm);
        try {
            String extractedToken = extractToken(token);
            DecodedJWT decodedJWT = verifier.verify(extractedToken);
            long userId = Long.parseLong(decodedJWT.getClaim("userId").toString());
            userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Not Found User"));
            log.info("user Login : {}", decodedJWT.getClaim("username").toString());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("invalid token");
        }
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
            throw new RuntimeException("is not bearer token");
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
