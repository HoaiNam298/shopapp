package com.project.shopapp.repositories;

import com.project.shopapp.model.Token;
import com.project.shopapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TokenRepository extends JpaRepository<Token, Long> {
    Token findByToken(String token);
    List<Token> findByUser(User user);
}
