package com.thienan.lovebox.service;

import com.thienan.lovebox.shared.dto.BffRequestDto;
import com.thienan.lovebox.utils.PagedResponse;

public interface BffRequestService {

    PagedResponse<BffRequestDto> getSentBffRequestByUserId(Long userId, int page, int size);

    PagedResponse<BffRequestDto> getReceivedBffRequestByUserId(Long userId, int page, int size);

    BffRequestDto getBffRequest(Long id);

    Boolean checkBffRequestExists(Long fromUserId, Long toUserId);

    BffRequestDto getBffRequestByFromUserIdAndToUserId(Long fromUserId, Long toUserId);

    BffRequestDto createBffRequest(BffRequestDto bffRequestDto);

    void approveBffRequest(Long id);

    void rejectBffRequest(Long id);

    void deleteBffRequest(Long id);

    void deleteAllBffRequestsByUserId(Long userId);
}
