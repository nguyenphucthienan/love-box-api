package com.thienan.lovebox.controller;

import com.thienan.lovebox.payload.response.UserDetailResponse;
import com.thienan.lovebox.service.UserService;
import com.thienan.lovebox.shared.dto.UserDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserDetailResponse> getUser(@PathVariable("id") Long id) {
        UserDto userDto = userService.getUserById(id);

        ModelMapper modelMapper = new ModelMapper();
        UserDetailResponse userDetailResponse = modelMapper.map(userDto, UserDetailResponse.class);

        return ResponseEntity.ok(userDetailResponse);
    }
}
