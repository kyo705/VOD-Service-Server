package com.ktube.vod.identification;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;
import java.util.Map;

public class JwtUtils {

    public static String create(String secret, String subject, long expirationTimeMs, Map<String, String> claims) {

        JWTCreator.Builder builder = JWT.create();

        builder.withSubject(subject)
                .withExpiresAt(new Date(System.currentTimeMillis() + expirationTimeMs));

        for(Map.Entry<String, String> claim : claims.entrySet()) {
            builder.withClaim(claim.getKey(), claim.getValue());
        }
        return builder.sign(Algorithm.HMAC256(secret));
    }

    public static DecodedJWT verify(String jwt, String secret) {

        return JWT.require(Algorithm.HMAC256(secret))
                .build()
                .verify(jwt);
    }

    public static String getSubject(DecodedJWT decodedJWT) {

        return decodedJWT.getSubject();
    }

    public static Date getExpireDate(DecodedJWT decodedJWT) {

        return decodedJWT.getExpiresAt();
    }

     public static String getClaim(DecodedJWT decodedJWT, String key) {

        return decodedJWT.getClaim(key).asString();
    }
}
