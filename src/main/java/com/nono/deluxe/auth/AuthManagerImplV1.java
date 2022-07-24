package com.nono.deluxe.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class AuthManagerImplV1 implements AuthManager{

    // 여기가 먼저 받아져야 하는데 그렇지 않은 듯
    @Value("${auth.key}")
    private String key;
    @Value("${auth.issuer}")
    private String issuer;





    // token 시간 입력 받아 생성해서 리턴하기
    @Override
    public String getToken(String username, long tokenActiveSeconds) {
        Algorithm algorithm = getAlgorithm(key);
        return JWT.create()
                .withExpiresAt(new Date(System.currentTimeMillis() + (1000 * tokenActiveSeconds)))
                .withIssuer(issuer)
                .withClaim("username", username)
                .sign(algorithm);
    }

    //  token 검사하고 권한 확인하기
    @Override
    public void isUser(String token) {
        Algorithm algorithm = getAlgorithm(key);
        JWTVerifier verifier = getVerifier(algorithm);
        try {
            DecodedJWT decodedJWT = verifier.verify(token);
            log.info(decodedJWT.getClaim("username").toString());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("token is expired");
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
