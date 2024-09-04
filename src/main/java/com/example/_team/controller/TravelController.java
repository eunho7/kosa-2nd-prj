package com.example._team.controller;

import com.example._team.domain.enums.Region;
import com.example._team.service.TravelService;
import com.example._team.web.dto.travelalbum.TravelAlbumResponseDTO.TravelAlbumListDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/travel")
public class TravelController {

    private final TravelService travelService;

    @GetMapping("/searchform")
    public String travelTheme() {
        return "view/travel/SearchByTheme";  // 검색창 페이지
    }

    @GetMapping("/search")
    public String searchByRegionOrTheme(
            @RequestParam(value = "theme", required = false) String theme,
            @RequestParam(value = "region", required = false) String region,
            Model model) {

        List<TravelAlbumListDTO> albums;

        if (theme != null && !theme.trim().isEmpty()) {
            // 테마 검색
            System.out.println("Searching by theme: " + theme);
            albums = travelService.searchTravelListByTheme(theme, 1);
        } else if (region != null && !region.trim().isEmpty()) {
            // 지역 검색
            System.out.println("Searching by region: " + region);
            try {
                // 공백 제거 및 대문자로 변환
                Region regionEnum = Region.valueOf(region.trim().toUpperCase());
                System.out.println("Region enum: " + regionEnum);
                albums = travelService.searchTravelListByRegion(regionEnum, 1);
            } catch (IllegalArgumentException e) {
                // 유효하지 않은 지역 값
                System.err.println("Invalid region value: " + region);
                albums = List.of(); // 빈 리스트 반환
            }
        } else {
            // 테마와 지역이 모두 없는 경우
            albums = List.of();
        }

        model.addAttribute("albums", albums);
        return "view/travel/TravelListByTheme";
    }
}