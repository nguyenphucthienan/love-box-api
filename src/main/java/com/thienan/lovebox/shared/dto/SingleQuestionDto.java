package com.thienan.lovebox.shared.dto;

import java.time.Instant;
import java.util.Set;

public class SingleQuestionDto {

    private Long id;
    private Instant createdAt;
    private UserDto questioner;
    private UserDto answerer;
    private String questionText;
    private String answerText;
    private boolean answered;
    private Instant answeredAt;
    private Set<UserDto> loves;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public UserDto getQuestioner() {
        return questioner;
    }

    public void setQuestioner(UserDto questioner) {
        this.questioner = questioner;
    }

    public UserDto getAnswerer() {
        return answerer;
    }

    public void setAnswerer(UserDto answerer) {
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

    public Set<UserDto> getLoves() {
        return loves;
    }

    public void setLoves(Set<UserDto> loves) {
        this.loves = loves;
    }
}
