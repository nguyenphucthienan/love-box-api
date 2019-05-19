package com.thienan.lovebox.repository;

import com.thienan.lovebox.entity.CoupleQuestionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CoupleQuestionRepository extends JpaRepository<CoupleQuestionEntity, Long> {

    @Query(value = "select q from CoupleQuestionEntity q where (q.firstAnswerer.id = :userId or q.secondAnswerer.id = :userId) and q.answered = :answered",
            countQuery = "select count(q) from CoupleQuestionEntity q where (q.firstAnswerer.id = :userId or q.secondAnswerer.id = :userId) and q.answered = :answered")
    Page<CoupleQuestionEntity> findAllQuestionsByUserId(@Param("userId") Long userId,
                                                        @Param("answered") boolean answered,
                                                        Pageable pageable);
}
