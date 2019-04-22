package com.thienan.lovebox.entity;

import com.thienan.lovebox.entity.audit.DateAudit;

import javax.persistence.*;
import java.util.Date;

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

    @Column(nullable = false)
    private String questionText;

    private String answerText;

    private boolean answered;

    private Date answeredAt;

    public SingleQuestionEntity() {
    }

    public SingleQuestionEntity(UserEntity questioner, UserEntity answerer, String questionText,
                                String answerText, boolean answered, Date answeredAt) {
        this.questioner = questioner;
        this.answerer = answerer;
        this.questionText = questionText;
        this.answerText = answerText;
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

    public Date getAnsweredAt() {
        return answeredAt;
    }

    public void setAnsweredAt(Date answeredAt) {
        this.answeredAt = answeredAt;
    }
}
