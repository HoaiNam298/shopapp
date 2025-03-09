package com.project.shopapp.services;

import com.project.shopapp.dtos.UpdateUserDTO;
import com.project.shopapp.dtos.UserDTO;
import com.project.shopapp.dtos.UserLoginDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.exceptions.InvalidPasswordException;
import com.project.shopapp.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    User createUser(UserDTO userDTO) throws Exception;
    String login(UserLoginDTO userLoginDTO) throws Exception;
    User getUserDetailsFromToken(String token) throws Exception;
    User updateUser(Long userId, UpdateUserDTO updatedUserDTO) throws Exception;
    User getUserDetailsFromRefreshToken(String token) throws Exception;
    Page<User> findAllUser(String keyword, Pageable pageable) throws Exception;
    void resetPassword(Long userId, String newPassword) throws InvalidPasswordException, DataNotFoundException;
    public void blockOrEnable(Long userId, Boolean active) throws DataNotFoundException;
}
