package com.thienan.lovebox.controller;

import com.thienan.lovebox.exception.BadRequestException;
import com.thienan.lovebox.exception.ForbiddenException;
import com.thienan.lovebox.payload.request.BffRequestRequest;
import com.thienan.lovebox.payload.response.ApiResponse;
import com.thienan.lovebox.payload.response.BffRequestResponse;
import com.thienan.lovebox.security.CurrentUser;
import com.thienan.lovebox.security.UserPrincipal;
import com.thienan.lovebox.service.BffRequestService;
import com.thienan.lovebox.service.UserService;
import com.thienan.lovebox.shared.dto.BffRequestDto;
import com.thienan.lovebox.shared.dto.UserDto;
import com.thienan.lovebox.utils.PagedResponse;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}/bff-requests")
public class BffRequestController {

    @Autowired
    UserService userService;

    @Autowired
    BffRequestService bffRequestService;

    @Autowired
    ModelMapper modelMapper;

    @GetMapping()
    @PreAuthorize("hasRole('USER')")
    public PagedResponse<BffRequestResponse> getBffRequests(@CurrentUser UserPrincipal currentUser,
                                                            @PathVariable("userId") Long userId,
                                                            @RequestParam(value = "type") String type,
                                                            Pageable pageable) {
        if (!userId.equals(currentUser.getId())) {
            throw new ForbiddenException("Cannot get this BFF requests of this user");
        }

        PagedResponse<BffRequestDto> bffRequestDtoPage;
        if (type.equals("sent")) {
            bffRequestDtoPage = bffRequestService.getSentBffRequestByUserId(userId, pageable);
        } else if (type.equals("received")) {
            bffRequestDtoPage = bffRequestService.getReceivedBffRequestByUserId(userId, pageable);
        } else {
            throw new BadRequestException("Missing BFF request type");
        }

        return mapToBffRequestResponsePage(bffRequestDtoPage);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public BffRequestResponse getBffRequest(@CurrentUser UserPrincipal currentUser,
                                            @PathVariable("userId") Long userId,
                                            @PathVariable("id") Long id) {
        BffRequestDto bffRequestDto = bffRequestService.getBffRequest(id);

        if (!bffRequestDto.getFromUser().getId().equals(currentUser.getId())
                && !bffRequestDto.getToUser().getId().equals(userId)) {
            throw new BadRequestException("User ID and BFF request ID do not match");
        }

        if (!bffRequestDto.getFromUser().getId().equals(currentUser.getId())
                && !bffRequestDto.getToUser().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("Cannot get this BFF request");
        }

        return mapToBffRequestResponse(bffRequestDto);
    }

    @GetMapping("/exist")
    public BffRequestResponse checkBffRequestExists(@RequestParam(value = "fromUserId") Long fromUserId,
                                                    @RequestParam(value = "toUserId") Long toUserId) {
        BffRequestDto bffRequestDto = bffRequestService.getBffRequestByFromUserIdAndToUserId(fromUserId, toUserId);
        return mapToBffRequestResponse(bffRequestDto);
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public BffRequestResponse sendBffRequest(@CurrentUser UserPrincipal currentUser,
                                             @PathVariable("userId") Long toUserId,
                                             @RequestBody BffRequestRequest bffRequestRequest) {
        if (userService.checkUserHasBff(currentUser.getId())) {
            throw new BadRequestException("User with ID " + toUserId + " already has BFF");
        }

        if (userService.checkUserHasBff(toUserId)) {
            throw new BadRequestException("User with ID " + toUserId + " already has BFF");
        }

        Long fromUserId = currentUser.getId();

        if (bffRequestService.checkBffRequestExists(fromUserId, toUserId)) {
            throw new BadRequestException("BFF request with from user ID " + fromUserId + " and to user ID " + toUserId + " exists");
        }

        UserDto fromUser = userService.getUserById(fromUserId);
        UserDto toUser = userService.getUserById(toUserId);

        BffRequestDto bffRequestDto = modelMapper.map(bffRequestRequest, BffRequestDto.class);
        bffRequestDto.setFromUser(fromUser);
        bffRequestDto.setToUser(toUser);

        BffRequestDto createdBffRequest = bffRequestService.createBffRequest(bffRequestDto);
        return mapToBffRequestResponse(createdBffRequest);
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse approveBffRequest(@CurrentUser UserPrincipal currentUser,
                                         @PathVariable("userId") Long userId,
                                         @PathVariable("id") Long id) {
        BffRequestDto bffRequestDto = bffRequestService.getBffRequest(id);

        if (!bffRequestDto.getFromUser().getId().equals(currentUser.getId())
                && !bffRequestDto.getToUser().getId().equals(userId)) {
            throw new BadRequestException("User ID and BFF request ID do not match");
        }

        if (!bffRequestDto.getToUser().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("Cannot approve this BFF request");
        }

        bffRequestService.approveBffRequest(id);
        return new ApiResponse(true, "Approve BFF request successfully");
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse rejectBffRequest(@CurrentUser UserPrincipal currentUser,
                                        @PathVariable("userId") Long userId,
                                        @PathVariable("id") Long id) {
        BffRequestDto bffRequestDto = bffRequestService.getBffRequest(id);

        if (!bffRequestDto.getFromUser().getId().equals(currentUser.getId())
                && !bffRequestDto.getToUser().getId().equals(userId)) {
            throw new BadRequestException("User ID and BFF request ID do not match");
        }

        if (!bffRequestDto.getToUser().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("Cannot reject this BFF request");
        }

        bffRequestService.rejectBffRequest(id);
        return new ApiResponse(true, "Reject BFF request successfully");
    }

    private BffRequestResponse mapToBffRequestResponse(BffRequestDto bffRequestDto) {
        return modelMapper.map(bffRequestDto, BffRequestResponse.class);
    }

    private List<BffRequestResponse> mapToBffRequestResponseList(List<BffRequestDto> bffRequestDtos) {
        return modelMapper.map(bffRequestDtos, new TypeToken<List<BffRequestResponse>>() {
        }.getType());
    }

    private PagedResponse<BffRequestResponse> mapToBffRequestResponsePage(PagedResponse<BffRequestDto> bffRequestDtoPage) {
        List<BffRequestDto> bffRequestDtos = bffRequestDtoPage.getContent();
        List<BffRequestResponse> bffRequestResponses = mapToBffRequestResponseList(bffRequestDtos);
        return new PagedResponse<>(bffRequestResponses, bffRequestDtoPage.getPagination());
    }
}
