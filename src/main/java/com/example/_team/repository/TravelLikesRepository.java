package com.example._team.repository;

import com.example._team.domain.TravelBoard;
import com.example._team.domain.TravelLikes;
import com.example._team.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TravelLikesRepository extends JpaRepository<TravelLikes, Integer> {
    boolean existsByUserIdxAndTravelIdx(Users userIdx, TravelBoard travelIdx);
}
