package com.example._team.controller;

import com.example._team.service.TravelService;
import com.example._team.web.dto.travelalbum.TravelAlbumResponseDTO.TravelAlbumListDTO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@RequestMapping("/travel")
@RequiredArgsConstructor
//@RestController
@Controller
public class TravelController {
    private final TravelService travelService;

    /*
        테마별 여행 앨범 검색 (API)
     */
    @GetMapping("/theme")
    public String searchTravel(Model model, @RequestParam(value = "theme", defaultValue = "")String theme, Pageable pageable) {
        List<TravelAlbumListDTO> response = travelService.searchTravelListByTheme(theme, 1);
        log.info("Fetched Travel Boards: {}", response);

        model.addAttribute("albums", response);
        model.addAttribute("theme", theme);
        return "view/travel/TravelListByTheme";
    }

    // 테마 검색 (뷰)
    @GetMapping("/themeform")
    public String travelTheme() {
        return "view/travel/SearchByTheme";  // 검색창 페이지
    }
}
