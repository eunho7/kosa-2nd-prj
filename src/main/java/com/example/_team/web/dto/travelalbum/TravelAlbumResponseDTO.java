package com.example._team.web.dto.travelalbum;

import java.util.List;
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

    @Builder
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TravelAlbumDetailResponseDTO {
        Integer id;
        String nickname;
        String dateRange;   // 여행날짜
        String region;
        String thumbnail;
        String title;
        List<TravelAlbumImageListDTO> travelAlbumImageList;
        List<TravelThemeListDTO> travelThemeList;
    }

    @Builder
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TravelAlbumImageListDTO {
        // 일단은 이미지 리스트만 불러오고, 위치정보 잘 가져오면 방문한 장소 리스트 불러오기
        Integer id; // 이미지 아이디
        String imagePath;   // 이미지 경로
    }

    @Builder
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TravelThemeListDTO {
        Integer id;
        String name;    // 테마 이름
    }

    @Builder
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TravelAlbumLikesResultDTO {
        Integer travelLikesIdx;
    }

    @Builder
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TravelAlbumResultDTO {
        Integer travelIdx;
    }
}
