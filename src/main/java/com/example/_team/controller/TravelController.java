package com.example._team.controller;

import com.example._team.domain.Marker;
import com.example._team.domain.TravelBoard;
import com.example._team.domain.Users;
import com.example._team.domain.enums.Region;
import com.example._team.exception.DataNotFoundException;
import com.example._team.global.s3.AmazonS3Manager;
import com.example._team.repository.MarkerRepository;
import com.example._team.repository.TravelRepository;
import com.example._team.service.TravelService;
import com.example._team.service.UserService;
import com.example._team.web.dto.travelalbum.TravelAlbumRequestDTO.createTravelAlbumDTO;
import com.example._team.web.dto.travelalbum.TravelAlbumResponseDTO;
import com.example._team.web.dto.travelalbum.TravelAlbumResponseDTO.TravelAlbumByDate;
import com.example._team.web.dto.travelalbum.TravelAlbumResponseDTO.TravelAlbumDetailResponseDTO;
import com.example._team.web.dto.travelalbum.TravelAlbumResponseDTO.TravelAlbumListDTO;
import com.example._team.web.dto.travelalbum.TravelAlbumResponseDTO.TravelAlbumResultDTO;
import com.example._team.web.dto.travelalbum.TravelAlbumResponseDTO.myTravelAlbumListDTO;
import com.example._team.web.dto.travelalbum.TravelAlbumUpdateRequestDTO;
import com.example._team.web.dto.user.UserResponseDTO.UserListByPostLikesDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.*;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/travel")
public class TravelController {

    private final TravelService travelService;
    private final UserService userService;
    private final AmazonS3Manager s3ImgService;
    private final MarkerRepository markerRepository;
    private final TravelRepository travelRepository;

    // 테마, 지역별 검색
    @GetMapping("/search")
    public String searchByRegionOrTheme(
            @RequestParam(value = "theme", required = false) String theme,
            @RequestParam(value = "region", required = false) String region,
            Model model) {

        List<TravelAlbumListDTO> albums;

        if (theme != null && !theme.trim().isEmpty() && region != null && !region.trim().isEmpty()) {
            try {
                Region regionEnum = Region.valueOf(region.trim().toUpperCase());
                albums = travelService.searchTravelListByThemeAndRegion(theme, regionEnum, 1);
            } catch (IllegalArgumentException e) {
                albums = List.of();
            }
        } else if (theme != null && !theme.trim().isEmpty()) {
            albums = travelService.searchTravelListByTheme(theme, 1);
        } else if (region != null && !region.trim().isEmpty()) {
            try {
                Region regionEnum = Region.valueOf(region.trim().toUpperCase());
                albums = travelService.searchTravelListByRegion(regionEnum, 1);
            } catch (IllegalArgumentException e) {
                albums = List.of();
            }
        } else {
            albums = List.of();
        }

        model.addAttribute("albums", albums);
        model.addAttribute("region", region);
        model.addAttribute("theme", theme);
        return "view/travel/region-theme-list";
    }


    // 여행앨범 랜덤 리스트 조회
    @GetMapping("/random")
    public String getRandomTravelAlbum(Model model) {

        List<TravelAlbumDetailResponseDTO> randomResponses = travelService.getRandomTravelAlbums();
        // 최신순으로 4개
        List<TravelAlbumByDate> dateRangeRes = travelService.getTravelAlbumByDate();
        model.addAttribute("randomResponses", randomResponses);
        model.addAttribute("dateRangeRes", dateRangeRes);
        return "view/travel/random-list";
    }

    @GetMapping("/editor")
    public String getEditor() {
        return "view/travel/editor";
    }

    @PostMapping("/like/{travelIdx}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addLike(@PathVariable Integer travelIdx,
                                                       @RequestBody Map<String, Integer> payload) {
        return handleLikeAction(travelIdx, payload.get("userIdx"), true);
    }

    @DeleteMapping("/like/{travelIdx}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> removeLike(@PathVariable Integer travelIdx,
                                                          @RequestBody Map<String, Integer> payload) {
        return handleLikeAction(travelIdx, payload.get("userIdx"), false);
    }
    private ResponseEntity<Map<String, Object>> handleLikeAction(Integer travelIdx, Integer userIdx, boolean isLike) {
        boolean success;
        if (isLike) {
            success = travelService.addLike(travelIdx, Long.valueOf(userIdx));
        } else {
            success = travelService.removeLike(travelIdx, Long.valueOf(userIdx));
        }

        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        return ResponseEntity.ok(response);
    }

    // 여행앨범 생성
    @PostMapping("/create")
    public String createTravelAlbum(@ModelAttribute("request") createTravelAlbumDTO request, @RequestParam("albumId") Long albumId,
                                    RedirectAttributes redirectAttributes) {
        System.out.println("왔니?");
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        TravelAlbumResultDTO response = travelService.postTravelAlbum(email, request,albumId);
        redirectAttributes.addAttribute("id", response.getTravelIdx());
        return "redirect:/api/travel/detail/{id}";
    }

    // 여행앨범 생성
    @GetMapping("/upload")
    public String showUploadForm(Model model) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Users user = userService.findByEmail(email);
        TravelAlbumResponseDTO.TravelAlbumResultMapDTO data = travelService.createNullData();
        Long albumId = data.getTravelIdx();
        System.out.println(albumId);
        model.addAttribute("albumId", albumId);
        model.addAttribute("data", data);
        model.addAttribute("user", user);
        return "view/travel/upload";
    }

    // 여행앨범 content 내부 이미지 리스트 업로드
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

    @PostMapping("/edit/upload-image")
    @ResponseBody
    public ResponseEntity<Map<String, String>> uploadImageEditor(@RequestParam("file") MultipartFile file) {
        try {
            String fileName = UUID.randomUUID().toString().substring(0, 10) + "-" + file.getOriginalFilename();
            String keyName = "travel/images/" + fileName;
            String fileUrl = s3ImgService.uploadFile(keyName, file);

            Map<String, String> response = new HashMap<>();
            response.put("url", fileUrl);  // Editor.js가 요구하는 응답 형식

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace(); // 예외 로그
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 단건조회
    @GetMapping("/detail/{id}")
    public String getTravelBoard(@PathVariable Integer id, Model model) throws JsonProcessingException {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Users user = userService.findByEmail(email);

        // 앨범 상세 정보 가져오기
        TravelAlbumDetailResponseDTO response = travelService.getTravelBoard(id, user);
        model.addAttribute("response", response);

        // 앨범에 좋아요를 누른 사용자 목록 가져오기
        List<UserListByPostLikesDTO> userList = travelService.getTravelLikesByUsers(id);
        model.addAttribute("userList", userList);

        TravelBoard travelBoard = travelRepository.findById(Math.toIntExact(response.getId())).orElseThrow(() -> new DataNotFoundException("X"));
        // 앨범에 연관된 마커 리스트 가져오기
        List<Marker> markers = markerRepository.findByTravelBoard1(travelBoard.getId());
        // 마커 리스트를 JSON으로 변환
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());  // JavaTimeModule을 등록

        String markerJson = objectMapper.writeValueAsString(markers);
        model.addAttribute("markers", markerJson);
        model.addAttribute("markers", markerJson);
        model.addAttribute("connectUser", user);

        return "view/travel/detail";
    }

    // 삭제
    @PostMapping("/delete/{travelIdx}")
    public String deleteTravelBoard(@PathVariable Integer travelIdx) {

        travelService.deleteTravelBoard(travelIdx);
        return "redirect:/api/travel/random-list";
    }

    // 나의 앨범 리스트 조회(좋아요, 최신순 정렬)
    @GetMapping("/myTravel")
    public String getMyTravelBoardSort(Model model,
                                       @RequestParam(defaultValue = "latest") String sort) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Users user = userService.findByEmail(email);

        List<myTravelAlbumListDTO> response = travelService.getMyTravelBoardSortList(user, sort);
        model.addAttribute("response", response);

        return "view/travel/my-random-list";
    }

    // 수정 폼 페이지
    @GetMapping("/update/{travelIdx}")
    public String updateTravelBoard(@PathVariable Integer travelIdx, Model model) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Users user = userService.findByEmail(email);
        TravelAlbumDetailResponseDTO response = travelService.getTravelBoard(travelIdx, user);
        String dateRange = response.getDateRange();

        // System.out.println()을 사용하여 isPublic 값 출력 - 현재 콘솔 창: isPublic value: null
        System.out.println("isPublic value: " + response.getIsPublic());

        System.out.println("Thumbnail value: " + response.getThumbnail());

        // 날짜 포맷터 생성
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

        // 문자열을 각각 시작일과 종료일로 분리
        String[] dates = dateRange.split(" - ");

        // 문자열을 LocalDate로 변환
        LocalDate startDate = LocalDate.parse(dates[0], formatter);
        LocalDate endDate = LocalDate.parse(dates[1], formatter);

        model.addAttribute("response", response);
        model.addAttribute("user", user);
        model.addAttribute("startDate",startDate);
        model.addAttribute("endDAte",endDate);
        System.out.println(response.getContent());
        // 공개여부 추가
        model.addAttribute("ispublic", response.getIsPublic());

        return "view/travel/edit";
    }

    // 수정 완료 후
    @PostMapping("/update/{travelIdx}")
    public String submitUpdateTravelBoard(@PathVariable Integer travelIdx,
                                          @ModelAttribute("request") TravelAlbumUpdateRequestDTO request,
                                          RedirectAttributes redirectAttributes) {
        // 현재 로그인한 사용자의 이메일 정보로 사용자 확인
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Users user = userService.findByEmail(email);

        // 여행 보드 업데이트 시도
        boolean updateResult = travelService.updateTravelBoard(travelIdx, request, user);

        if (updateResult) {
            // 수정 성공 시 해당 여행보드 상세 페이지로 리다이렉트
            redirectAttributes.addFlashAttribute("successMessage", "여행앨범이 성공적으로 업데이트 되었습니다.");
            return "redirect:/api/travel/detail/" + travelIdx;
        } else {
            // 수정 실패 시 다시 수정 페이지로 리다이렉트
            redirectAttributes.addFlashAttribute("errorMessage", "여행앨범 업데이트에 실패했습니다.");
            return "redirect:/api/travel/update/" + travelIdx;
        }
    }
}