package com.thienan.lovebox.repository;

import com.thienan.lovebox.entity.CoupleQuestionEntity;
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
public interface CoupleQuestionRepository extends JpaRepository<CoupleQuestionEntity, Long> {

    @Query(value = "select q from CoupleQuestionEntity q where (q.firstAnswerer.id in (:userIds) or q.secondAnswerer.id in (:userIds)) and q.answered = true",
            countQuery = "select count(q) from CoupleQuestionEntity q where (q.firstAnswerer.id in (:userIds) or q.secondAnswerer.id in (:userIds)) and q.answered = true")
    Page<CoupleQuestionEntity> findAllAnsweredQuestionsByUserIdsIn(@Param("userIds") Set<Long> userIds,
                                                                   Pageable pageable);

    @Query(value = "select q from CoupleQuestionEntity q where (q.firstAnswerer.id = :userId or q.secondAnswerer.id = :userId) and q.answered = true",
            countQuery = "select count(q) from CoupleQuestionEntity q where (q.firstAnswerer.id = :userId or q.secondAnswerer.id = :userId) and q.answered = true")
    Page<CoupleQuestionEntity> findAllAnsweredQuestionsByUserId(@Param("userId") Long userId,
                                                                Pageable pageable);

    @Query(value = "select q from CoupleQuestionEntity q where (q.firstAnswerer.id = :userId and q.firstAnswerText = null " +
            "or q.secondAnswerer.id = :userId and q.secondAnswerText = null) and q.answered = false",
            countQuery = "select count(q) from CoupleQuestionEntity q where (q.firstAnswerer.id = :userId and q.firstAnswerText = null " +
                    "or q.secondAnswerer.id = :userId and q.secondAnswerText = null) and q.answered = false")
    Page<CoupleQuestionEntity> findAllUnansweredQuestionsByUserId(@Param("userId") Long userId,
                                                                  Pageable pageable);

    @Transactional
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query(value = "update CoupleQuestionEntity q set q.updatedAt = :answeredAt, q.firstAnswerText = :answerText where q.id = :id")
    void answerQuestionByFirstAnswerer(@Param("id") Long id,
                                       @Param("answeredAt") Instant answeredAt,
                                       @Param("answerText") String answerText);

    @Transactional
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query(value = "update CoupleQuestionEntity q set q.updatedAt = :answeredAt, q.secondAnswerText = :answerText where q.id = :id")
    void answerQuestionBySecondAnswerer(@Param("id") Long id,
                                        @Param("answeredAt") Instant answeredAt,
                                        @Param("answerText") String answerText);

    @Transactional
    @Modifying
    @Query(value = "update CoupleQuestionEntity q set q.updatedAt = :answeredAt, q.answeredAt = :answeredAt, q.answered = :answered where q.id = :id")
    void setAnsweredQuestion(@Param("id") Long id,
                             @Param("answeredAt") Instant answeredAt,
                             @Param("answered") boolean answered);

    @Transactional
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query(value = "update CoupleQuestionEntity q set q.updatedAt = :unansweredAt, q.answered = false, " +
            "q.answeredAt = null, q.firstAnswerText = null, q.secondAnswerText = null where q.id = :id")
    void unanswerQuestion(@Param("id") Long id, @Param("unansweredAt") Instant unansweredAt);
}
