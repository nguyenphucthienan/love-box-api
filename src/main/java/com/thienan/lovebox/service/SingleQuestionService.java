package com.thienan.lovebox.service;

import com.thienan.lovebox.utils.PagedResponse;
import com.thienan.lovebox.shared.dto.SingleQuestionDto;

public interface SingleQuestionService {

    PagedResponse<SingleQuestionDto> getQuestionsInNewsFeed(Long userId, int page, int size);

    PagedResponse<SingleQuestionDto> getQuestionsByUserId(Long userId, boolean answered, int page, int size);

    SingleQuestionDto getQuestion(Long id);

    SingleQuestionDto createQuestion(SingleQuestionDto singleQuestionDto);

    SingleQuestionDto answerQuestion(Long id, String answerText);

    SingleQuestionDto unanswerQuestion(Long id);

    SingleQuestionDto loveOrUnloveQuestion(Long id, Long userId);

    void deleteQuestion(Long id);
}
