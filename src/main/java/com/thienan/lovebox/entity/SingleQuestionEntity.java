package com.thienan.lovebox.entity;

import com.thienan.lovebox.entity.audit.DateAudit;

import javax.persistence.*;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "single_questions")
public class SingleQuestionEntity extends DateAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "questioner_id")
    private UserEntity questioner;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "answerer_id")
    private UserEntity answerer;

    @Column(nullable = false, length = 50)
    private String questionText;

    @Column(length = 50)
    private String answerText;

    private boolean answered;

    private Instant answeredAt;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "single_question_loves",
            joinColumns = @JoinColumn(name = "single_question_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<UserEntity> loves;

    public SingleQuestionEntity() {
    }

    public SingleQuestionEntity(UserEntity questioner, UserEntity answerer,
                                String questionText, String answerText, boolean answered, Instant answeredAt) {
        this.questioner = questioner;
        this.answerer = answerer;
        this.questionText = questionText;
        this.answerText = answerText;
        this.answered = answered;
        this.answeredAt = answeredAt;
        this.loves = new HashSet<>();
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

    public UserEntity getAnswerer() {
        return answerer;
    }

    public void setAnswerer(UserEntity answerer) {
        this.answerer = answerer;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getAnswerText() {
        return answerText;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
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
