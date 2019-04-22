package com.thienan.lovebox.controller;

import com.thienan.lovebox.payload.response.UserAvailabilityResponse;
import com.thienan.lovebox.payload.response.UserDetailResponse;
import com.thienan.lovebox.service.UserService;
import com.thienan.lovebox.shared.dto.UserDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/{id}")
    public UserDetailResponse getUser(@PathVariable("id") Long id) {
        UserDto userDto = userService.getUserById(id);

        ModelMapper modelMapper = new ModelMapper();
        UserDetailResponse userDetailResponse = modelMapper.map(userDto, UserDetailResponse.class);

        return userDetailResponse;
    }

    @GetMapping("/check-username")
    public UserAvailabilityResponse checkUsernameAvailability(@RequestParam(value = "username") String username) {
        Boolean isAvailable = userService.checkUsernameAvailability(username);
        return new UserAvailabilityResponse(isAvailable);
    }

    @GetMapping("/check-email")
    public UserAvailabilityResponse checkEmailAvailability(@RequestParam(value = "email") String email) {
        Boolean isAvailable = userService.checkEmailAvailability(email);
        return new UserAvailabilityResponse(isAvailable);
    }
}
