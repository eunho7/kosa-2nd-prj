package com.example._team.web.dto.travelalbum;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

public class TravelAlbumRequestDTO {
    @Data
    public static class createTravelAlbumDTO {
        String title;
        String region;
        LocalDate statDate;
        LocalDate endDate;
        MultipartFile thumbnail;
        String content;
        Integer isPublic;
        private List<TravelAlbumThemeListDTO> travelThemeList = new ArrayList<>();  // 테마 리스트
    }

    @Data
    public static class TravelAlbumThemeListDTO {
        String name;    // 테마 이름
    }
}