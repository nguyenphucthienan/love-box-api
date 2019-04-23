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
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BffRequestServiceImpl implements BffRequestService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    BffRequestRepository bffRequestRepository;

    @Autowired
    BffDetailRepository bffDetailRepository;

    @Override
    public BffRequestDto getBffRequest(Long id) {
        ModelMapper modelMapper = new ModelMapper();

        BffRequestEntity bffRequestEntity = bffRequestRepository.findById(id)
                .orElseThrow(() -> new SingleQuestionServiceException("BFF Request with ID " + id + " not found"));

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
    public Boolean checkBffRequestExists(Long fromUserId, Long toUserId) {
        BffRequestEntity bffRequestEntity = bffRequestRepository.findByFromUserIdAndToUserId(fromUserId, toUserId)
                .orElse(null);

        return bffRequestEntity != null;
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
}
