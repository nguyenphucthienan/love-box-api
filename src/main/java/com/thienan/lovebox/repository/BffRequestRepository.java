package com.thienan.lovebox.repository;

import com.thienan.lovebox.entity.BffRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BffRequestRepository extends JpaRepository<BffRequestEntity, Long> {
}
