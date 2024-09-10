package com.example._team.controller;

import com.example._team.domain.Users;
import com.example._team.domain.enums.Region;
import com.example._team.global.s3.AmazonS3Manager;
import com.example._team.service.TravelService;
import com.example._team.service.UserService;
import com.example._team.web.dto.travelalbum.TravelAlbumRequestDTO.createTravelAlbumDTO;
import com.example._team.web.dto.travelalbum.TravelAlbumResponseDTO.TravelAlbumDetailResponseDTO;
import com.example._team.web.dto.travelalbum.TravelAlbumResponseDTO.TravelAlbumLikesResultDTO;
import com.example._team.web.dto.travelalbum.TravelAlbumResponseDTO.TravelAlbumListDTO;
import com.example._team.web.dto.travelalbum.TravelAlbumResponseDTO.TravelAlbumResultDTO;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Controller
//@RestController
@RequiredArgsConstructor
@RequestMapping("/api/travel")
public class TravelController {

    private final TravelService travelService;
    private final UserService userService;
    private final AmazonS3Manager s3ImgService;

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

        if (theme != null && !theme.trim().isEmpty() && region != null && !region.trim().isEmpty()) {
            // 테마와 지역 모두 검색
            try {
                // 공백 제거 및 대문자로 변환
                Region regionEnum = Region.valueOf(region.trim().toUpperCase());
                albums = travelService.searchTravelListByThemeAndRegion(theme, regionEnum, 1);
            } catch (IllegalArgumentException e) {
                // 유효하지 않은 지역 값
                albums = List.of(); // 빈 리스트 반환
            }
        } else if (theme != null && !theme.trim().isEmpty()) {
            // 테마 검색
            albums = travelService.searchTravelListByTheme(theme, 1);
        } else if (region != null && !region.trim().isEmpty()) {
            // 지역 검색
            try {
                // 공백 제거 및 대문자로 변환
                Region regionEnum = Region.valueOf(region.trim().toUpperCase());
                albums = travelService.searchTravelListByRegion(regionEnum, 1);
            } catch (IllegalArgumentException e) {
                // 유효하지 않은 지역 값
                albums = List.of(); // 빈 리스트 반환
            }
        } else {
            // 테마와 지역이 모두 없는 경우
            albums = List.of();
        }

        model.addAttribute("albums", albums);
        model.addAttribute("region", region);
        model.addAttribute("theme", theme);
        return "view/travel/TravelListByTheme";
    }


    @GetMapping("/random")
    public String getRandomTravelAlbum(Model model) {

        TravelAlbumDetailResponseDTO response = travelService.getRandomTravelAlbum();
        model.addAttribute("response", response);
        return "view/travel/TravelAlbumRandom";
    }

    @PostMapping("/likes/{travelIdx}")
    public TravelAlbumLikesResultDTO postTravelAlbumLikes(@PathVariable(name = "travelIdx")Integer travelIdx) {
        TravelAlbumLikesResultDTO response = travelService.postAlbumLikes(travelIdx);
        return response;
    }

    @DeleteMapping("/likes/{travelIdx}")
    public TravelAlbumLikesResultDTO deleteTravelAlbumLikes(@PathVariable(name = "travelIdx") Integer travelIdx) {
        TravelAlbumLikesResultDTO response = travelService.cancelTravelAlbumLikes(travelIdx);
        return response;
    }

    /*
        여행앨범 생성
     */
    @PostMapping("/create")
    public String createTravelAlbum(@ModelAttribute("request")createTravelAlbumDTO request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        travelService.postTravelAlbum(email, request);
        return "view/travel/TravelUpload";
    }
    @GetMapping("/upload")
    public String showUploadForm(Model model) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Users user = userService.findByEmail(email);
        model.addAttribute("user", user);
        return "view/travel/TravelUpload";
    }
    @PostMapping("/upload-image")
    @ResponseBody
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String fileName = UUID.randomUUID().toString().substring(0, 10) + "-" + file.getOriginalFilename();
            String keyName = "travel/images/" + fileName;
            String fileUrl = s3ImgService.uploadFile(keyName, file);

            Map<String, String> response = new HashMap<>();
            response.put("location", fileUrl);  // TinyMCE가 요구하는 응답 형식

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}