package com.example._team.repository;

import com.example._team.domain.TravelBoard;
import com.example._team.domain.TravelLikes;
import com.example._team.domain.Users;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TravelLikesRepository extends JpaRepository<TravelLikes, Integer> {
    boolean existsByUserIdxAndTravelIdx(Users userIdx, TravelBoard travelIdx);
    TravelLikes findByUserIdxAndTravelIdx(Users userIdx, TravelBoard travelIdx);

}
