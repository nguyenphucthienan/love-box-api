package com.thienan.lovebox.service.impl;

import com.thienan.lovebox.entity.SingleQuestionEntity;
import com.thienan.lovebox.exception.BadRequestException;
import com.thienan.lovebox.exception.service.SingleQuestionServiceException;
import com.thienan.lovebox.utils.AppConstants;
import com.thienan.lovebox.utils.PagedResponse;
import com.thienan.lovebox.repository.SingleQuestionRepository;
import com.thienan.lovebox.service.SingleQuestionService;
import com.thienan.lovebox.shared.dto.SingleQuestionDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class SingleQuestionServiceImpl implements SingleQuestionService {

    @Autowired
    SingleQuestionRepository singleQuestionRepository;

    @Override
    public PagedResponse<SingleQuestionDto> getQuestionsByUserId(Long userId, boolean answered, int page, int size) {
        validatePageNumberAndSize(page, size);

        Pageable pageRequest = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        Page<SingleQuestionEntity> questionsPage = singleQuestionRepository.findAllQuestionsByUserId(userId, answered, pageRequest);

        List<SingleQuestionEntity> questions = questionsPage.getContent();
        List<SingleQuestionDto> questionDtos = new ArrayList<>();

        ModelMapper modelMapper = new ModelMapper();

        for (SingleQuestionEntity singleQuestionEntity : questions) {
            SingleQuestionDto singleQuestionDto = modelMapper.map(singleQuestionEntity, SingleQuestionDto.class);
            questionDtos.add(singleQuestionDto);
        }

        return new PagedResponse<>(questionDtos, questionsPage.getNumber(), questionsPage.getSize(),
                questionsPage.getTotalElements(), questionsPage.getTotalPages(),
                questionsPage.isFirst(), questionsPage.isLast());
    }

    @Override
    public SingleQuestionDto getQuestion(Long id) {
        ModelMapper modelMapper = new ModelMapper();

        SingleQuestionEntity singleQuestionEntity = singleQuestionRepository.findById(id)
                .orElseThrow(() -> new SingleQuestionServiceException("Single question with ID " + id + " not found"));

        SingleQuestionDto returnQuestion = modelMapper.map(singleQuestionEntity, SingleQuestionDto.class);
        return returnQuestion;
    }

    @Override
    public SingleQuestionDto createQuestion(SingleQuestionDto singleQuestionDto) {
        ModelMapper modelMapper = new ModelMapper();

        SingleQuestionEntity singleQuestionEntity = modelMapper.map(singleQuestionDto, SingleQuestionEntity.class);
        SingleQuestionEntity storedQuestion = singleQuestionRepository.save(singleQuestionEntity);

        SingleQuestionDto returnQuestion = modelMapper.map(storedQuestion, SingleQuestionDto.class);
        return returnQuestion;
    }

    @Override
    public SingleQuestionDto answeredQuestion(Long id, String answerText) {
        SingleQuestionEntity singleQuestionEntity = singleQuestionRepository.findById(id)
                .orElseThrow(() -> new SingleQuestionServiceException("Single question with ID " + id + " not found"));

        if (singleQuestionEntity.isAnswered()) {
            throw new SingleQuestionServiceException("Single question has been answered");
        }

        singleQuestionRepository.answerQuestion(id, Instant.now(), answerText);

        SingleQuestionEntity answeredSingleQuestionEntity = singleQuestionRepository.findById(id)
                .orElseThrow(() -> new SingleQuestionServiceException("Single question with ID " + id + " not found"));

        ModelMapper modelMapper = new ModelMapper();
        SingleQuestionDto returnQuestion = modelMapper.map(answeredSingleQuestionEntity, SingleQuestionDto.class);

        return returnQuestion;
    }

    private void validatePageNumberAndSize(int page, int size) {
        if (page < 0) {
            throw new BadRequestException("Page number cannot be less than zero.");
        }

        if (size > AppConstants.MAX_PAGE_SIZE) {
            throw new BadRequestException("Page size must not be greater than " + AppConstants.MAX_PAGE_SIZE);
        }
    }
}
