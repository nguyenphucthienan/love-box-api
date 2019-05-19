package com.thienan.lovebox.payload.response;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

public class CoupleQuestionResponse {

    private Long id;
    private Instant createdAt;
    private UserBriefDetailResponse firstAnswerer;
    private UserBriefDetailResponse secondAnswerer;
    private String questionText;
    private String firstAnswerText;
    private String secondAnswerText;
    private boolean answered;
    private Instant answeredAt;
    private Set<UserBriefDetailResponse> loves = new HashSet<>();

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

    public UserBriefDetailResponse getFirstAnswerer() {
        return firstAnswerer;
    }

    public void setFirstAnswerer(UserBriefDetailResponse firstAnswerer) {
        this.firstAnswerer = firstAnswerer;
    }

    public UserBriefDetailResponse getSecondAnswerer() {
        return secondAnswerer;
    }

    public void setSecondAnswerer(UserBriefDetailResponse secondAnswerer) {
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

    public Set<UserBriefDetailResponse> getLoves() {
        return loves;
    }

    public void setLoves(Set<UserBriefDetailResponse> loves) {
        this.loves = loves;
    }
}
