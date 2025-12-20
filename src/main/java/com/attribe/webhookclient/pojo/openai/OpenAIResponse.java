package com.attribe.webhookclient.pojo.openai;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OpenAIResponse {
    private String id;
    private String object;
    private long created;
    private String model;
    
    @JsonProperty("output")
    private List<Map<String, Object>> output;

    public OpenAIResponse() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public List<Map<String, Object>> getOutput() {
        return output;
    }

    public void setOutput(List<Map<String, Object>> output) {
        this.output = output;
    }

    @Override
    public String toString() {
        return "OpenAIResponse{" +
                "id='" + id + '\'' +
                ", object='" + object + '\'' +
                ", created=" + created +
                ", model='" + model + '\'' +
                ", output=" + output +
                '}';
    }
}
