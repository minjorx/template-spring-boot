package com.minjor.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class JwtService {

    @Value("${app.auth.jwt.secret:defaultSecretKey}")
    private String secret;

    @Value("${app.auth.jwt.access-expiration:3600}") // 1 hour in seconds
    private Long accessExpiration;

    @Value("${app.auth.jwt.refresh-expiration:604800}") // 7 days in seconds
    private Long refreshExpiration;

    /**
     * 生成访问令牌
     */
    public String generateAccessToken(UserDetails userDetails) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + accessExpiration * 1000);

        return JWT.create()
                .withSubject(userDetails.getUsername())
                .withIssuedAt(now)
                .withExpiresAt(expiryDate)
                .withClaim("authorities", getAuthority(userDetails))
                .withClaim("token_type", "access")
                .sign(Algorithm.HMAC512(secret));
    }

    /**
     * 生成刷新令牌
     */
    public String generateRefreshToken(UserDetails userDetails) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshExpiration * 1000);

        return JWT.create()
                .withSubject(userDetails.getUsername())
                .withIssuedAt(now)
                .withExpiresAt(expiryDate)
                .withClaim("token_type", "refresh")
                .sign(Algorithm.HMAC512(secret));
    }

    /**
     * 刷新访问令牌
     */
    public String refreshToken(String refreshToken, UserDetails userDetails) {
        if (validateRefreshToken(refreshToken, userDetails)) {
            return generateAccessToken(userDetails);
        }
        throw new JWTVerificationException("Invalid refresh token");
    }

    /**
     * 验证访问令牌
     */
    public Boolean validateAccessToken(String token, UserDetails userDetails) {
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC512(secret))
                    .withSubject(userDetails.getUsername())
                    .withClaim("token_type", "access")
                    .build();

            DecodedJWT decodedJWT = verifier.verify(token);
            return !decodedJWT.getExpiresAt().before(new Date());
        } catch (JWTVerificationException e) {
            return false;
        }
    }

    /**
     * 验证刷新令牌
     */
    public Boolean validateRefreshToken(String token, UserDetails userDetails) {
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC512(secret))
                    .withSubject(userDetails.getUsername())
                    .withClaim("token_type", "refresh")
                    .build();

            DecodedJWT decodedJWT = verifier.verify(token);
            return !decodedJWT.getExpiresAt().before(new Date());
        } catch (JWTVerificationException e) {
            return false;
        }
    }

    /**
     * 从令牌中提取用户名
     */
    public String extractUsername(String token) {
        DecodedJWT decodedJWT = JWT.decode(token);
        return decodedJWT.getSubject();
    }

    /**
     * 从访问令牌中提取角色
     */
    public List<String> extractRoles(String token) {
        DecodedJWT decodedJWT = JWT.decode(token);
        String rolesString = decodedJWT.getClaim("authorities").asString();
        if (rolesString != null) {
            return Arrays.asList(rolesString.split(","));
        }
        return new ArrayList<>();
    }

    /**
     * 检查令牌是否即将过期（默认30秒内）
     */
    public boolean isTokenExpiringSoon(String token, int secondsThreshold) {
        DecodedJWT decodedJWT = JWT.decode(token);
        Date expiresAt = decodedJWT.getExpiresAt();
        Date now = new Date();
        long timeToExpire = expiresAt.getTime() - now.getTime();
        return timeToExpire < secondsThreshold * 1000L;
    }

    /**
     * 获取用户权限
     */
    private String getAuthority(UserDetails userDetails) {
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        List<String> roles = new ArrayList<>();
        for (GrantedAuthority authority : authorities) {
            roles.add(authority.getAuthority());
        }
        return String.join(",", roles);
    }

    /**
     * 获取访问令牌过期时间
     */
    public Date getAccessExpirationDateFromToken(String token) {
        return JWT.decode(token).getExpiresAt();
    }

    /**
     * 获取刷新令牌过期时间
     */
    public Date getRefreshExpirationDateFromToken(String token) {
        return JWT.decode(token).getExpiresAt();
    }
}
