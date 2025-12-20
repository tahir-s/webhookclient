package com.attribe.webhookclient.pojo.thirdparty.model;

import java.util.Map;

public class Response {
    private String responseType;
    private Map<String, Object> content;

    // Constructor
    public Response() {
    }

    public Response(String responseType, Map<String, Object> content) {
        this.responseType = responseType;
        this.content = content;
    }

    // Getters and Setters
    public String getResponseType() {
        return responseType;
    }

    public void setResponseType(String responseType) {
        this.responseType = responseType;
    }

    public Map<String, Object> getContent() {
        return content;
    }

    public void setContent(Map<String, Object> content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Response{" +
                "responseType='" + responseType + '\'' +
                ", content=" + content +
                '}';
    }
}
