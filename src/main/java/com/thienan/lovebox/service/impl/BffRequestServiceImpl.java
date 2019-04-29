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
import com.thienan.lovebox.utils.AppConstants;
import com.thienan.lovebox.utils.PagedResponse;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BffRequestServiceImpl implements BffRequestService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    BffRequestRepository bffRequestRepository;

    @Autowired
    BffDetailRepository bffDetailRepository;

    @Override
    public PagedResponse<BffRequestDto> getSentBffRequestByUserId(Long userId, int page, int size) {
        validatePageNumberAndSize(page, size);

        Pageable pageRequest = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        Page<BffRequestEntity> bffRequestPage = bffRequestRepository.findAllSentBffRequestsByUserId(userId, pageRequest);

        return this.mapToBffRequestDtoPage(bffRequestPage);
    }

    @Override
    public PagedResponse<BffRequestDto> getReceivedBffRequestByUserId(Long userId, int page, int size) {
        validatePageNumberAndSize(page, size);

        Pageable pageRequest = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        Page<BffRequestEntity> bffRequestPage = bffRequestRepository.findAllReceivedBffRequestsByUserId(userId, pageRequest);

        return this.mapToBffRequestDtoPage(bffRequestPage);
    }

    @Override
    public BffRequestDto getBffRequest(Long id) {
        ModelMapper modelMapper = new ModelMapper();

        BffRequestEntity bffRequestEntity = bffRequestRepository.findById(id)
                .orElseThrow(() -> new SingleQuestionServiceException("BFF Request with ID " + id + " not found"));

        BffRequestDto returnBffRequest = modelMapper.map(bffRequestEntity, BffRequestDto.class);
        return returnBffRequest;
    }

    @Override
    public Boolean checkBffRequestExists(Long fromUserId, Long toUserId) {
        BffRequestEntity bffRequestEntity = bffRequestRepository.findByFromUserIdAndToUserId(fromUserId, toUserId)
                .orElse(null);

        return bffRequestEntity != null;
    }

    @Override
    public BffRequestDto getBffRequestByFromUserIdAndToUserId(Long fromUserId, Long toUserId) {
        ModelMapper modelMapper = new ModelMapper();

        BffRequestEntity bffRequestEntity = bffRequestRepository.findByFromUserIdAndToUserId(fromUserId, toUserId)
                .orElse(null);

        BffRequestDto returnBffRequest = modelMapper.map(bffRequestEntity, BffRequestDto.class);
        return returnBffRequest;
    }

    @Override
    public BffRequestDto createBffRequest(BffRequestDto bffRequestDto) {
        ModelMapper modelMapper = new ModelMapper();

        BffRequestEntity bffRequestEntity = modelMapper.map(bffRequestDto, BffRequestEntity.class);
        BffRequestEntity storedBffRequest = bffRequestRepository.save(bffRequestEntity);

        BffRequestDto returnBffRequest = modelMapper.map(storedBffRequest, BffRequestDto.class);
        return returnBffRequest;
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

    private void validatePageNumberAndSize(int page, int size) {
        if (page < 0) {
            throw new SingleQuestionServiceException("Page number cannot be less than zero.");
        }

        if (size > AppConstants.MAX_PAGE_SIZE) {
            throw new SingleQuestionServiceException("Page size must not be greater than " + AppConstants.MAX_PAGE_SIZE);
        }
    }

    private PagedResponse<BffRequestDto> mapToBffRequestDtoPage(Page<BffRequestEntity> bffRequestPage) {
        List<BffRequestEntity> bffRequests = bffRequestPage.getContent();
        List<BffRequestDto> questionDtos = new ArrayList<>();

        ModelMapper modelMapper = new ModelMapper();

        for (BffRequestEntity bffRequestEntity : bffRequests) {
            BffRequestDto bffRequestDto = modelMapper.map(bffRequestEntity, BffRequestDto.class);
            questionDtos.add(bffRequestDto);
        }

        return new PagedResponse<>(questionDtos, bffRequestPage.getNumber(), bffRequestPage.getSize(),
                bffRequestPage.getTotalElements(), bffRequestPage.getTotalPages(),
                bffRequestPage.isFirst(), bffRequestPage.isLast());
    }
}
