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
import com.thienan.lovebox.utils.PagedResponse;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public UserDto updateUser(Long id, UserDto userDto) {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new UserServiceException("User with ID " + id + " not found"));

        userEntity.setEmail(userDto.getEmail());
        userEntity.setFirstName(userDto.getFirstName());
        userEntity.setLastName(userDto.getLastName());

        UserEntity savedUserEntity = userRepository.save(userEntity);
        return mapToUserDto(savedUserEntity);
    }

    @Override
    public UserDto changeUserPassword(Long id, String newPassword) {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new UserServiceException("User with ID " + id + " not found"));

        userEntity.setPassword(bCryptPasswordEncoder.encode(newPassword));
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
    public PagedResponse<UserDto> searchUsers(String username, Pageable pageable) {
        Page<UserEntity> userPage = userRepository.findAllByUsername(username, pageable);
        return mapToUserDtoPage(userPage);
    }

    @Override
    public PagedResponse<UserDto> getFollowing(Long id, Pageable pageable) {
        Page<UserEntity> userPage = userRepository.findAllFollowingById(id, pageable);
        return mapToUserDtoPage(userPage);
    }

    @Override
    public PagedResponse<UserDto> getFollowers(Long id, Pageable pageable) {
        Page<UserEntity> userPage = userRepository.findAllFollowerById(id, pageable);
        return mapToUserDtoPage(userPage);
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

    private PagedResponse<UserDto> mapToUserDtoPage(Page<UserEntity> userEntityPage) {
        List<UserEntity> userEntities = userEntityPage.getContent();
        List<UserDto> userDtos = mapToListUserDto(userEntities);

        return new PagedResponse<>(userDtos, userEntityPage.getNumber(), userEntityPage.getSize(),
                userEntityPage.getTotalElements(), userEntityPage.getTotalPages(),
                userEntityPage.isFirst(), userEntityPage.isLast());
    }
}
