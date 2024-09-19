package com.example._team.repository;

import com.example._team.domain.Marker;
import com.example._team.domain.TravelBoard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MarkerRepository extends JpaRepository<Marker,Long> {
    @Query(value = "select * from marker where travel_board = :travel_board", nativeQuery = true)
    List<Marker> findByTravelBoard(@Param("travel_board") TravelBoard travel_board);

    @Query(value = "select * from marker where travel_idx = :travel_board", nativeQuery = true)
    List<Marker> findByTravelBoard1(@Param("travel_board") Integer travel_board);
