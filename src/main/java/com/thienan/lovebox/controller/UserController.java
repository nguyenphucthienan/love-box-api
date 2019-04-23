package com.thienan.lovebox.controller;

import com.thienan.lovebox.payload.response.ApiResponse;
import com.thienan.lovebox.payload.response.UserAvailabilityResponse;
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

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/{id}")
    public UserDetailResponse getUser(@PathVariable("id") Long id) {
        UserDto userDto = userService.getUserById(id);

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.typeMap(UserDto.class, UserDetailResponse.class)
                .addMappings(mapper -> mapper.map(UserDto::getBffDetail, UserDetailResponse::setBffDetail));

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

    @PostMapping("{id}/follow")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> followUser(@CurrentUser UserPrincipal currentUser,
                                        @PathVariable("id") Long idToFollow) {

        userService.followOrUnfollowUser(currentUser.getId(), idToFollow);

        return ResponseEntity.ok()
                .body(new ApiResponse(true, "Follow/unfollow user successfully"));
    }
}
