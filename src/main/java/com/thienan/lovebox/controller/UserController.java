package com.thienan.lovebox.controller;

import com.thienan.lovebox.payload.response.ApiResponse;
import com.thienan.lovebox.payload.response.UserAvailabilityResponse;
import com.thienan.lovebox.payload.response.UserBriefDetailResponse;
import com.thienan.lovebox.payload.response.UserResponse;
import com.thienan.lovebox.security.CurrentUser;
import com.thienan.lovebox.security.UserPrincipal;
import com.thienan.lovebox.service.UserService;
import com.thienan.lovebox.shared.dto.UserDto;
import com.thienan.lovebox.utils.PagedResponse;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Size;
import java.util.List;
import java.util.Set;

@Validated
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    ModelMapper modelMapper;

    @GetMapping("/{id}")
    public UserResponse getUser(@CurrentUser UserPrincipal currentUser, @PathVariable("id") Long id) {
        UserDto userDto = userService.getUserById(id);

        Converter<Set<UserDto>, Integer> converter = ctx -> ctx.getSource() == null ? null : ctx.getSource().size();

        modelMapper.typeMap(UserDto.class, UserResponse.class)
                .addMappings(mapper -> mapper.map(UserDto::getBffDetail, UserResponse::setBffDetail))
                .addMappings(mapper -> mapper.using(converter).map(UserDto::getFollowing, UserResponse::setFollowingCount))
                .addMappings(mapper -> mapper.using(converter).map(UserDto::getFollowers, UserResponse::setFollowersCount));

        if (currentUser == null) {
            modelMapper.typeMap(UserDto.class, UserResponse.class)
                    .addMappings(mapper -> mapper.skip(UserResponse::setFollowed));
        }

        UserResponse userResponse = modelMapper.map(userDto, UserResponse.class);

        if (currentUser != null) {
            boolean isFollowed = userService.checkUserHasFollow(currentUser.getId(), id);
            userResponse.setFollowed(isFollowed);
        }

        return userResponse;
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

    @GetMapping("/{id}/following")
    @PreAuthorize("hasRole('USER')")
    public PagedResponse<UserBriefDetailResponse> getFollowing(@PathVariable("id") Long id, Pageable pageable) {
        PagedResponse<UserDto> users = userService.getFollowing(id, pageable);
        return mapToUserBriefDetailResponsePage(users);
    }

    @GetMapping("/{id}/followers")
    @PreAuthorize("hasRole('USER')")
    public PagedResponse<UserBriefDetailResponse> getFollowers(@PathVariable("id") Long id, Pageable pageable) {
        PagedResponse<UserDto> users = userService.getFollowers(id, pageable);
        return mapToUserBriefDetailResponsePage(users);
    }

    @PostMapping("{id}/follow")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse followUser(@CurrentUser UserPrincipal currentUser,
                                  @PathVariable("id") Long idToFollow) {
        userService.followOrUnfollowUser(currentUser.getId(), idToFollow);
        return new ApiResponse(true, "Follow/unfollow user successfully");
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('USER')")
    public PagedResponse<UserBriefDetailResponse> searchUsers(@RequestParam(value = "username") @Size(min = 3, max = 20) String username,
                                                              Pageable pageable) {
        PagedResponse<UserDto> users = userService.findUsers(username, pageable);
        return mapToUserBriefDetailResponsePage(users);
    }

    private PagedResponse<UserBriefDetailResponse> mapToUserBriefDetailResponsePage(PagedResponse<UserDto> userDtos) {
        List<UserBriefDetailResponse> userResponses = modelMapper.map(
                userDtos.getContent(),
                new TypeToken<List<UserBriefDetailResponse>>() {
                }.getType()
        );

        return new PagedResponse<>(userResponses, userDtos.getPagination());
    }
}
