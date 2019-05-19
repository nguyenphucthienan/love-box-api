package com.thienan.lovebox.controller;

import com.thienan.lovebox.exception.BadRequestException;
import com.thienan.lovebox.exception.ForbiddenException;
import com.thienan.lovebox.payload.request.AskCoupleQuestionRequest;
import com.thienan.lovebox.payload.response.CoupleQuestionResponse;
import com.thienan.lovebox.security.CurrentUser;
import com.thienan.lovebox.security.UserPrincipal;
import com.thienan.lovebox.service.CoupleQuestionService;
import com.thienan.lovebox.service.UserService;
import com.thienan.lovebox.shared.dto.BffDetailDto;
import com.thienan.lovebox.shared.dto.CoupleQuestionDto;
import com.thienan.lovebox.shared.dto.UserDto;
import com.thienan.lovebox.utils.PagedResponse;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}/couple-questions")
public class CoupleQuestionController {

    @Autowired
    UserService userService;

    @Autowired
    CoupleQuestionService coupleQuestionService;

    @Autowired
    ModelMapper modelMapper;

    @GetMapping()
    @PreAuthorize("hasRole('USER')")
    public PagedResponse<CoupleQuestionResponse> getQuestions(@CurrentUser UserPrincipal currentUser,
                                                              @PathVariable("userId") Long userId,
                                                              @RequestParam(value = "answered", defaultValue = "false") boolean answered,
                                                              Pageable pageable) {
        if (!userId.equals(currentUser.getId()) && !answered) {
            throw new ForbiddenException("Cannot get questions of this user");
        }

        PagedResponse<CoupleQuestionDto> questions = coupleQuestionService.getQuestionsByUserId(userId, answered, pageable);
        return mapToCoupleQuestionResponsePage(questions);
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public CoupleQuestionResponse askCoupleQuestion(@CurrentUser UserPrincipal currentUser,
                                                    @PathVariable("userId") Long userId,
                                                    @Valid @RequestBody AskCoupleQuestionRequest askCoupleQuestionRequest) {
        UserDto questioner = userService.getUserById(currentUser.getId());
        UserDto userDto = userService.getUserById(userId);
        BffDetailDto bffDetailDto = userDto.getBffDetail();

        if (bffDetailDto == null) {
            throw new BadRequestException("This user does not have BFF");
        }

        CoupleQuestionDto coupleQuestionDto = modelMapper.map(askCoupleQuestionRequest, CoupleQuestionDto.class);
        coupleQuestionDto.setQuestioner(questioner);
        coupleQuestionDto.setFirstAnswerer(bffDetailDto.getFirstUser());
        coupleQuestionDto.setSecondAnswerer(bffDetailDto.getSecondUser());

        CoupleQuestionDto createdQuestion = coupleQuestionService.createQuestion(coupleQuestionDto);
        return mapToCoupleQuestionResponse(createdQuestion);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public CoupleQuestionResponse getCoupleQuestion(@CurrentUser UserPrincipal currentUser,
                                                    @PathVariable("userId") Long userId,
                                                    @PathVariable("id") Long id) {
        CoupleQuestionDto coupleQuestionDto = coupleQuestionService.getQuestion(id);

        if (!coupleQuestionDto.getFirstAnswerer().getId().equals(userId)
                && !coupleQuestionDto.getSecondAnswerer().getId().equals(userId)) {
            throw new BadRequestException("User ID and Question ID do not match");
        }

        if ((!coupleQuestionDto.getFirstAnswerer().getId().equals(currentUser.getId())
                || !coupleQuestionDto.getSecondAnswerer().getId().equals(currentUser.getId()))
                && !coupleQuestionDto.isAnswered()) {
            throw new BadRequestException("Question has not been answered");
        }

        return mapToCoupleQuestionResponse(coupleQuestionDto);
    }

    private CoupleQuestionResponse mapToCoupleQuestionResponse(CoupleQuestionDto coupleQuestionDto) {
        return modelMapper.map(coupleQuestionDto, CoupleQuestionResponse.class);
    }

    private PagedResponse<CoupleQuestionResponse> mapToCoupleQuestionResponsePage(PagedResponse<CoupleQuestionDto> coupleQuestionDtos) {
        List<CoupleQuestionResponse> singleQuestionResponses = modelMapper.map(
                coupleQuestionDtos.getContent(),
                new TypeToken<List<CoupleQuestionResponse>>() {
                }.getType()
        );

        return new PagedResponse<>(singleQuestionResponses, coupleQuestionDtos.getPagination());
    }
}
