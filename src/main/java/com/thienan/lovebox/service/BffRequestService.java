package com.thienan.lovebox.service;

import com.thienan.lovebox.shared.dto.BffRequestDto;

public interface BffRequestService {

    BffRequestDto getBffRequest(Long id);

    BffRequestDto createBffRequest(BffRequestDto bffRequestDto);
}
