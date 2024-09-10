package com.example._team.repository;

import com.example._team.domain.TravelBoard;
import com.example._team.domain.TravelImages;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TravelImageRepository extends JpaRepository<TravelImages, Integer> {
    List<TravelImages> findByTravelIdx(TravelBoard travelBoard);
    void deleteByTravelIdx(TravelBoard travelIdx);
}
