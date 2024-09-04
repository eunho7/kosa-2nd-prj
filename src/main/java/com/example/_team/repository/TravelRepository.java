package com.example._team.repository;

import com.example._team.domain.TravelBoard;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TravelRepository extends JpaRepository<TravelBoard, Integer> {
    @Query(value = "select t.id, t.title, t.stat_date, t.end_date, t.thumbnail from travel_board t join theme th "
            + "on th.travel_id = t.id where th.name = :name and t.is_public = :is_public",
            nativeQuery = true)
    List<Object[]> findAllByThemeName(@Param("name") String name, @Param("is_public") Integer is_public);
}
