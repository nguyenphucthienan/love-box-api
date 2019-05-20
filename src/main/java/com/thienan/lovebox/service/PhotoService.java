package com.thienan.lovebox.service;

import com.thienan.lovebox.shared.dto.PhotoDto;
import org.springframework.web.multipart.MultipartFile;

public interface PhotoService {

    PhotoDto uploadFile(MultipartFile multipartFile);
}
