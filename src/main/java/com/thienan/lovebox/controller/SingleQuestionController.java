package com.thienan.lovebox.controller;

import com.thienan.lovebox.payload.request.AskSingleQuestionRequest;
import com.thienan.lovebox.payload.response.SingleQuestionReponse;
import com.thienan.lovebox.security.CurrentUser;
import com.thienan.lovebox.security.UserPrincipal;
import com.thienan.lovebox.service.SingleQuestionService;
import com.thienan.lovebox.service.UserService;
import com.thienan.lovebox.shared.dto.SingleQuestionDto;
import com.thienan.lovebox.shared.dto.UserDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/{userId}/single-questions")
public class SingleQuestionController {

    @Autowired
    UserService userService;

    @Autowired
    SingleQuestionService singleQuestionService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<SingleQuestionReponse> askSingleQuestion(@CurrentUser UserPrincipal currentUser,
                                                                   @PathVariable("userId") Long answererId,
                                                                   @RequestBody AskSingleQuestionRequest askSingleQuestionRequest) {
        UserDto questioner = userService.getUserById(currentUser.getId());
        UserDto answerer = userService.getUserById(answererId);

        ModelMapper modelMapper = new ModelMapper();

        SingleQuestionDto singleQuestionDto = modelMapper.map(askSingleQuestionRequest, SingleQuestionDto.class);
        singleQuestionDto.setQuestioner(questioner);
        singleQuestionDto.setAnswerer(answerer);

        SingleQuestionDto createdQuestion = singleQuestionService.createQuestion(singleQuestionDto);
        SingleQuestionReponse singleQuestionReponse = modelMapper.map(createdQuestion, SingleQuestionReponse.class);

        return ResponseEntity.ok(singleQuestionReponse);
    }
}
