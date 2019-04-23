package com.thienan.lovebox.service.impl;

import com.thienan.lovebox.entity.BffRequestEntity;
import com.thienan.lovebox.exception.service.SingleQuestionServiceException;
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
}
