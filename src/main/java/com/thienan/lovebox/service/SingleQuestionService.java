package com.thienan.lovebox.service;

import com.thienan.lovebox.utils.PagedResponse;
import com.thienan.lovebox.shared.dto.SingleQuestionDto;
import org.springframework.data.domain.Pageable;

public interface SingleQuestionService {

    PagedResponse<SingleQuestionDto> getQuestionsInNewsFeed(Long userId, Pageable pageable);

    PagedResponse<SingleQuestionDto> getQuestionsByUserId(Long userId, boolean answered, Pageable pageable);

    SingleQuestionDto getQuestion(Long id);

    SingleQuestionDto createQuestion(SingleQuestionDto singleQuestionDto);

    SingleQuestionDto answerQuestion(Long id, String answerText);

    SingleQuestionDto unanswerQuestion(Long id);

    SingleQuestionDto loveOrUnloveQuestion(Long id, Long userId);

    void deleteQuestion(Long id);
}
