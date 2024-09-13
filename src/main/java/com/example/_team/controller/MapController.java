package com.example._team.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

@Controller
public class MapController {

    private static final String GOOGLE_MAPS_API_KEY = "AIzaSyAmUNKi79QJtTDCBYn4DHDnOi9gnT0KaII";

    // map.html 페이지로 이동시키는 컨트롤러
    @GetMapping("/map")
    public String viewMapPage() {
        return "map"; // map.html 파일을 반환
    }

    @GetMapping("/mapview")
    public String viewMapPage(@RequestParam("albumId") Integer albumId, Model model) {
        // albumId를 모델에 추가하여 뷰에서 사용할 수 있도록 설정
        model.addAttribute("albumId", albumId);
        return "map"; // map.html 파일을 반환
    }

    // 주소 기반으로 Google Maps API를 호출하는 API 엔드포인트
    @GetMapping("/map/view")
    public ResponseEntity<String> geocodeAddress(@RequestParam String address) {
        // Google Geocoding API 호출 URL 생성
        String url = "https://maps.googleapis.com/maps/api/geocode/json?address="
                + address + "&key=" + GOOGLE_MAPS_API_KEY;

        // RestTemplate을 사용하여 Google API 호출
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(url, String.class);

        return ResponseEntity.ok(response);
    }
}
