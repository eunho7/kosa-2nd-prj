package com.example._team.web.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class UserResponseDTO {
    @Builder
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserListByPostLikesDTO {
        Long userIdx;
        String nickname;
    }
}
