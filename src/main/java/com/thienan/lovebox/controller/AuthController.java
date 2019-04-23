package com.thienan.lovebox.controller;

import com.thienan.lovebox.payload.request.UserSignInRequest;
import com.thienan.lovebox.payload.request.UserSignUpRequest;
import com.thienan.lovebox.payload.response.ApiResponse;
import com.thienan.lovebox.payload.response.JwtAuthenticationResponse;
import com.thienan.lovebox.payload.response.UserDetailResponse;
import com.thienan.lovebox.security.CurrentUser;
import com.thienan.lovebox.security.UserPrincipal;
import com.thienan.lovebox.service.UserService;
import com.thienan.lovebox.shared.dto.UserDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    UserService userService;

    @PostMapping("/sign-in")
    public ResponseEntity<?> signIn(@Valid @RequestBody UserSignInRequest userSignInRequest) {
        String jwt = userService.authenticateUser(
                userSignInRequest.getUsernameOrEmail(),
                userSignInRequest.getPassword()
        );

        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@Valid @RequestBody UserSignUpRequest userSignUpRequest) {
        if (!userService.checkUsernameAvailability(userSignUpRequest.getUsername())) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Username already taken"));
        }

        if (!userService.checkEmailAvailability(userSignUpRequest.getEmail())) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Email address already in use"));
        }

        ModelMapper modelMapper = new ModelMapper();
        UserDto userDto = modelMapper.map(userSignUpRequest, UserDto.class);

        UserDto createdUser = userService.createUser(userDto);
        UserDetailResponse userDetailResponse = modelMapper.map(createdUser, UserDetailResponse.class);

        return ResponseEntity.ok(userDetailResponse);
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public UserDetailResponse getCurrentUser(@CurrentUser UserPrincipal currentUser) {
        ModelMapper modelMapper = new ModelMapper();
        UserDetailResponse userDetailResponse = modelMapper.map(currentUser, UserDetailResponse.class);

        return userDetailResponse;
    }
}
