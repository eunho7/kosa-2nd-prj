package com.example._team.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example._team.domain.Reply;

public interface ReplyRepository extends JpaRepository<Reply, Integer> {

		@Query(value = "SELECT * FROM (SELECT r.*, ROW_NUMBER() OVER (ORDER BY r.created_at DESC) AS rn FROM reply r WHERE r.board_idx = :boardIdx) WHERE rn BETWEEN :startRow AND :endRow",
		       countQuery = "SELECT COUNT(*) FROM reply r WHERE r.board_idx = :boardIdx",
		       nativeQuery = true)
	    List<Reply> findRepliesByBoardIdxWithPagination(@Param("boardIdx") Integer boardIdx,
	                                                    @Param("startRow") int startRow,
	                                                    @Param("endRow") int endRow,
	                                                    Pageable pageable);
	
	long countByBoardBoardIdx(Integer boardIdx);

}
