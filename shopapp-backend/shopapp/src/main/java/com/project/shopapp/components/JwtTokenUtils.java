package com.project.shopapp.components;

import com.project.shopapp.exceptions.InvalidParamException;
import com.project.shopapp.model.Token;
import com.project.shopapp.model.User;
import com.project.shopapp.repositories.TokenRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.security.SecureRandom;
import java.util.*;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class JwtTokenUtils {

    @Value("${jwt.expiration}")
    private int expiration; //save to an environment variable

    @Value("${jwt.secretKey}")
    private String secretKey;

    private final TokenRepository tokenRepository;

    public String generationToken(User user) throws Exception {
        //properties => claims
        Map<String, Object> claims = new HashMap<>();
//        this.generateSecretKey();
        claims.put("userId", user.getId());

        // Nếu có số điện thoại thì dùng, nếu không thì dùng email
        String subject = (user.getPhoneNumber() != null && !user.getPhoneNumber().isEmpty())
                ? user.getPhoneNumber()
                : user.getEmail();

        // Lưu cả phoneNumber và email vào claims
        claims.put("phoneNumber", user.getPhoneNumber());
        claims.put("email", user.getEmail());
        try {
            String token = Jwts.builder()
                    .setClaims(claims)
                    .setSubject(subject)
                    .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000L))
                    .signWith(getSignKey(), SignatureAlgorithm.HS256)
                    .compact();
            return token;
        } catch (Exception e) {
            throw new InvalidParamException("Cannot create jwt token, error: "+ e.getMessage());
        }
    }

    private Key getSignKey() {
        byte[] bytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(bytes);
    }

    /**
     * Tạo secret key ngẫu nhiên (chỉ dùng khi cần tạo mới key)
     */
    private String generateSecretKey() {
        SecureRandom random = new SecureRandom();
        byte[] keyBytes = new byte[32];
        random.nextBytes(keyBytes);
        return Encoders.BASE64.encode(keyBytes);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public  <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = this.extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    //check expiration
    public boolean isTokenExpired(String token) {
        Date expirationDate = this.extractClaim(token, Claims::getExpiration);
        return expirationDate.before(new Date());
    }

    /**
     * Lấy số điện thoại từ token (hoặc email nếu không có số điện thoại)
     */
    public String extractSubject(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public boolean validateToken(String token, User user) {
        try {
            String tokenSubject = extractSubject(token);
            String phoneNumber = extractClaim(token, claims -> claims.get("phoneNumber", String.class));
            String email = extractClaim(token, claims -> claims.get("email", String.class));
            Token existingToken = tokenRepository.findByToken(token);
            if (existingToken == null || existingToken.getExpired() || !user.getIsActive()) {
                return false;
            }
            // Kiểm tra token hợp lệ bằng cách so sánh với user
            return ((phoneNumber != null && phoneNumber.equals(user.getPhoneNumber()))
                    || (email != null && email.equals(user.getEmail())))
                    && !isTokenExpired(token);
        } catch (MalformedJwtException e) {
            throw new IllegalArgumentException("Token không hợp lệ: " + e.getMessage());
        } catch (ExpiredJwtException e) {
            throw new IllegalStateException("Token đã hết hạn: " + e.getMessage());
        }
    }
}
