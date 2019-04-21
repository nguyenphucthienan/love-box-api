package com.thienan.lovebox.shared.dto;

import java.util.Date;

public class SingleQuestionDto {

    private Long id;
    private UserDto questioner;
    private UserDto answerer;
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

    public Date getAnsweredAt() {
        return answeredAt;
    }

    public void setAnsweredAt(Date answeredAt) {
        this.answeredAt = answeredAt;
    }
}
