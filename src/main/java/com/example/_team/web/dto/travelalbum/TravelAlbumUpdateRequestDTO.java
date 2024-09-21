package com.example._team.web.dto.travelalbum;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.example._team.web.dto.travelalbum.TravelAlbumRequestDTO.TravelAlbumThemeListDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TravelAlbumUpdateRequestDTO {
    private String title;
    private String region;
    private LocalDate statDate;
    private LocalDate endDate;
    private MultipartFile thumbnail;
    private String content;
    private Integer isPublic;
    private List<TravelAlbumThemeListDTO> travelThemeList = new ArrayList<>();

}