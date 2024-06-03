package com.project.shopapp.services;

import com.project.shopapp.dtos.UserDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.model.User;

public interface UserService {
    User createUser(UserDTO userDTO) throws DataNotFoundException;
    String login(String phoneNumber, String password) throws Exception;
}
