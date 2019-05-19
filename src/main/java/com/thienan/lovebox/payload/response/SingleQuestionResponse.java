package com.thienan.lovebox.payload.response;

import java.time.Instant;
import java.util.Set;

public class SingleQuestionResponse {

    private Long id;
    private Instant createdAt;
    private UserBriefDetailResponse answerer;
    private String questionText;
    private String answerText;
    private boolean answered;
    private Instant answeredAt;
    private Set<UserBriefDetailResponse> loves;

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

    public UserBriefDetailResponse getAnswerer() {
        return answerer;
    }

    public void setAnswerer(UserBriefDetailResponse answerer) {
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

    public Set<UserBriefDetailResponse> getLoves() {
        return loves;
    }

    public void setLoves(Set<UserBriefDetailResponse> loves) {
        this.loves = loves;
    }
}
