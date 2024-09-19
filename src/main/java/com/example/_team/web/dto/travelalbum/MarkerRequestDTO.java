package com.example._team.web.dto.travelalbum;

import lombok.Data;

@Data
public class MarkerRequestDTO {
    private String placeName;
    private String address;
    private double latitude;
    private double longitude;
    private int markerNumber;
    private Long travelBoardId;  // albumId를 받기 위한 필드
}