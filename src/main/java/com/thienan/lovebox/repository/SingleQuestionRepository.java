package com.thienan.lovebox.repository;

import com.thienan.lovebox.entity.SingleQuestionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SingleQuestionRepository extends JpaRepository<SingleQuestionEntity, Long> {

    @Query(value = "select q from SingleQuestionEntity q where q.answerer.id = :userId and q.answered = :answered",
            countQuery = "select count(q) from SingleQuestionEntity q where q.answerer.id = :userId and q.answered = :answered")
    Page<SingleQuestionEntity> findAllQuestionsByUserId(@Param("userId") Long userId,
                                                        @Param("answered") boolean answered,
                                                        Pageable pageableRequest);
}
