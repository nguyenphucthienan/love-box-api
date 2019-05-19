package com.thienan.lovebox.service.impl;

import com.thienan.lovebox.entity.CoupleQuestionEntity;
import com.thienan.lovebox.entity.UserEntity;
import com.thienan.lovebox.exception.service.CoupleQuestionServiceException;
import com.thienan.lovebox.repository.CoupleQuestionRepository;
import com.thienan.lovebox.repository.UserRepository;
import com.thienan.lovebox.service.CoupleQuestionService;
import com.thienan.lovebox.shared.dto.CoupleQuestionDto;
import com.thienan.lovebox.utils.PagedResponse;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CoupleQuestionServiceImpl implements CoupleQuestionService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    CoupleQuestionRepository coupleQuestionRepository;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public PagedResponse<CoupleQuestionDto> getQuestionsInNewsFeed(Long userId, Pageable pageable) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new CoupleQuestionServiceException("User with ID " + userId + " not found"));

        Set<Long> userIds = userEntity.getFollowing().stream().map(UserEntity::getId).collect(Collectors.toSet());
        Page<CoupleQuestionEntity> coupleQuestionEntityPage = coupleQuestionRepository.findAllAnsweredQuestionsByUserIdsIn(userIds, pageable);

        return mapToCoupleQuestionDtoPage(coupleQuestionEntityPage);
    }

    @Override
    public PagedResponse<CoupleQuestionDto> getQuestionsByUserId(Long userId, boolean answered, Pageable pageable) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new CoupleQuestionServiceException("User with ID " + userId + " not found"));

        Page<CoupleQuestionEntity> coupleQuestionEntityPage;
        if (answered) {
            coupleQuestionEntityPage = coupleQuestionRepository.findAllAnsweredQuestionsByUserId(userId, pageable);
        } else {
            coupleQuestionEntityPage = coupleQuestionRepository.findAllUnansweredQuestionsByUserId(userId, pageable);
        }

        return mapToCoupleQuestionDtoPage(coupleQuestionEntityPage);
    }

    @Override
    public CoupleQuestionDto getQuestion(Long id) {
        CoupleQuestionEntity coupleQuestionEntity = coupleQuestionRepository.findById(id)
                .orElseThrow(() -> new CoupleQuestionServiceException("Couple question with ID " + id + " not found"));

        return mapToCoupleQuestionDto(coupleQuestionEntity);
    }

    @Override
    public CoupleQuestionDto createQuestion(CoupleQuestionDto coupleQuestionDto) {
        CoupleQuestionEntity coupleQuestionEntity = mapToCoupleQuestionEntity(coupleQuestionDto);
        CoupleQuestionEntity savedCoupleQuestionEntity = coupleQuestionRepository.save(coupleQuestionEntity);
        return mapToCoupleQuestionDto(savedCoupleQuestionEntity);
    }

    @Override
    @Transactional
    public CoupleQuestionDto answerQuestion(Long id, Long userId, String answerText) {
        CoupleQuestionEntity coupleQuestionEntity = coupleQuestionRepository.findById(id)
                .orElseThrow(() -> new CoupleQuestionServiceException("Couple question with ID " + id + " not found"));

        if (coupleQuestionEntity.isAnswered()) {
            throw new CoupleQuestionServiceException("Couple question has been answered");
        }

        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new CoupleQuestionServiceException("User with ID " + userId + " not found"));

        if (userEntity.getId().equals(coupleQuestionEntity.getFirstAnswerer().getId())) {
            coupleQuestionRepository.answerQuestionByFirstAnswerer(id, Instant.now(), answerText);
            if (coupleQuestionEntity.getSecondAnswerText() != null) {
                coupleQuestionRepository.setAnsweredQuestion(id, Instant.now(), true);
            }
        } else if (userEntity.getId().equals(coupleQuestionEntity.getSecondAnswerer().getId())) {
            coupleQuestionRepository.answerQuestionBySecondAnswerer(id, Instant.now(), answerText);
            if (coupleQuestionEntity.getFirstAnswerText() != null) {
                coupleQuestionRepository.setAnsweredQuestion(id, Instant.now(), true);
            }
        }

        CoupleQuestionEntity answeredCoupleQuestionEntity = coupleQuestionRepository.findById(id)
                .orElseThrow(() -> new CoupleQuestionServiceException("Couple question with ID " + id + " not found"));

        return mapToCoupleQuestionDto(answeredCoupleQuestionEntity);
    }

    @Override
    public CoupleQuestionDto unanswerQuestion(Long id) {
        CoupleQuestionEntity coupleQuestionEntity = coupleQuestionRepository.findById(id)
                .orElseThrow(() -> new CoupleQuestionServiceException("Couple question with ID " + id + " not found"));

        if (!coupleQuestionEntity.isAnswered()) {
            throw new CoupleQuestionServiceException("Couple question has not been answered");
        }

        coupleQuestionRepository.unanswerQuestion(id, Instant.now());

        CoupleQuestionEntity unansweredCoupleQuestionEntity = coupleQuestionRepository.findById(id)
                .orElseThrow(() -> new CoupleQuestionServiceException("Couple question with ID " + id + " not found"));

        return mapToCoupleQuestionDto(unansweredCoupleQuestionEntity);
    }

    @Override
    public CoupleQuestionDto loveOrUnloveQuestion(Long id, Long userId) {
        CoupleQuestionEntity coupleQuestionEntity = coupleQuestionRepository.findById(id)
                .orElseThrow(() -> new CoupleQuestionServiceException("Couple question with ID " + id + " not found"));

        UserEntity userEntity = userRepository.findById(userId).orElseThrow(() ->
                new CoupleQuestionServiceException("User with ID " + id + " not found")
        );

        if (!coupleQuestionEntity.getLoves().contains(userEntity)) {
            coupleQuestionEntity.getLoves().add(userEntity);
        } else {
            coupleQuestionEntity.getLoves().remove(userEntity);
        }

        CoupleQuestionEntity lovedCoupleQuestionEntity = coupleQuestionRepository.save(coupleQuestionEntity);
        return mapToCoupleQuestionDto(lovedCoupleQuestionEntity);
    }

    @Override
    public void deleteQuestion(Long id) {
        CoupleQuestionEntity coupleQuestionEntity = coupleQuestionRepository.findById(id)
                .orElseThrow(() -> new CoupleQuestionServiceException("Couple question with ID " + id + " not found"));

        if (coupleQuestionEntity.isAnswered()) {
            throw new CoupleQuestionServiceException("Couple question has been answered");
        }

        coupleQuestionRepository.delete(coupleQuestionEntity);
    }

    private CoupleQuestionEntity mapToCoupleQuestionEntity(CoupleQuestionDto coupleQuestionDto) {
        return modelMapper.map(coupleQuestionDto, CoupleQuestionEntity.class);
    }

    private CoupleQuestionDto mapToCoupleQuestionDto(CoupleQuestionEntity coupleQuestionEntity) {
        return modelMapper.map(coupleQuestionEntity, CoupleQuestionDto.class);
    }

    private List<CoupleQuestionDto> mapToListCoupleQuestionDto(List<CoupleQuestionEntity> coupleQuestionEntities) {
        List<CoupleQuestionDto> coupleQuestionDtos = new ArrayList<>();

        for (CoupleQuestionEntity coupleQuestionEntity : coupleQuestionEntities) {
            CoupleQuestionDto coupleQuestionDto = mapToCoupleQuestionDto(coupleQuestionEntity);
            coupleQuestionDtos.add(coupleQuestionDto);
        }

        return coupleQuestionDtos;
    }

    private PagedResponse<CoupleQuestionDto> mapToCoupleQuestionDtoPage(Page<CoupleQuestionEntity> coupleQuestionEntityPage) {
        List<CoupleQuestionEntity> coupleQuestionEntities = coupleQuestionEntityPage.getContent();
        List<CoupleQuestionDto> coupleQuestionDtos = mapToListCoupleQuestionDto(coupleQuestionEntities);

        return new PagedResponse<>(coupleQuestionDtos, coupleQuestionEntityPage.getNumber(), coupleQuestionEntityPage.getSize(),
                coupleQuestionEntityPage.getTotalElements(), coupleQuestionEntityPage.getTotalPages(),
                coupleQuestionEntityPage.isFirst(), coupleQuestionEntityPage.isLast());
    }
}
