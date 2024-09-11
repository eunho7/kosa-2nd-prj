package com.example._team.repository;

import com.example._team.domain.TravelBoard;
import com.example._team.domain.TravelLikes;
import com.example._team.domain.Users;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TravelLikesRepository extends JpaRepository<TravelLikes, Integer> {
    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN TRUE ELSE FALSE END " +
            "FROM TravelLikes t " +
            "WHERE t.userIdx = :userIdx AND t.travelIdx = :travelIdx")
    boolean existsByUserIdxAndTravelIdx(@Param("userIdx")Users userIdx, @Param("travelIdx")TravelBoard travelIdx);
    TravelLikes findByUserIdxAndTravelIdx(Users userIdx, TravelBoard travelIdx);
    Long countAllByTravelIdx(@Param("travelIdx") TravelBoard travelIdx);
    List<TravelLikes> findUsersByTravelIdx(@Param("travelIdx") TravelBoard travelIdx);
    void deleteByTravelIdx(TravelBoard travelIdx);

}
