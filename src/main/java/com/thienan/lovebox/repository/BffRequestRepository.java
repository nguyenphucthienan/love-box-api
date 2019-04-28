package com.thienan.lovebox.repository;

import com.thienan.lovebox.entity.BffRequestEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface BffRequestRepository extends JpaRepository<BffRequestEntity, Long> {

    Optional<BffRequestEntity> findByFromUserIdAndToUserId(Long fromUserId, Long toUserId);

    @Query(value = "select r from BffRequestEntity r where r.fromUser.id = :userId",
            countQuery = "select count(r) from BffRequestEntity r where r.fromUser.id = :userId")
    Page<BffRequestEntity> findAllSentBffRequestsByUserId(@Param("userId") Long userId, Pageable pageableRequest);

    @Query(value = "select r from BffRequestEntity r where r.toUser.id = :userId",
            countQuery = "select count(r) from BffRequestEntity r where r.toUser.id = :userId")
    Page<BffRequestEntity> findAllReceivedBffRequestsByUserId(@Param("userId") Long userId, Pageable pageableRequest);

    @Transactional
    @Modifying
    @Query("delete from BffRequestEntity b where b.fromUser.id = :userId or b.toUser.id = :userId")
    void deleteAllByUserId(@Param("userId") Long userId);
}
