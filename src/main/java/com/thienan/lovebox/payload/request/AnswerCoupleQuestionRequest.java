package com.thienan.lovebox.payload.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class AnswerCoupleQuestionRequest {

    @NotBlank
    @Size(max = 200)
    private String answerText;

    public String getAnswerText() {
        return answerText;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }
}
