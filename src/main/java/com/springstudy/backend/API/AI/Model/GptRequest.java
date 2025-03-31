package com.springstudy.backend.API.AI.Model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class GptRequest {
    private String model;
    private List<Message> messages;
    public GptRequest(String model, String messages) {
        this.model = model;
        this.messages = new ArrayList<>();
        this.messages.add(new Message("user", messages));
    }
}
