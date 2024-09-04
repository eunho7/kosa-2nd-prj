package com.example._team.web.dto.travelalbum;

import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class TravelAlbumResponseDTO {
    @Builder
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TravelAlbumListDTO {
        Integer id;
        String title;
        String dateRange;
        String thumbnail;

    }
}
