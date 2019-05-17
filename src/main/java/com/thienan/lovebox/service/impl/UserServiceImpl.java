package com.thienan.lovebox.service.impl;

import com.thienan.lovebox.entity.RoleEntity;
import com.thienan.lovebox.entity.RoleName;
import com.thienan.lovebox.entity.UserEntity;
import com.thienan.lovebox.exception.service.UserServiceException;
import com.thienan.lovebox.repository.RoleRepository;
import com.thienan.lovebox.repository.UserRepository;
import com.thienan.lovebox.security.JwtTokenProvider;
import com.thienan.lovebox.service.UserService;
import com.thienan.lovebox.shared.dto.UserDto;
import com.thienan.lovebox.utils.AppConstants;
import com.thienan.lovebox.utils.PagedResponse;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public String authenticateUser(String usernameOrEmail, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(usernameOrEmail, password)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        return jwtTokenProvider.generateToken(authentication);
    }

    @Override
    public UserDto getUserById(Long id) {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new UserServiceException("User with ID " + id + " not found"));

        return mapToUserDto(userEntity);
    }

    @Override
    public UserDto createUser(UserDto userDto) {

        if (userRepository.findByUsernameOrEmail(userDto.getUsername(), userDto.getEmail()).isPresent()) {
            throw new UserServiceException("Email already exists");
        }

        UserEntity userEntity = mapToUserEntity(userDto);
        userEntity.setPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));

        RoleEntity userRoleEntity = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new UserServiceException("Role not found"));

        userEntity.setRoles(Collections.singleton(userRoleEntity));
        UserEntity savedUser = userRepository.save(userEntity);

        return mapToUserDto(savedUser);
    }

    @Override
    public Boolean checkUsernameAvailability(String username) {
        return !userRepository.existsByUsername(username);
    }

    @Override
    public Boolean checkEmailAvailability(String email) {
        return !userRepository.existsByEmail(email);
    }

    @Override
    public void followOrUnfollowUser(Long id, Long idToFollowOrUnfollow) {
        UserEntity userEntity = userRepository.findById(id).orElseThrow(() ->
                new UsernameNotFoundException("User with ID " + id + " not found")
        );

        UserEntity userEntityToFollowOrUnfollow = userRepository.findById(idToFollowOrUnfollow).orElseThrow(() ->
                new UsernameNotFoundException("User with ID " + id + " not found")
        );

        if (!userEntity.getFollowing().contains(userEntityToFollowOrUnfollow)) {
            userEntity.addFollowing(userEntityToFollowOrUnfollow);
        } else {
            userEntity.removeFollowing(userEntityToFollowOrUnfollow);
        }

        userRepository.save(userEntity);
    }

    @Override
    public Boolean checkUserHasBff(Long id) {
        UserEntity userEntity = userRepository.findById(id).orElseThrow(() ->
                new UsernameNotFoundException("User with ID " + id + " not found")
        );

        return userEntity.getBffDetail() != null;
    }

    @Override
    public Boolean checkUserHasFollow(Long id, Long userId) {
        UserEntity userEntity = userRepository.findById(id).orElseThrow(() ->
                new UsernameNotFoundException("User with ID " + id + " not found")
        );

        UserEntity followedUserEntity = userRepository.findById(userId).orElseThrow(() ->
                new UsernameNotFoundException("User with ID " + id + " not found")
        );

        return userEntity.getFollowing().contains(followedUserEntity);
    }

    @Override
    public PagedResponse<UserDto> findUsers(String username, int page, int size) {
        validatePageNumberAndSize(page, size);

        Pageable pageRequest = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        Page<UserEntity> userPage = userRepository.findAllByUsername(username, pageRequest);

        return mapToUserDtoPage(userPage);
    }

    @Override
    public PagedResponse<UserDto> getFollowing(Long id, int page, int size) {
        validatePageNumberAndSize(page, size);

        Pageable pageRequest = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        Page<UserEntity> userPage = userRepository.findAllFollowingById(id, pageRequest);

        return mapToUserDtoPage(userPage);
    }

    @Override
    public PagedResponse<UserDto> getFollowers(Long id, int page, int size) {
        validatePageNumberAndSize(page, size);

        Pageable pageRequest = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        Page<UserEntity> userPage = userRepository.findAllFollowerById(id, pageRequest);

        return mapToUserDtoPage(userPage);
    }

    private void validatePageNumberAndSize(int page, int size) {
        if (page < 0) {
            throw new UserServiceException("Page number cannot be less than zero.");
        }

        if (size > AppConstants.MAX_PAGE_SIZE) {
            throw new UserServiceException("Page size must not be greater than " + AppConstants.MAX_PAGE_SIZE);
        }
    }

    private UserEntity mapToUserEntity(UserDto userDto) {
        return modelMapper.map(userDto, UserEntity.class);
    }

    private UserDto mapToUserDto(UserEntity userEntity) {
        return modelMapper.map(userEntity, UserDto.class);
    }

    private List<UserDto> mapToListUserDto(List<UserEntity> userEntities) {
        return modelMapper.map(userEntities, new TypeToken<List<UserDto>>() {
        }.getType());
    }

    private PagedResponse<UserDto> mapToUserDtoPage(Page<UserEntity> userPage) {
        List<UserEntity> userEntities = userPage.getContent();
        List<UserDto> userDtos = mapToListUserDto(userEntities);

        return new PagedResponse<>(userDtos, userPage.getNumber(), userPage.getSize(),
                userPage.getTotalElements(), userPage.getTotalPages(),
                userPage.isFirst(), userPage.isLast());
    }
}
