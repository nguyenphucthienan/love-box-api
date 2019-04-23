package com.thienan.lovebox.repository;

import com.thienan.lovebox.entity.BffDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BffDetailRepository extends JpaRepository<BffDetailEntity, Long> {
}
