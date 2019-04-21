package com.thienan.lovebox.payload.response;

import java.util.Date;

public class SingleQuestionReponse {

    private Long id;
    private UserDetailResponse questioner;
    private UserDetailResponse answerer;
    private String questionText;
    private String answerText;
    private boolean answered;
    private Date answeredAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserDetailResponse getQuestioner() {
        return questioner;
    }

    public void setQuestioner(UserDetailResponse questioner) {
        this.questioner = questioner;
    }

    public UserDetailResponse getAnswerer() {
        return answerer;
    }

    public void setAnswerer(UserDetailResponse answerer) {
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
