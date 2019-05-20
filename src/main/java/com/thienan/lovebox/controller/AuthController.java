package com.thienan.lovebox.controller;

import com.thienan.lovebox.exception.BadRequestException;
import com.thienan.lovebox.payload.request.UserChangePasswordRequest;
import com.thienan.lovebox.payload.request.UserSignInRequest;
import com.thienan.lovebox.payload.request.UserSignUpRequest;
import com.thienan.lovebox.payload.request.UserUpdateRequest;
import com.thienan.lovebox.payload.response.JwtAuthenticationResponse;
import com.thienan.lovebox.payload.response.UserResponse;
import com.thienan.lovebox.security.CurrentUser;
import com.thienan.lovebox.security.UserPrincipal;
import com.thienan.lovebox.service.UserService;
import com.thienan.lovebox.shared.dto.UserDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    UserService userService;

    @Autowired
    ModelMapper modelMapper;

    @PostMapping("/sign-in")
    public JwtAuthenticationResponse signIn(@Valid @RequestBody UserSignInRequest userSignInRequest) {
        String jwt = userService.authenticateUser(
                userSignInRequest.getUsernameOrEmail(),
                userSignInRequest.getPassword()
        );

        return new JwtAuthenticationResponse(jwt);
    }

    @PostMapping("/sign-up")
    public UserResponse signUp(@Valid @RequestBody UserSignUpRequest userSignUpRequest) {
        if (!userService.checkUsernameAvailability(userSignUpRequest.getUsername())) {
            throw new BadRequestException("Username already taken");
        }

        if (!userService.checkEmailAvailability(userSignUpRequest.getEmail())) {
            throw new BadRequestException("Email address already in use");
        }

        UserDto userDto = modelMapper.map(userSignUpRequest, UserDto.class);
        UserDto createdUser = userService.createUser(userDto);

        return mapToUserResponse(createdUser);
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public UserResponse getCurrentUser(@CurrentUser UserPrincipal currentUser) {
        UserDto userDto = userService.getUserById(currentUser.getId());
        return mapToUserResponse(userDto);
    }

    @PutMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public UserResponse updateUserInfo(@CurrentUser UserPrincipal currentUser,
                                       @RequestBody UserUpdateRequest userUpdateRequest) {
        UserDto userDto = modelMapper.map(userUpdateRequest, UserDto.class);
        UserDto updatedUserDto = userService.updateUser(currentUser.getId(), userDto);
        return mapToUserResponse(updatedUserDto);
    }

    @PutMapping("/me/password")
    @PreAuthorize("hasRole('USER')")
    public UserResponse changeUserPassword(@CurrentUser UserPrincipal currentUser,
                                           @RequestBody UserChangePasswordRequest userChangePasswordRequest) {
        UserDto updatedUserDto = userService.changeUserPassword(currentUser.getId(), userChangePasswordRequest.getNewPassword());
        return mapToUserResponse(updatedUserDto);
    }

    @PostMapping("/me/photo")
    @PreAuthorize("hasRole('USER')")
    public UserResponse changeUserPhoto(@CurrentUser UserPrincipal currentUser,
                                        @RequestParam("file") MultipartFile multipartFile) {
        UserDto updatedUserDto = userService.changeUserPhoto(currentUser.getId(), multipartFile);
        return mapToUserResponse(updatedUserDto);
    }

    private UserResponse mapToUserResponse(UserDto userDto) {
        return modelMapper.map(userDto, UserResponse.class);
    }
}
