package com.project.shopapp.services;

import com.project.shopapp.model.Token;
import com.project.shopapp.model.User;

public interface TokenService {
    Token addToken(User user, String token, boolean isMobileDevice);
    Token refreshToken(String refreshToken, User user) throws Exception;
}
