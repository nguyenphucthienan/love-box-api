package com.thienan.lovebox.service;

import com.thienan.lovebox.utils.PagedResponse;
import com.thienan.lovebox.shared.dto.SingleQuestionDto;

public interface SingleQuestionService {

    PagedResponse<SingleQuestionDto> getQuestionsByUserId(Long userId, boolean answered, int page, int size);

    SingleQuestionDto createQuestion(SingleQuestionDto singleQuestionDto);
}
