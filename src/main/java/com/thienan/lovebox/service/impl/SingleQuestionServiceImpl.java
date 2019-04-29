package com.thienan.lovebox.service.impl;

import com.thienan.lovebox.entity.SingleQuestionEntity;
import com.thienan.lovebox.entity.UserEntity;
import com.thienan.lovebox.exception.service.SingleQuestionServiceException;
import com.thienan.lovebox.repository.UserRepository;
import com.thienan.lovebox.shared.dto.UserDto;
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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SingleQuestionServiceImpl implements SingleQuestionService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    SingleQuestionRepository singleQuestionRepository;

    @Override
    public PagedResponse<SingleQuestionDto> getQuestionsInNewsFeed(Long userId, int page, int size) {
        validatePageNumberAndSize(page, size);

        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User with ID " + userId + " not found"));

        Set<Long> userIds = userEntity.getFollowing().stream().map(UserEntity::getId).collect(Collectors.toSet());

        Pageable pageRequest = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        Page<SingleQuestionEntity> questionPage = singleQuestionRepository.findAllAnsweredQuestionsByUserIdsIn(userIds, pageRequest);

        return this.mapToSingleQuestionDtoPage(questionPage);
    }

    @Override
    public PagedResponse<SingleQuestionDto> getQuestionsByUserId(Long userId, boolean answered, int page, int size) {
        validatePageNumberAndSize(page, size);

        Pageable pageRequest = PageRequest.of(page, size, Sort.Direction.ASC, "createdAt");
        Page<SingleQuestionEntity> questionPage = singleQuestionRepository.findAllQuestionsByUserId(userId, answered, pageRequest);

        return this.mapToSingleQuestionDtoPage(questionPage);
    }

    @Override
    public SingleQuestionDto getQuestion(Long id) {
        SingleQuestionEntity singleQuestionEntity = singleQuestionRepository.findById(id)
                .orElseThrow(() -> new SingleQuestionServiceException("Single question with ID " + id + " not found"));

        return this.mapSingleQuestionDto(singleQuestionEntity);
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
    public SingleQuestionDto answerQuestion(Long id, String answerText) {
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

    @Override
    public SingleQuestionDto unanswerQuestion(Long id) {
        SingleQuestionEntity singleQuestionEntity = singleQuestionRepository.findById(id)
                .orElseThrow(() -> new SingleQuestionServiceException("Single question with ID " + id + " not found"));

        if (!singleQuestionEntity.isAnswered()) {
            throw new SingleQuestionServiceException("Single question has not been answered");
        }

        singleQuestionRepository.unanswerQuestion(id, Instant.now());

        SingleQuestionEntity answeredSingleQuestionEntity = singleQuestionRepository.findById(id)
                .orElseThrow(() -> new SingleQuestionServiceException("Single question with ID " + id + " not found"));

        return this.mapSingleQuestionDto(answeredSingleQuestionEntity);
    }

    @Override
    public SingleQuestionDto loveOrUnloveQuestion(Long id, Long userId) {
        SingleQuestionEntity singleQuestionEntity = singleQuestionRepository.findById(id)
                .orElseThrow(() -> new SingleQuestionServiceException("Single question with ID " + id + " not found"));

        UserEntity userEntity = userRepository.findById(userId).orElseThrow(() ->
                new UsernameNotFoundException("User with ID " + id + " not found")
        );

        if (!singleQuestionEntity.getLoves().contains(userEntity)) {
            singleQuestionEntity.getLoves().add(userEntity);
        } else {
            singleQuestionEntity.getLoves().remove(userEntity);
        }

        SingleQuestionEntity lovedSingleQuestionEntity = singleQuestionRepository.save(singleQuestionEntity);

        return mapSingleQuestionDto(lovedSingleQuestionEntity);
    }

    @Override
    public void deleteQuestion(Long id) {
        SingleQuestionEntity singleQuestionEntity = singleQuestionRepository.findById(id)
                .orElseThrow(() -> new SingleQuestionServiceException("Single question with ID " + id + " not found"));

        if (singleQuestionEntity.isAnswered()) {
            throw new SingleQuestionServiceException("Single question has been answered");
        }

        singleQuestionRepository.delete(singleQuestionEntity);
    }

    private void validatePageNumberAndSize(int page, int size) {
        if (page < 0) {
            throw new SingleQuestionServiceException("Page number cannot be less than zero.");
        }

        if (size > AppConstants.MAX_PAGE_SIZE) {
            throw new SingleQuestionServiceException("Page size must not be greater than " + AppConstants.MAX_PAGE_SIZE);
        }
    }

    private PagedResponse<SingleQuestionDto> mapToSingleQuestionDtoPage(Page<SingleQuestionEntity> questionPage) {
        List<SingleQuestionEntity> questions = questionPage.getContent();
        List<SingleQuestionDto> questionDtos = new ArrayList<>();

        for (SingleQuestionEntity singleQuestionEntity : questions) {
            SingleQuestionDto singleQuestionDto = this.mapSingleQuestionDto(singleQuestionEntity);
            questionDtos.add(singleQuestionDto);
        }

        return new PagedResponse<>(questionDtos, questionPage.getNumber(), questionPage.getSize(),
                questionPage.getTotalElements(), questionPage.getTotalPages(),
                questionPage.isFirst(), questionPage.isLast());
    }

    private SingleQuestionDto mapSingleQuestionDto(SingleQuestionEntity singleQuestionEntity) {
        ModelMapper modelMapper = new ModelMapper();
        Set<UserDto> lovedUserDtos = new HashSet<>();

        for (UserEntity userEntity : singleQuestionEntity.getLoves()) {
            UserDto userDto = modelMapper.map(userEntity, UserDto.class);
            lovedUserDtos.add(userDto);
        }

        SingleQuestionDto questionDto = modelMapper.map(singleQuestionEntity, SingleQuestionDto.class);
        questionDto.setLoves(lovedUserDtos);

        return questionDto;
    }
}
