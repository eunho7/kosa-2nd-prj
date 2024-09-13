package com.example._team.repository;

import com.example._team.domain.TravelBoard;
import com.example._team.domain.enums.Region;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TravelRepository extends JpaRepository<TravelBoard, Integer> {
    @Query(value = "select t.id, t.title, t.stat_date, t.end_date, t.thumbnail from travel_board t join theme th "
            + "on th.travel_id = t.id where th.name = :name and t.is_public = :is_public",
            nativeQuery = true)
    List<Object[]> findAllByThemeName(@Param("name") String name, @Param("is_public") Integer is_public);

    List<TravelBoard> findAllByRegionAndIsPublic(Region region, @Param("is_public") Integer is_public);
    @Query(value = "SELECT t.id, t.title, t.stat_date, t.end_date, t.thumbnail FROM travel_board t " +
            "JOIN theme th ON th.travel_id = t.id " +
            "WHERE th.name = :name AND t.is_public = :is_public and t.region = :region", nativeQuery = true)
    List<Object[]> findAllByThemeAndRegionAndIsPublic(@Param("name") String name,
                                                         @Param("region") String region,
                                                         @Param("is_public") Integer is_public);

    @Query(value = "SELECT * FROM (SELECT t.* FROM travel_board t ORDER BY DBMS_RANDOM.VALUE) WHERE ROWNUM = 1", nativeQuery = true)
    TravelBoard findRandomTravelBoard();
}
