package com.thienan.lovebox.payload.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class AskCoupleQuestionRequest {

    @NotBlank
    @Size(max = 200)
    private String questionText;

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }
}
