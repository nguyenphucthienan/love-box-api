package com.thienan.lovebox.service;

import com.thienan.lovebox.shared.dto.UserDto;
import com.thienan.lovebox.utils.PagedResponse;

public interface UserService {

    String authenticateUser(String usernameOrEmail, String password);

    UserDto getUserById(Long id);

    UserDto createUser(UserDto user);

    Boolean checkUsernameAvailability(String username);

    Boolean checkEmailAvailability(String email);

    void followOrUnfollowUser(Long id, Long idToFollowOrUnfollow);

    Boolean checkUserHasBff(Long id);

    PagedResponse<UserDto> findUsers(String username, int page, int size);
}
