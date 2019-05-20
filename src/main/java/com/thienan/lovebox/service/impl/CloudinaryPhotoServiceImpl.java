package com.thienan.lovebox.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.thienan.lovebox.entity.PhotoEntity;
import com.thienan.lovebox.repository.PhotoRepository;
import com.thienan.lovebox.service.PhotoService;
import com.thienan.lovebox.shared.dto.PhotoDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
public class CloudinaryPhotoServiceImpl implements PhotoService {

    @Autowired
    PhotoRepository photoRepository;

    @Autowired
    Cloudinary cloudinary;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public PhotoDto uploadFile(MultipartFile multipartFile) {
        try {
            Map uploadResult = cloudinary.uploader().upload(multipartFile.getBytes(), ObjectUtils.emptyMap());
            PhotoEntity photoEntity = new PhotoEntity(uploadResult.get("url").toString());
            PhotoEntity savedPhotoEntity = photoRepository.save(photoEntity);
            return mapToPhotoDto(savedPhotoEntity);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private PhotoDto mapToPhotoDto(PhotoEntity photoEntity) {
        return modelMapper.map(photoEntity, PhotoDto.class);
    }
}
