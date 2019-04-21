package com.thienan.lovebox.repository;

import com.thienan.lovebox.entity.SingleQuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SingleQuestionRepository extends JpaRepository<SingleQuestionEntity, Long> {
}
