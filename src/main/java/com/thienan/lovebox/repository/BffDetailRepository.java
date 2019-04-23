package com.thienan.lovebox.repository;

import com.thienan.lovebox.entity.BffDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BffDetailRepository extends JpaRepository<BffDetailEntity, Long> {

    Optional<BffDetailEntity> findByFirstUserIdOrSecondUserId(Long firstUserId, Long secondUserId);
}
