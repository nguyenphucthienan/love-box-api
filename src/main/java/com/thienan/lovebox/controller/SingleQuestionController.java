package com.thienan.lovebox.controller;

import com.thienan.lovebox.exception.BadRequestException;
import com.thienan.lovebox.exception.ForbiddenException;
import com.thienan.lovebox.payload.request.AnswerSingleQuestionRequest;
import com.thienan.lovebox.payload.request.AskSingleQuestionRequest;
import com.thienan.lovebox.payload.response.ApiResponse;
import com.thienan.lovebox.utils.PagedResponse;
import com.thienan.lovebox.payload.response.SingleQuestionResponse;
import com.thienan.lovebox.security.CurrentUser;
import com.thienan.lovebox.security.UserPrincipal;
import com.thienan.lovebox.service.SingleQuestionService;
import com.thienan.lovebox.service.UserService;
import com.thienan.lovebox.shared.dto.SingleQuestionDto;
import com.thienan.lovebox.shared.dto.UserDto;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}/single-questions")
public class SingleQuestionController {

    @Autowired
    UserService userService;

    @Autowired
    SingleQuestionService singleQuestionService;

    @Autowired
    ModelMapper modelMapper;

    @GetMapping("/news-feed")
    @PreAuthorize("hasRole('USER')")
    public PagedResponse<SingleQuestionResponse> getQuestionsInNewsFeed(@CurrentUser UserPrincipal currentUser,
                                                                        @PathVariable("userId") Long userId,
                                                                        Pageable pageable) {
        if (!userId.equals(currentUser.getId())) {
            throw new ForbiddenException("Cannot get news feed of this user");
        }

        PagedResponse<SingleQuestionDto> questions = singleQuestionService.getQuestionsInNewsFeed(userId, pageable);
        return mapToSingleQuestionResponsePage(questions);
    }

    @GetMapping()
    @PreAuthorize("hasRole('USER')")
    public PagedResponse<SingleQuestionResponse> getQuestions(@CurrentUser UserPrincipal currentUser,
                                                              @PathVariable("userId") Long userId,
                                                              @RequestParam(value = "answered", defaultValue = "false") boolean answered,
                                                              Pageable pageable) {
        if (!userId.equals(currentUser.getId()) && !answered) {
            throw new ForbiddenException("Cannot get questions of this user");
        }

        PagedResponse<SingleQuestionDto> questions = singleQuestionService.getQuestionsByUserId(userId, answered, pageable);
        return mapToSingleQuestionResponsePage(questions);
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public SingleQuestionResponse askSingleQuestion(@CurrentUser UserPrincipal currentUser,
                                                    @PathVariable("userId") Long answererId,
                                                    @Valid @RequestBody AskSingleQuestionRequest askSingleQuestionRequest) {
        UserDto questioner = userService.getUserById(currentUser.getId());
        UserDto answerer = userService.getUserById(answererId);

        SingleQuestionDto singleQuestionDto = modelMapper.map(askSingleQuestionRequest, SingleQuestionDto.class);
        singleQuestionDto.setQuestioner(questioner);
        singleQuestionDto.setAnswerer(answerer);

        SingleQuestionDto createdQuestion = singleQuestionService.createQuestion(singleQuestionDto);
        return mapToSingleQuestionResponse(createdQuestion);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public SingleQuestionResponse getSingleQuestion(@CurrentUser UserPrincipal currentUser,
                                                    @PathVariable("userId") Long answererId,
                                                    @PathVariable("id") Long id) {
        SingleQuestionDto singleQuestionDto = singleQuestionService.getQuestion(id);

        if (!singleQuestionDto.getAnswerer().getId().equals(answererId)) {
            throw new BadRequestException("User ID and Question ID do not match");
        }

        if ((!singleQuestionDto.getAnswerer().getId().equals(currentUser.getId()) && !singleQuestionDto.isAnswered())) {
            throw new BadRequestException("Question has not been answered");
        }

        return mapToSingleQuestionResponse(singleQuestionDto);
    }

    @PostMapping("/{id}/answer")
    @PreAuthorize("hasRole('USER')")
    public SingleQuestionResponse answerSingleQuestion(@CurrentUser UserPrincipal currentUser,
                                                       @PathVariable("userId") Long answererId,
                                                       @PathVariable("id") Long id,
                                                       @Valid @RequestBody AnswerSingleQuestionRequest answerSingleQuestionRequest) {
        SingleQuestionDto singleQuestionDto = singleQuestionService.getQuestion(id);

        if (!singleQuestionDto.getAnswerer().getId().equals(answererId)) {
            throw new BadRequestException("User ID and Question ID do not match");
        }

        if (!singleQuestionDto.getAnswerer().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("Cannot answer this question");
        }

        SingleQuestionDto answeredSingleQuestionDto = singleQuestionService
                .answerQuestion(id, answerSingleQuestionRequest.getAnswerText());

        return mapToSingleQuestionResponse(answeredSingleQuestionDto);
    }

    @PostMapping("/{id}/unanswer")
    @PreAuthorize("hasRole('USER')")
    public SingleQuestionResponse unanswerSingleQuestion(@CurrentUser UserPrincipal currentUser,
                                                         @PathVariable("userId") Long answererId,
                                                         @PathVariable("id") Long id) {
        SingleQuestionDto singleQuestionDto = singleQuestionService.getQuestion(id);

        if (!singleQuestionDto.getAnswerer().getId().equals(answererId)) {
            throw new BadRequestException("User ID and Question ID do not match");
        }

        if (!singleQuestionDto.getAnswerer().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("Cannot unanswer this question");
        }

        SingleQuestionDto unansweredSingleQuestionDto = singleQuestionService.unanswerQuestion(id);
        return mapToSingleQuestionResponse(unansweredSingleQuestionDto);
    }

    @PostMapping("/{id}/love")
    @PreAuthorize("hasRole('USER')")
    public SingleQuestionResponse loveOrUnloveSingleQuestion(@CurrentUser UserPrincipal currentUser,
                                                             @PathVariable("userId") Long answererId,
                                                             @PathVariable("id") Long id) {
        SingleQuestionDto singleQuestionDto = singleQuestionService.getQuestion(id);

        if (!singleQuestionDto.getAnswerer().getId().equals(answererId)) {
            throw new BadRequestException("User ID and Question ID do not match");
        }

        if (!singleQuestionDto.isAnswered()) {
            throw new BadRequestException("Question has not been answered");
        }

        SingleQuestionDto lovedSingleQuestionDto = singleQuestionService.loveOrUnloveQuestion(id, currentUser.getId());
        return mapToSingleQuestionResponse(lovedSingleQuestionDto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse deleteSingleQuestion(@CurrentUser UserPrincipal currentUser,
                                            @PathVariable("userId") Long answererId,
                                            @PathVariable("id") Long id) {
        SingleQuestionDto singleQuestionDto = singleQuestionService.getQuestion(id);

        if (!singleQuestionDto.getAnswerer().getId().equals(answererId)) {
            throw new BadRequestException("User ID and Question ID do not match");
        }

        if (!singleQuestionDto.getAnswerer().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("Cannot delete this question");
        }

        singleQuestionService.deleteQuestion(id);
        return new ApiResponse(true, "Delete single question successfully");
    }

    private SingleQuestionResponse mapToSingleQuestionResponse(SingleQuestionDto singleQuestionDto) {
        return modelMapper.map(singleQuestionDto, SingleQuestionResponse.class);
    }

    private PagedResponse<SingleQuestionResponse> mapToSingleQuestionResponsePage(PagedResponse<SingleQuestionDto> singleQuestionDtos) {
        List<SingleQuestionResponse> singleQuestionResponses = modelMapper.map(
                singleQuestionDtos.getContent(),
                new TypeToken<List<SingleQuestionResponse>>() {
                }.getType()
        );

        return new PagedResponse<>(singleQuestionResponses, singleQuestionDtos.getPagination());
    }
}
