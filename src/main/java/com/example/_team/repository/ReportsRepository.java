package com.example._team.repository;

import com.example._team.domain.Board;
import com.example._team.domain.Reports;
import lombok.Data;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReportsRepository extends JpaRepository<Reports, Integer> {

    @Query(value = "SELECT * FROM ( SELECT ROWNUM AS RNUM, Z.* FROM (select reports.* from ( reports join board on reports.board_idx = board.board_idx) where board.status = :statusValue order by reports.created_at asc ) Z WHERE  ROWNUM <= :endRow) WHERE  RNUM >= :startRow",
            countQuery = "SELECT COUNT(*) FROM REPORTS",  nativeQuery = true)
    List<Reports> findByStatusAndCreatedAt(@Param("startRow") int startRow, @Param("endRow") int endRow, @Param("statusValue") int statusValue, Pageable pageable);
}

