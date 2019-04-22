package com.thienan.lovebox.service;

import com.thienan.lovebox.shared.dto.SingleQuestionDto;

import java.util.List;

public interface SingleQuestionService {

    List<SingleQuestionDto> getQuestionsByUserId(Long userId, boolean answered, int page, int limit);

    SingleQuestionDto createQuestion(SingleQuestionDto singleQuestionDto);
}
