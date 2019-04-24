package com.thienan.lovebox.repository;

import com.thienan.lovebox.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByUsername(String username);

    Optional<UserEntity> findByUsernameOrEmail(String username, String email);

    List<UserEntity> findByIdIn(List<Long> userIds);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    @Query(value = "select u from UserEntity u where u.username like %:username%",
            countQuery = "select count(u) from UserEntity u where u.username like %:username%")
    Page<UserEntity> findAllByUsername(@Param("username") String username, Pageable pageableRequest);

    @Query(value = "select u from UserEntity u left join u.followers f where f.id = :id",
            countQuery = "select count(u) from UserEntity u left join u.followers f where f.id = :id")
    Page<UserEntity> findAllFollowingById(@Param("id") Long id, Pageable pageableRequest);

    @Query(value = "select u from UserEntity u left join u.following f where f.id = :id",
            countQuery = "select count(u) from UserEntity u left join u.following f where f.id = :id")
    Page<UserEntity> findAllFollowerById(@Param("id") Long id, Pageable pageableRequest);
}
