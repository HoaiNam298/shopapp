package com.project.shopapp.services.Impl;

import com.project.shopapp.model.Token;
import com.project.shopapp.model.User;
import com.project.shopapp.repositories.TokenRepository;
import com.project.shopapp.services.TokenService;
import com.project.shopapp.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    @Value("${jwt.expiration}")
    private int expiration; //save to an environment variable
    private final UserService userService;
    private final TokenRepository tokenRepository;
    int MAX_TOKENS = 3;

    @Transactional
    @Override
    public void addToken(User user, String token, Boolean isMobileDevice) {
        List<Token> userTokens = tokenRepository.findByUser(user);
        int tokenCount = userTokens.size();
        //Số lượng token vượt quá giới hạn, xóa một token cũ
        if(tokenCount >= MAX_TOKENS) {
            // Kiểm tra xem trong danh sách userTokens có tồn tại ít nhất
            // một token không phải là thiết bị di động (non-mobile)
            boolean hasNonMobileToken = !userTokens.stream().allMatch(Token::getIsMobile);
            Token tokenToDetete;
            if (hasNonMobileToken) {
                tokenToDetete = userTokens.stream()
                        .filter(userToken -> !userToken.getIsMobile())
                        .findFirst()
                        .orElse(userTokens.get(0));
            } else {
                //tất cả các token đều là thiết bị di động.
                // chúng ta sẽ xóa token đầu tiên trong danh sách
                tokenToDetete = userTokens.get(0);
            }
            tokenRepository.delete(tokenToDetete);
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
        tokenRepository.save(newToken);
    }
}
