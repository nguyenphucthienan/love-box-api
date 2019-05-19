package com.thienan.lovebox.service;

import com.thienan.lovebox.shared.dto.CoupleQuestionDto;
import com.thienan.lovebox.utils.PagedResponse;
import org.springframework.data.domain.Pageable;

public interface CoupleQuestionService {

    PagedResponse<CoupleQuestionDto> getQuestionsInNewsFeed(Long userId, Pageable pageable);

    PagedResponse<CoupleQuestionDto> getQuestionsByUserId(Long userId, boolean answered, Pageable pageable);

    CoupleQuestionDto getQuestion(Long id);

    CoupleQuestionDto createQuestion(CoupleQuestionDto singleQuestionDto);

    CoupleQuestionDto answerQuestion(Long id, Long userId, String answerText);

    CoupleQuestionDto unanswerQuestion(Long id);

    CoupleQuestionDto loveOrUnloveQuestion(Long id, Long userId);

    void deleteQuestion(Long id);
}
