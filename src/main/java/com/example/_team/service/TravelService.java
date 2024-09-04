package com.example._team.service;

import com.example._team.repository.TravelRepository;
import com.example._team.web.dto.travelalbum.TravelAlbumResponseDTO.TravelAlbumListDTO;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TravelService {
    private final TravelRepository travelRepository;

    public List<TravelAlbumListDTO> searchTravelListByTheme(String theme, Integer isPublic) {
        List<Object[]> results = travelRepository.findAllByThemeName(theme, isPublic);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

        return results.stream().map(record -> {
            Integer id = (Integer) record[0];
            String title = (String) record[1];
            LocalDateTime statDate = ((Timestamp) record[2]).toLocalDateTime();
            LocalDateTime endDate = ((Timestamp) record[3]).toLocalDateTime();
            String thumbnail = (String) record[4];

            System.out.println(thumbnail);
            // 포맷된 날짜 문자열 생성
            String formattedDateRange = statDate.format(formatter) + " - " + endDate.format(formatter);

            return new TravelAlbumListDTO(id, title, formattedDateRange, thumbnail);
        }).collect(Collectors.toList());
    }
}