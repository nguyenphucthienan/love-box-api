package com.thienan.lovebox.entity;

import com.thienan.lovebox.entity.audit.DateAudit;

import javax.persistence.*;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "couple_questions")
public class CoupleQuestionEntity extends DateAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "questioner_id")
    private UserEntity questioner;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "first_answerer_id")
    private UserEntity firstAnswerer;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "second_answerer_id")
    private UserEntity secondAnswerer;

    @Column(nullable = false, length = 200)
    private String questionText;

    @Column(length = 2000)
    private String firstAnswerText;

    @Column(length = 2000)
    private String secondAnswerText;

    private boolean answered;

    private Instant answeredAt;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "couple_question_loves",
            joinColumns = @JoinColumn(name = "couple_question_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<UserEntity> loves = new HashSet<>();

    public CoupleQuestionEntity() {
    }

    public CoupleQuestionEntity(UserEntity questioner, UserEntity firstAnswerer, UserEntity secondAnswerer,
                                String questionText, String firstAnswerText, String secondAnswerText,
                                boolean answered, Instant answeredAt) {
        this.questioner = questioner;
        this.firstAnswerer = firstAnswerer;
        this.secondAnswerer = secondAnswerer;
        this.questionText = questionText;
        this.firstAnswerText = firstAnswerText;
        this.secondAnswerText = secondAnswerText;
        this.answered = answered;
        this.answeredAt = answeredAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserEntity getQuestioner() {
        return questioner;
    }

    public void setQuestioner(UserEntity questioner) {
        this.questioner = questioner;
    }

    public UserEntity getFirstAnswerer() {
        return firstAnswerer;
    }

    public void setFirstAnswerer(UserEntity firstAnswerer) {
        this.firstAnswerer = firstAnswerer;
    }

    public UserEntity getSecondAnswerer() {
        return secondAnswerer;
    }

    public void setSecondAnswerer(UserEntity secondAnswerer) {
        this.secondAnswerer = secondAnswerer;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getFirstAnswerText() {
        return firstAnswerText;
    }

    public void setFirstAnswerText(String firstAnswerText) {
        this.firstAnswerText = firstAnswerText;
    }

    public String getSecondAnswerText() {
        return secondAnswerText;
    }

    public void setSecondAnswerText(String secondAnswerText) {
        this.secondAnswerText = secondAnswerText;
    }

    public boolean isAnswered() {
        return answered;
    }

    public void setAnswered(boolean answered) {
        this.answered = answered;
    }

    public Instant getAnsweredAt() {
        return answeredAt;
    }

    public void setAnsweredAt(Instant answeredAt) {
        this.answeredAt = answeredAt;
    }

    public Set<UserEntity> getLoves() {
        return loves;
    }

    public void setLoves(Set<UserEntity> loves) {
        this.loves = loves;
    }
}
