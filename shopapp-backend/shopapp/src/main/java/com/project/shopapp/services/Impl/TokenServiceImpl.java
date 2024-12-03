package com.project.shopapp.services.Impl;

import com.project.shopapp.components.JwtTokenUtils;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.exceptions.TokenExpiredException;
import com.project.shopapp.model.Token;
import com.project.shopapp.model.User;
import com.project.shopapp.repositories.TokenRepository;
import com.project.shopapp.repositories.UserRepository;
import com.project.shopapp.services.TokenService;
import com.project.shopapp.services.UserService;
import com.project.shopapp.ultils.MessageKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    @Value("${jwt.expiration}")
    private int expiration; //save to an environment variable

    @Value("${jwt.expiration-refresh-token}")
    private int expirationRefreshToken;

    private final UserService userService;
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final JwtTokenUtils jwtTokenUtil;
    int MAX_TOKENS = 3;

    @Transactional
    @Override
    public Token addToken(User user, String token, boolean isMobileDevice) {
        List<Token> userTokens = tokenRepository.findByUser(user);
        int tokenCount = userTokens.size();
        //Số lượng token vượt quá giới hạn, xóa một token cũ
        if(tokenCount >= MAX_TOKENS) {
            // Kiểm tra xem trong danh sách userTokens có tồn tại ít nhất
            // một token không phải là thiết bị di động (non-mobile)
            boolean hasNonMobileToken = !userTokens.stream().allMatch(Token::getIsMobile);
            Token tokenToDelete;
            if (hasNonMobileToken) {
                tokenToDelete = userTokens.stream()
                        .filter(userToken -> !userToken.getIsMobile())
                        .findFirst()
                        .orElse(userTokens.get(0));
            } else {
                //tất cả các token đều là thiết bị di động.
                // chúng ta sẽ xóa token đầu tiên trong danh sách
                tokenToDelete = userTokens.get(0);
            }
            tokenRepository.delete(tokenToDelete);
        }
        long expirationInSeconds = expiration;
        LocalDateTime expirationDateTime = LocalDateTime.now().plusSeconds(expirationInSeconds);
        //Tạo mới một token cho người dùng
        Token newToken = Token.builder()
                .user(user)
                .token(token)
                .revoked(false)
                .expired(false)
                .tokenType("Bearer")
                .expirationDate(expirationDateTime)
                .isMobile(isMobileDevice)
                .build();
        newToken.setRefreshToken(UUID.randomUUID().toString());
        newToken.setRefreshExpirationDate(LocalDateTime.now().plusSeconds(expirationRefreshToken));
        tokenRepository.save(newToken);
        return newToken;
    }

    @Transactional
    @Override
    public Token refreshToken(String refreshToken, User user) throws Exception {
        // Tìm token theo refreshToken
        Token existingToken = tokenRepository.findByRefreshToken(refreshToken);
        if (existingToken == null) {
            throw new Exception("Invalid refresh token");
        }

        // Kiểm tra xem refresh token có còn hạn không
        if (existingToken.getRefreshExpirationDate().isBefore(LocalDateTime.now())) {
            throw new TokenExpiredException("Refresh token has expired");
        }

        // Cập nhật token
        long expirationInSeconds = expiration;
        LocalDateTime newAccessTokenExpiration = LocalDateTime.now().plusSeconds(expirationInSeconds); // Thời hạn mới

        String newRefreshToken = UUID.randomUUID().toString(); // Sinh refresh token mới
        LocalDateTime newRefreshTokenExpiration = LocalDateTime.now().plusSeconds(expirationRefreshToken); // Thời hạn refresh mới

        // Cập nhật thông tin token cũ
        existingToken.setToken(jwtTokenUtil.generationToken(existingToken.getUser()));
        existingToken.setExpirationDate(newAccessTokenExpiration);
        existingToken.setRefreshToken(newRefreshToken);
        existingToken.setRefreshExpirationDate(newRefreshTokenExpiration);
        existingToken.setRevoked(false); // Đảm bảo token không bị thu hồi
        existingToken.setExpired(false); // Đảm bảo token không hết hạn

        // Lưu thay đổi vào cơ sở dữ liệu
        tokenRepository.save(existingToken);

        return existingToken; // Trả về token đã được cập nhật
    }


}
