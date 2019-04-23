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
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

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
        UserEntity userEntity = userRepository.findById(id).orElseThrow(() ->
                new UsernameNotFoundException("User with ID " + id + " not found")
        );

        ModelMapper modelMapper = new ModelMapper();
        UserDto returnUser = modelMapper.map(userEntity, UserDto.class);

        return returnUser;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        if (userRepository.findByUsernameOrEmail(userDto.getUsername(), userDto.getEmail()).isPresent()) {
            throw new UserServiceException("Email already exists");
        }

        ModelMapper modelMapper = new ModelMapper();

        UserEntity userEntity = modelMapper.map(userDto, UserEntity.class);
        userEntity.setPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));

        RoleEntity userRoleEntity = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new UserServiceException("Role not found"));

        userEntity.setRoles(Collections.singleton(userRoleEntity));

        UserEntity storedUser = userRepository.save(userEntity);
        UserDto returnUser = modelMapper.map(storedUser, UserDto.class);

        return returnUser;
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
}
