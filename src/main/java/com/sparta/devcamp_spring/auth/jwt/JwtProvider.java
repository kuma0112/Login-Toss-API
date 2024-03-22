package com.sparta.devcamp_spring.auth.jwt;

import com.sparta.devcamp_spring.auth.entity.UserRole;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.MacAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Component
@Slf4j
public class JwtProvider {
    // Header KEY 값
    public static final String ACCESS_TOKEN_HEADER = "AccessToken";
    public static final String REFRESH_TOKEN_HEADER = "RefreshToken";
    // Token 식별자
    public static final String BEARER_PREFIX = "Bearer ";
    // Access 토큰 만료시간
    private final long ACCESS_TOKEN_TIME = 60 * 60 * 1000L; // 60분
    // Refresh 토큰 만료시간
    private final long REFRESH_TOKEN_TIME = 60 * 60 * 24 * 7 * 1000L; // 7일

    @Value("${jwt.secret.key}")
    private String secretKey; // Base64 Encode: Secret Key
    private SecretKey key;
    private MacAlgorithm algorithm;

    @PostConstruct
    public void init() {
        key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
        algorithm = Jwts.SIG.HS256;
    }

    public TokenPayload createTokenPayload(String email, UserRole role, TokenType tokenType)  {
        Date date = new Date();
        long tokenTime = TokenType.ACCESS.equals(tokenType) ? ACCESS_TOKEN_TIME : REFRESH_TOKEN_TIME;
        return new TokenPayload(
                email,
                UUID.randomUUID().toString(),
                date,
                new Date(date.getTime() + tokenTime),
                role
        );
    }

    public String createToken(TokenPayload payload) {
        return BEARER_PREFIX +
                Jwts.builder()
                        .subject(payload.getSubject()) // 사용자 식별자값(ID)
                        .claim(ACCESS_TOKEN_HEADER, payload.getRole()) // 사용자 권한
                        .expiration(payload.getExpiresAt()) // 만료 시간
                        .issuedAt(payload.getIssuedAt()) // 발급일
                        .id(payload.getJwtId()) // JWT ID
                        .signWith(key, algorithm) // 암호화 Key & 알고리즘
                        .compact();
    }

    /**
     * HTTP Header 에서 JWT 추출
     * @param request : HTTP Request 정보
     * @return Header 에서 추출한 JWT
     */
    public String getJwtFromHeader(HttpServletRequest request, TokenType tokenType) {
        if(Objects.equals(TokenType.ACCESS, tokenType)) {
            String bearerToken = request.getHeader(ACCESS_TOKEN_HEADER);
            if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
                return bearerToken.substring(7);
            }
        }
        return null;
    }

    public String getTokenFromRequest(HttpServletRequest request, TokenType tokenType) {
        if(Objects.equals(TokenType.REFRESH, tokenType)) {
            String bearerToken = Arrays.toString(request.getCookies());
            if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
                return bearerToken.substring(7);
            }
        }
        return null;
    }

        public String addBearer(String token){
        return BEARER_PREFIX + token;
    }

    //생성된 JWT를 Cookie에 저장
    public Cookie addJwtToCookie(String token, String tokenName) {
        token = addBearer(token);
        token = URLEncoder.encode(token, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        Cookie cookie = new Cookie(tokenName, token);

        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setAttribute("SameSite", "None");

        if (tokenName.equals(JwtProvider.REFRESH_TOKEN_HEADER)) {
            cookie.setMaxAge(7 * 24 * 3600);
        }

        return cookie;
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.error("Invalid JWT signature. 유효하지 않는 JWT 서명입니다.", e);
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token. 만료된 JWT 토큰입니다.", e);
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token. 지원되지 않는 JWT 토큰입니다.", e);
        } catch (IllegalArgumentException e) {
            log.error("JWT token is compacted of the wrong type or is incorrectly constructed. JWT 토큰이 잘못된 타입으로 압축되었거나 잘못 구성되었습니다.", e);
        } catch (Exception e) {
            // 일반적인 예외를 잡아서 처리합니다. 이는 예상치 못한 다른 종류의 예외를 처리하기 위함입니다.
            log.error("An error occurred while processing the JWT Token. JWT 토큰 처리 중 오류가 발생했습니다.", e);
        }
        return false;
    }

    public Claims getUserInfoFromToken(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }
}
