package com.thienan.lovebox.controller;

import com.thienan.lovebox.payload.response.ApiResponse;
import com.thienan.lovebox.payload.response.UserAvailabilityResponse;
import com.thienan.lovebox.payload.response.UserBriefDetailResponse;
import com.thienan.lovebox.payload.response.UserDetailResponse;
import com.thienan.lovebox.security.CurrentUser;
import com.thienan.lovebox.security.UserPrincipal;
import com.thienan.lovebox.service.UserService;
import com.thienan.lovebox.shared.dto.UserDto;
import com.thienan.lovebox.utils.AppConstants;
import com.thienan.lovebox.utils.PagedResponse;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Validated
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

    @GetMapping("/search")
    @PreAuthorize("hasRole('USER')")
    public PagedResponse<UserBriefDetailResponse> searchUsers(@RequestParam(value = "username") @Size(min = 3, max = 20) String username,
                                                              @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
                                                              @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size) {
        PagedResponse<UserDto> users = userService.findUsers(username, page, size);
        List<UserBriefDetailResponse> userResponses = new ArrayList<>();

        ModelMapper modelMapper = new ModelMapper();

        for (UserDto userDto : users.getContent()) {
            UserBriefDetailResponse userBriefDetailResponse = modelMapper.map(userDto, UserBriefDetailResponse.class);
            userResponses.add(userBriefDetailResponse);
        }

        return new PagedResponse<>(userResponses, users.getPagination());
    }
}
