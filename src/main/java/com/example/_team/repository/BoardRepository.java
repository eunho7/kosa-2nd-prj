package com.example._team.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example._team.domain.Board;
import com.example._team.domain.enums.Category;

public interface BoardRepository extends JpaRepository<Board, Integer> {

	List<Board> findByTitleContaining(String keyword);

	@Query("SELECT b FROM Board b WHERE b.category = :category ORDER BY b.boardIdx DESC")
	List<Board> findByCategoryOrderByBoardIdxDesc(@Param("category") Category category);

	List<Board> findAllByOrderByViewsDesc();

//	List<Board> findAll();
	
//	@Query(value = "SELECT * FROM (SELECT b.*, ROW_NUMBER() OVER (ORDER BY b.board_idx DESC) AS rn FROM board b) WHERE rn BETWEEN :startRow AND :endRow",
//	           countQuery = "SELECT COUNT(*) FROM board",
//	           nativeQuery = true)
@Query(value = "SELECT * FROM ( SELECT b.*, ROW_NUMBER() OVER (ORDER BY CASE  WHEN b.answer_board_idx IS NULL THEN b.board_idx ELSE b.answer_board_idx END DESC, b.answer_board_idx NULLS FIRST, b.board_idx ASC) AS rn FROM board b START WITH b.answer_board_idx IS NULL CONNECT BY PRIOR b.board_idx = b.answer_board_idx) WHERE rn BETWEEN :startRow AND :endRow",
		countQuery = "SELECT COUNT(*) FROM board",
		nativeQuery = true)
	    List<Board> findAllOrderedByBoardIdx(@Param("startRow") int startRow, @Param("endRow") int endRow, Pageable pageable);

}
