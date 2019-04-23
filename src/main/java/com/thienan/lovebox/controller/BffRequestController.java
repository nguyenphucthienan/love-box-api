package com.thienan.lovebox.controller;

import com.thienan.lovebox.exception.BadRequestException;
import com.thienan.lovebox.exception.ForbiddenException;
import com.thienan.lovebox.payload.request.BffRequestRequest;
import com.thienan.lovebox.payload.response.BffRequestResponse;
import com.thienan.lovebox.security.CurrentUser;
import com.thienan.lovebox.security.UserPrincipal;
import com.thienan.lovebox.service.BffRequestService;
import com.thienan.lovebox.service.UserService;
import com.thienan.lovebox.shared.dto.BffRequestDto;
import com.thienan.lovebox.shared.dto.UserDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/{userId}/bff-requests")
public class BffRequestController {

    @Autowired
    UserService userService;

    @Autowired
    BffRequestService bffRequestService;

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public BffRequestResponse getBffRequest(@CurrentUser UserPrincipal currentUser,
                                            @PathVariable("userId") Long toUserId,
                                            @PathVariable("id") Long id) {
        BffRequestDto bffRequestDto = bffRequestService.getBffRequest(id);

        if (!bffRequestDto.getFromUser().getId().equals(currentUser.getId())
                && !bffRequestDto.getToUser().getId().equals(toUserId)) {
            throw new BadRequestException("User ID and BFF request ID do not match");
        }

        if (!bffRequestDto.getFromUser().getId().equals(currentUser.getId())
                && !bffRequestDto.getToUser().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("Cannot get this BFF request");
        }

        ModelMapper modelMapper = new ModelMapper();

        BffRequestResponse bffRequestResponse = modelMapper.map(bffRequestDto, BffRequestResponse.class);
        return bffRequestResponse;
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public BffRequestResponse sentBffRequest(@CurrentUser UserPrincipal currentUser,
                                             @PathVariable("userId") Long toUserId,
                                             @RequestBody BffRequestRequest bffRequestRequest) {
        UserDto fromUser = userService.getUserById(currentUser.getId());
        UserDto toUser = userService.getUserById(toUserId);

        ModelMapper modelMapper = new ModelMapper();

        BffRequestDto bffRequestDto = modelMapper.map(bffRequestRequest, BffRequestDto.class);
        bffRequestDto.setFromUser(fromUser);
        bffRequestDto.setToUser(toUser);

        BffRequestDto createdBffRequest = bffRequestService.createBffRequest(bffRequestDto);
        BffRequestResponse bffRequestResponse = modelMapper.map(createdBffRequest, BffRequestResponse.class);

        return bffRequestResponse;
    }
}
