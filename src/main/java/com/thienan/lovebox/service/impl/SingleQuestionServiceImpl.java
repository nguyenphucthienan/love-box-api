package com.thienan.lovebox.service.impl;

import com.thienan.lovebox.entity.SingleQuestionEntity;
import com.thienan.lovebox.repository.SingleQuestionRepository;
import com.thienan.lovebox.service.SingleQuestionService;
import com.thienan.lovebox.shared.dto.SingleQuestionDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SingleQuestionServiceImpl implements SingleQuestionService {

    @Autowired
    SingleQuestionRepository singleQuestionRepository;

    @Override
    public SingleQuestionDto createQuestion(SingleQuestionDto singleQuestionDto) {
        ModelMapper modelMapper = new ModelMapper();

        SingleQuestionEntity singleQuestionEntity = modelMapper.map(singleQuestionDto, SingleQuestionEntity.class);
        SingleQuestionEntity storedQuestion = singleQuestionRepository.save(singleQuestionEntity);

        SingleQuestionDto returnQuestion = modelMapper.map(storedQuestion, SingleQuestionDto.class);
        return returnQuestion;
    }
}
