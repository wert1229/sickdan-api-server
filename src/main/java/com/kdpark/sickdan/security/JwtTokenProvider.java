package com.kdpark.sickdan.security;

import com.kdpark.sickdan.domain.Member;
import com.kdpark.sickdan.error.common.ErrorCode;
import com.kdpark.sickdan.error.exception.EntityNotFoundException;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${spring.jwt.secret-key}")
    private String secretKey;

    private static final long ACCESS_TOKEN_VALID_TIME = 60 * 60 * 1000L;
    private static final long REFRESH_TOKEN_VALID_TIME = 7 * 24 * 60 * 60 * 1000L;

    public static final String TYPE_ACCESS = "access";
    public static final String TYPE_REFRESH = "refresh";

    public static final String ACCESS_TOKEN_HEADER = "Authorization";
    public static final String REFRESH_TOKEN_HEADER = "Refresh";

    @PostConstruct
    protected void init() {
        this.secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public String createAccessToken(String userPk, List<String> roles) {
        Claims claims = makeClaim(userPk, roles, TYPE_ACCESS);
        return createToken(claims, ACCESS_TOKEN_VALID_TIME);
    }

    public String createRefreshToken(String userPk, List<String> roles) {
        Claims claims = makeClaim(userPk, roles, TYPE_REFRESH);
        return createToken(claims, REFRESH_TOKEN_VALID_TIME);
    }

    public String createToken(Claims claims, long expiredTime) {
        Date now = new Date();

        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(now.getTime() + expiredTime))
                .setIssuedAt(now)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    private Claims makeClaim(String userPk, List<String> roles, String type) {
        Claims claims = Jwts.claims().setSubject(userPk);
        claims.put("roles", roles);
        claims.put("type", type);

        return claims;
    }

    public Authentication getAuthentication(String token) {
        Map<String, Object> parseInfo = getUserParseInfo(token);
        Member member = Member.builder()
                .id(Long.valueOf((String) parseInfo.get("id")))
                .roles((List) parseInfo.get("authorities"))
                .build();

        return new UsernamePasswordAuthenticationToken(member, "", member.getAuthorities());
    }

    public Map<String, Object> getUserParseInfo(String token) {
        Jws<Claims> parseInfo = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
        Map<String, Object> result = new HashMap<>();
        result.put("id", parseInfo.getBody().getSubject());
        result.put("authorities", parseInfo.getBody().get("roles", List.class));

        return result;
    }

    public Long getUserPk(String token) throws ExpiredJwtException {
        Jws<Claims> parseInfo = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
        return Long.valueOf(parseInfo.getBody().getSubject());
    }

    public String extractToken(HttpServletRequest request) {
        return request.getHeader(ACCESS_TOKEN_HEADER);
    }

    public boolean validateToken(String token) {
        return !isTokenExpired(token);
    }

    public boolean isTokenExpired(String token) {
        try {
            return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token)
                    .getBody()
                    .getExpiration()
                    .before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }
}
