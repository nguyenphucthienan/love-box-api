package com.thienan.lovebox.service.impl;

import com.thienan.lovebox.entity.SingleQuestionEntity;
import com.thienan.lovebox.repository.SingleQuestionRepository;
import com.thienan.lovebox.service.SingleQuestionService;
import com.thienan.lovebox.shared.dto.SingleQuestionDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SingleQuestionServiceImpl implements SingleQuestionService {

    @Autowired
    SingleQuestionRepository singleQuestionRepository;

    @Override
    public List<SingleQuestionDto> getQuestionsByUserId(Long userId, boolean answered, int page, int limit) {
        Pageable pageRequest = PageRequest.of(page, limit);
        Page<SingleQuestionEntity> questionsPage = singleQuestionRepository.findAllQuestionsByUserId(userId, answered, pageRequest);

        List<SingleQuestionEntity> questions = questionsPage.getContent();
        List<SingleQuestionDto> returnQuestions = new ArrayList<>();

        ModelMapper modelMapper = new ModelMapper();

        for (SingleQuestionEntity singleQuestionEntity : questions) {
            SingleQuestionDto singleQuestionDto = modelMapper.map(singleQuestionEntity, SingleQuestionDto.class);
            returnQuestions.add(singleQuestionDto);
        }

        return returnQuestions;
    }

    @Override
    public SingleQuestionDto createQuestion(SingleQuestionDto singleQuestionDto) {
        ModelMapper modelMapper = new ModelMapper();

        SingleQuestionEntity singleQuestionEntity = modelMapper.map(singleQuestionDto, SingleQuestionEntity.class);
        SingleQuestionEntity storedQuestion = singleQuestionRepository.save(singleQuestionEntity);

        SingleQuestionDto returnQuestion = modelMapper.map(storedQuestion, SingleQuestionDto.class);
        return returnQuestion;
    }
}
