package com.thienan.lovebox.service;

import com.thienan.lovebox.shared.dto.UserDto;

public interface UserService {

    String authenticateUser(String usernameOrEmail, String password);

    UserDto createUser(UserDto user);

    UserDto getUserById(Long id);
}
