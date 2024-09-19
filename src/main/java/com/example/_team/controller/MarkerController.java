package com.example._team.controller;

import com.example._team.domain.Marker;
import com.example._team.domain.TravelBoard;
import com.example._team.exception.DataNotFoundException;
import com.example._team.repository.MarkerRepository;
import com.example._team.repository.TravelRepository;
import com.example._team.web.dto.travelalbum.MarkerRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.yaml.snakeyaml.error.Mark;

import java.util.List;

@RestController
@RequestMapping("/api/markers")
public class MarkerController {

    @Autowired
    private MarkerRepository markerRepository;
    @Autowired
    private TravelRepository travelRepository;

    //마커 저장
    @PostMapping("/add")
    public ResponseEntity<String> addMarker(@RequestBody MarkerRequestDTO marker) {

        TravelBoard travelBoard = travelRepository.findById(Math.toIntExact(marker.getTravelBoardId()))
                .orElseThrow(() -> new IllegalArgumentException("Invalid album ID"));
        // Marker 객체 생성 및 TravelBoard 할당
        Marker newMarker = new Marker();
        newMarker.setPlaceName(marker.getPlaceName());
        newMarker.setAddress(marker.getAddress());
        newMarker.setLatitude(marker.getLatitude());
        newMarker.setLongitude(marker.getLongitude());
        newMarker.setMarkerNumber(marker.getMarkerNumber());
        newMarker.setTravelBoard(travelBoard);  // TravelBoard 객체 할당

        // Marker 저장
        Marker sys = markerRepository.save(newMarker);
        System.out.println(sys.getId());

        return ResponseEntity.ok("Marker saved successfully");
    }

    //마커 조회회
    @GetMapping("/all")
    public ResponseEntity<List<Marker>> getAllMarkers() {
        List<Marker> markers = markerRepository.findAll();
        return ResponseEntity.ok(markers);
    }

    // 특정 앨범에 해당하는 마커 데이터 조회
    @GetMapping("/album/{albumId}")
    public ResponseEntity<List<Marker>> getMarkersByAlbumId(@PathVariable Long albumId) {
        TravelBoard travelBoard = travelRepository.findById(Math.toIntExact(albumId))
                .orElseThrow(() -> new DataNotFoundException("X"));
        List<Marker> markers = markerRepository.findByTravelBoard(travelBoard);
        return ResponseEntity.ok(markers);
    }
}
