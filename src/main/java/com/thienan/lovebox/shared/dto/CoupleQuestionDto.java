package com.thienan.lovebox.shared.dto;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

public class CoupleQuestionDto {

    private Long id;
    private Instant createdAt;
    private UserDto questioner;
    private UserDto firstAnswerer;
    private UserDto secondAnswerer;
    private String questionText;
    private String firstAnswerText;
    private String secondAnswerText;
    private boolean answered;
    private Instant answeredAt;
    private Set<UserDto> loves = new HashSet<>();

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

    public UserDto getFirstAnswerer() {
        return firstAnswerer;
    }

    public void setFirstAnswerer(UserDto firstAnswerer) {
        this.firstAnswerer = firstAnswerer;
    }

    public UserDto getSecondAnswerer() {
        return secondAnswerer;
    }

    public void setSecondAnswerer(UserDto secondAnswerer) {
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

    public Set<UserDto> getLoves() {
        return loves;
    }

    public void setLoves(Set<UserDto> loves) {
        this.loves = loves;
    }
}
