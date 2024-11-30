package com.project.shopapp.services;

import com.project.shopapp.model.User;

public interface TokenService {
    void addToken(User user, String token, Boolean isMobileDevice);

}
