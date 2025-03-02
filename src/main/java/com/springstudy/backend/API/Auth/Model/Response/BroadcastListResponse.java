package com.springstudy.backend.API.Auth.Model.Response;

import com.springstudy.backend.API.Repository.Entity.Broadcast;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class BroadcastListResponse {
    private List<Broadcast> broadcasts;
}
