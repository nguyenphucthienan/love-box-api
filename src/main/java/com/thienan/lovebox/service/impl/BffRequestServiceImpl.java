package com.thienan.lovebox.service.impl;

import com.thienan.lovebox.entity.BffDetailEntity;
import com.thienan.lovebox.entity.BffRequestEntity;
import com.thienan.lovebox.entity.UserEntity;
import com.thienan.lovebox.exception.service.SingleQuestionServiceException;
import com.thienan.lovebox.repository.BffDetailRepository;
import com.thienan.lovebox.repository.BffRequestRepository;
import com.thienan.lovebox.repository.UserRepository;
import com.thienan.lovebox.service.BffRequestService;
import com.thienan.lovebox.shared.dto.BffRequestDto;
import com.thienan.lovebox.utils.PagedResponse;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BffRequestServiceImpl implements BffRequestService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    BffRequestRepository bffRequestRepository;

    @Autowired
    BffDetailRepository bffDetailRepository;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public PagedResponse<BffRequestDto> getSentBffRequestByUserId(Long userId, Pageable pageable) {
        Page<BffRequestEntity> bffRequestPage = bffRequestRepository.findAllSentBffRequestsByUserId(userId, pageable);
        return mapToBffRequestDtoPage(bffRequestPage);
    }

    @Override
    public PagedResponse<BffRequestDto> getReceivedBffRequestByUserId(Long userId, Pageable pageable) {
        Page<BffRequestEntity> bffRequestPage = bffRequestRepository.findAllReceivedBffRequestsByUserId(userId, pageable);
        return mapToBffRequestDtoPage(bffRequestPage);
    }

    @Override
    public BffRequestDto getBffRequest(Long id) {
        BffRequestEntity bffRequestEntity = bffRequestRepository.findById(id)
                .orElseThrow(() -> new SingleQuestionServiceException("BFF Request with ID " + id + " not found"));

        return mapToBffRequestDto(bffRequestEntity);
    }

    @Override
    public Boolean checkBffRequestExists(Long fromUserId, Long toUserId) {
        BffRequestEntity bffRequestEntity = bffRequestRepository.findByFromUserIdAndToUserId(fromUserId, toUserId)
                .orElse(null);

        return bffRequestEntity != null;
    }

    @Override
    public BffRequestDto getBffRequestByFromUserIdAndToUserId(Long fromUserId, Long toUserId) {
        BffRequestEntity bffRequestEntity = bffRequestRepository.findByFromUserIdAndToUserId(fromUserId, toUserId)
                .orElse(null);

        return mapToBffRequestDto(bffRequestEntity);
    }

    @Override
    public BffRequestDto createBffRequest(BffRequestDto bffRequestDto) {
        BffRequestEntity bffRequestEntity = mapToBffRequestEntity(bffRequestDto);
        BffRequestEntity savedBffRequest = bffRequestRepository.save(bffRequestEntity);
        return mapToBffRequestDto(savedBffRequest);
    }

    @Override
    public void approveBffRequest(Long id) {
        BffRequestEntity bffRequestEntity = bffRequestRepository.findById(id)
                .orElseThrow(() -> new SingleQuestionServiceException("BFF Request with ID " + id + " not found"));

        Long fromUserId = bffRequestEntity.getFromUser().getId();
        Long toUserId = bffRequestEntity.getToUser().getId();

        UserEntity fromUser = userRepository.findById(fromUserId)
                .orElseThrow(() -> new SingleQuestionServiceException("User with ID " + fromUserId + " not found"));
        UserEntity toUser = userRepository.findById(toUserId)
                .orElseThrow(() -> new SingleQuestionServiceException("User with ID " + toUserId + " not found"));

        BffDetailEntity bffDetailEntity = new BffDetailEntity(fromUser, toUser, "");
        BffDetailEntity storedBffDetailEntity = bffDetailRepository.save(bffDetailEntity);

        fromUser.setBffDetail(storedBffDetailEntity);
        toUser.setBffDetail(storedBffDetailEntity);

        userRepository.save(fromUser);
        userRepository.save(toUser);

        this.deleteAllBffRequestsByUserId(fromUserId);
        this.deleteAllBffRequestsByUserId(toUserId);
    }

    @Override
    public void rejectBffRequest(Long id) {
        BffRequestEntity bffRequestEntity = bffRequestRepository.findById(id)
                .orElseThrow(() -> new SingleQuestionServiceException("BFF Request with ID " + id + " not found"));

        this.deleteBffRequest(id);
    }

    @Override
    public void deleteBffRequest(Long id) {
        BffRequestEntity bffRequestEntity = bffRequestRepository.findById(id)
                .orElseThrow(() -> new SingleQuestionServiceException("BFF Request with ID " + id + " not found"));

        bffRequestRepository.delete(bffRequestEntity);
    }

    @Override
    public void deleteAllBffRequestsByUserId(Long userId) {
        bffRequestRepository.deleteAllByUserId(userId);
    }

    @Override
    public void breakUp(Long userId) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new SingleQuestionServiceException("User with ID " + userId + " not found"));

        BffDetailEntity bffDetailEntity = userEntity.getBffDetail();
        if (bffDetailEntity != null) {
            UserEntity firstUserEntity = bffDetailEntity.getFirstUser();
            UserEntity secondUserEntity = bffDetailEntity.getSecondUser();

            firstUserEntity.setBffDetail(null);
            secondUserEntity.setBffDetail(null);

            bffDetailRepository.delete(bffDetailEntity);
            userRepository.save(firstUserEntity);
            userRepository.save(secondUserEntity);
        }
    }

    private BffRequestEntity mapToBffRequestEntity(BffRequestDto bffRequestDto) {
        return modelMapper.map(bffRequestDto, BffRequestEntity.class);
    }

    private BffRequestDto mapToBffRequestDto(BffRequestEntity bffRequestEntity) {
        return modelMapper.map(bffRequestEntity, BffRequestDto.class);
    }

    private List<BffRequestDto> mapToBffRequestDtoList(List<BffRequestEntity> bffRequestEntities) {
        return modelMapper.map(bffRequestEntities, new TypeToken<List<BffRequestDto>>() {
        }.getType());
    }

    private PagedResponse<BffRequestDto> mapToBffRequestDtoPage(Page<BffRequestEntity> bffRequestEntityPage) {
        List<BffRequestEntity> bffRequestEntities = bffRequestEntityPage.getContent();
        List<BffRequestDto> bffRequestDtos = mapToBffRequestDtoList(bffRequestEntities);

        return new PagedResponse<>(bffRequestDtos, bffRequestEntityPage.getNumber(), bffRequestEntityPage.getSize(),
                bffRequestEntityPage.getTotalElements(), bffRequestEntityPage.getTotalPages(),
                bffRequestEntityPage.isFirst(), bffRequestEntityPage.isLast());
    }
}
