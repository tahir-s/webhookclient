package com.attribe.webhookclient.pojo.openai;

public class OpenAIRequest {
    private String model;
    private String input;

    public OpenAIRequest() {
    }

    public OpenAIRequest(String model, String userMessage) {
        this.model = model;
        this.input = userMessage;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    @Override
    public String toString() {
        return "OpenAIRequest{" +
                "model='" + model + '\'' +
                ", input='" + input + '\'' +
                '}';
    }
}
