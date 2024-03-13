package com.cringe.books;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtMain {
    @Value("${secretKey}")
    private String secretKey;
    @Value("${tokenLifetime}")
    private long lifetime;

    public String createJwt(String id) {
        Key key = getKeyFromString();
        return Jwts.builder()
                .subject(id)
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + lifetime))
                .signWith(key)
                .compact();
    }

    public String verifyToken(String jwt) throws IllegalArgumentException, JwtException {
        SecretKey key = (SecretKey) getKeyFromString();
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(jwt)
                .getPayload()
                .getSubject();
    }

    protected Key getKeyFromString() {
        byte[] secretBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return new SecretKeySpec(secretBytes, "HmacSHA256");
    }

    public String getCookieName() {
        return "Session";
        //TODO
//        return "__Host-session";
    }

}
