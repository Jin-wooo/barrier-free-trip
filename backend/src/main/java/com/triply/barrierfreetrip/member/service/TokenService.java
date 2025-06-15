package com.triply.barrierfreetrip.member.service;

import com.triply.barrierfreetrip.member.domain.Token;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TokenService {
    private String secretKey = "testSecretKey202301125testSecretKey202301125testSecretKey202301125";
    private final OauthMemberService memberService;

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public Token generateToken(String email, List<String> roles) {
        //long accessTokenPeriod = 1000L * 60L * 60L * 24L * 30L;    // 한 달
        long accessTokenPeriod = 1000L * 60L * 60L; // 60분
        long refreshPeriod = 1000L * 60L * 60L * 24L * 30L * 3L;    // 한 달

        Claims claims = Jwts.claims().setSubject(email);
        claims.put("roles", roles);

        Date now = new Date();

        String accessToken = Jwts.builder()
                .setClaims(claims)  // save info
                .setIssuedAt(now)   // token generated time info
                .setExpiration(new Date(now.getTime() + accessTokenPeriod)) // set expire time
                .signWith(SignatureAlgorithm.HS256, secretKey)  // using encryption algorithm and set secret value
                .compact();

        String refreshToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + refreshPeriod))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        return Token.builder().accessToken(accessToken).refreshToken(refreshToken).keyEmail(email).build();
    }

    public boolean verifyToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token);

            return claims.getBody()
                    .getExpiration().after(new Date());

        } catch (ExpiredJwtException e) {
            throw new JwtException("토큰 기한 만료");
        } // 후에 예외 추가 필요
    }

    // search authentication
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = memberService.loadUserByUsername(this.getUserPK(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // get member info from token
    public String getUserPK(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token)
                .getBody().getSubject();
    }

    // extract token from header
    public String extractToken(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            return header.substring("Bearer ".length());
        } else {
            return null;
        }
    }
}
