package com.thienan.lovebox.service.impl;

import com.thienan.lovebox.entity.SingleQuestionEntity;
import com.thienan.lovebox.entity.UserEntity;
import com.thienan.lovebox.exception.service.SingleQuestionServiceException;
import com.thienan.lovebox.repository.UserRepository;
import com.thienan.lovebox.shared.dto.UserDto;
import com.thienan.lovebox.utils.PagedResponse;
import com.thienan.lovebox.repository.SingleQuestionRepository;
import com.thienan.lovebox.service.SingleQuestionService;
import com.thienan.lovebox.shared.dto.SingleQuestionDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Autowired
    ModelMapper modelMapper;

    @Override
    public PagedResponse<SingleQuestionDto> getQuestionsInNewsFeed(Long userId, Pageable pageable) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new SingleQuestionServiceException("User with ID " + userId + " not found"));

        Set<Long> userIds = userEntity.getFollowing().stream().map(UserEntity::getId).collect(Collectors.toSet());
        Page<SingleQuestionEntity> singleQuestionEntityPage = singleQuestionRepository.findAllAnsweredQuestionsByUserIdsIn(userIds, pageable);

        return mapToSingleQuestionDtoPage(singleQuestionEntityPage);
    }

    @Override
    public PagedResponse<SingleQuestionDto> getQuestionsByUserId(Long userId, boolean answered, Pageable pageable) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new SingleQuestionServiceException("User with ID " + userId + " not found"));

        Page<SingleQuestionEntity> questionPage = singleQuestionRepository.findAllQuestionsByUserId(userId, answered, pageable);
        return mapToSingleQuestionDtoPage(questionPage);
    }

    @Override
    public SingleQuestionDto getQuestion(Long id) {
        SingleQuestionEntity singleQuestionEntity = singleQuestionRepository.findById(id)
                .orElseThrow(() -> new SingleQuestionServiceException("Single question with ID " + id + " not found"));

        return mapToSingleQuestionDto(singleQuestionEntity);
    }

    @Override
    public SingleQuestionDto createQuestion(SingleQuestionDto singleQuestionDto) {
        SingleQuestionEntity singleQuestionEntity = mapToSingleQuestionEntity(singleQuestionDto);
        SingleQuestionEntity savedSingleQuestionEntity = singleQuestionRepository.save(singleQuestionEntity);
        return mapToSingleQuestionDto(savedSingleQuestionEntity);
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

        return mapToSingleQuestionDto(answeredSingleQuestionEntity);
    }

    @Override
    public SingleQuestionDto unanswerQuestion(Long id) {
        SingleQuestionEntity singleQuestionEntity = singleQuestionRepository.findById(id)
                .orElseThrow(() -> new SingleQuestionServiceException("Single question with ID " + id + " not found"));

        if (!singleQuestionEntity.isAnswered()) {
            throw new SingleQuestionServiceException("Single question has not been answered");
        }

        singleQuestionRepository.unanswerQuestion(id, Instant.now());

        SingleQuestionEntity unansweredSingleQuestionEntity = singleQuestionRepository.findById(id)
                .orElseThrow(() -> new SingleQuestionServiceException("Single question with ID " + id + " not found"));

        return mapToSingleQuestionDto(unansweredSingleQuestionEntity);
    }

    @Override
    public SingleQuestionDto loveOrUnloveQuestion(Long id, Long userId) {
        SingleQuestionEntity singleQuestionEntity = singleQuestionRepository.findById(id)
                .orElseThrow(() -> new SingleQuestionServiceException("Single question with ID " + id + " not found"));

        UserEntity userEntity = userRepository.findById(userId).orElseThrow(() ->
                new SingleQuestionServiceException("User with ID " + id + " not found")
        );

        if (!singleQuestionEntity.getLoves().contains(userEntity)) {
            singleQuestionEntity.getLoves().add(userEntity);
        } else {
            singleQuestionEntity.getLoves().remove(userEntity);
        }

        SingleQuestionEntity lovedSingleQuestionEntity = singleQuestionRepository.save(singleQuestionEntity);
        return mapToSingleQuestionDto(lovedSingleQuestionEntity);
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

    private SingleQuestionEntity mapToSingleQuestionEntity(SingleQuestionDto singleQuestionDto) {
        return modelMapper.map(singleQuestionDto, SingleQuestionEntity.class);
    }

    private SingleQuestionDto mapToSingleQuestionDto(SingleQuestionEntity singleQuestionEntity) {
        Set<UserDto> lovedUserDtos = new HashSet<>();

        for (UserEntity userEntity : singleQuestionEntity.getLoves()) {
            UserDto userDto = modelMapper.map(userEntity, UserDto.class);
            lovedUserDtos.add(userDto);
        }

        SingleQuestionDto singleQuestionDto = modelMapper.map(singleQuestionEntity, SingleQuestionDto.class);
        singleQuestionDto.setLoves(lovedUserDtos);
        return singleQuestionDto;
    }

    private List<SingleQuestionDto> mapToListSingleQuestionDto(List<SingleQuestionEntity> singleQuestionEntities) {
        List<SingleQuestionDto> singleQuestionDtos = new ArrayList<>();

        for (SingleQuestionEntity singleQuestionEntity: singleQuestionEntities) {
            SingleQuestionDto singleQuestionDto = mapToSingleQuestionDto(singleQuestionEntity);
            singleQuestionDtos.add(singleQuestionDto);
        }

        return singleQuestionDtos;
    }

    private PagedResponse<SingleQuestionDto> mapToSingleQuestionDtoPage(Page<SingleQuestionEntity> singleQuestionEntityPage) {
        List<SingleQuestionEntity> singleQuestionEntities = singleQuestionEntityPage.getContent();
        List<SingleQuestionDto> singleQuestionDtos = mapToListSingleQuestionDto(singleQuestionEntities);

        return new PagedResponse<>(singleQuestionDtos, singleQuestionEntityPage.getNumber(), singleQuestionEntityPage.getSize(),
                singleQuestionEntityPage.getTotalElements(), singleQuestionEntityPage.getTotalPages(),
                singleQuestionEntityPage.isFirst(), singleQuestionEntityPage.isLast());
    }
}
