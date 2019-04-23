package com.thienan.lovebox.service;

import com.thienan.lovebox.shared.dto.BffRequestDto;

public interface BffRequestService {

    BffRequestDto getBffRequest(Long id);

    BffRequestDto createBffRequest(BffRequestDto bffRequestDto);

    Boolean checkBffRequestExists(Long fromUserId, Long toUserId);

    void approveBffRequest(Long id);

    void rejectBffRequest(Long id);

    void deleteBffRequest(Long id);

    void deleteAllBffRequestsByUserId(Long userId);
}
