package com.thienan.lovebox.repository;

import com.thienan.lovebox.entity.SingleQuestionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Set;

@Repository
public interface SingleQuestionRepository extends JpaRepository<SingleQuestionEntity, Long> {

    @Query(value = "select q from SingleQuestionEntity q where q.answerer.id in (:userIds) and q.answered = true",
            countQuery = "select count(q) from SingleQuestionEntity q where q.answerer.id in (:userIds) and q.answered = true")
    Page<SingleQuestionEntity> findAllAnsweredQuestionsByUserIdsIn(@Param("userIds") Set<Long> userIds,
                                                                   Pageable pageableRequest);

    @Query(value = "select q from SingleQuestionEntity q where q.answerer.id = :userId and q.answered = :answered",
            countQuery = "select count(q) from SingleQuestionEntity q where q.answerer.id = :userId and q.answered = :answered")
    Page<SingleQuestionEntity> findAllQuestionsByUserId(@Param("userId") Long userId,
                                                        @Param("answered") boolean answered,
                                                        Pageable pageableRequest);

    @Transactional
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query(value = "update SingleQuestionEntity q set q.updatedAt = :answeredAt, q.answered = true, " +
            "q.answeredAt = :answeredAt, q.answerText = :answerText where q.id = :id")
    void answerQuestion(@Param("id") Long id,
                        @Param("answeredAt") Instant answeredAt,
                        @Param("answerText") String answerText);

    @Transactional
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query(value = "update SingleQuestionEntity q set q.updatedAt = :unansweredAt, q.answered = false, " +
            "q.answeredAt = null, q.answerText = null where q.id = :id")
    void unanswerQuestion(@Param("id") Long id, @Param("unansweredAt") Instant unansweredAt);
}
