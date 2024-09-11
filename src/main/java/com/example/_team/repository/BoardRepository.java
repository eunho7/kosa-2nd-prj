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

	// 게시판 조회수별 정렬
	@Query(value = "SELECT * FROM (SELECT b.*, ROW_NUMBER() OVER (ORDER BY b.views DESC) AS rn FROM board b) WHERE rn BETWEEN :startRow AND :endRow", countQuery = "SELECT COUNT(*) FROM board", nativeQuery = true)
	List<Board> findAllOrderedByViews(@Param("startRow") int startRow, @Param("endRow") int endRow, Pageable pageable);

	// 게시판 카테고리별 정렬
	@Query(value = "SELECT * FROM (SELECT b.*, ROW_NUMBER() OVER (ORDER BY b.board_idx DESC) AS rn FROM board b "
			+ "WHERE b.category = :category) "
			+ "WHERE rn BETWEEN :startRow AND :endRow", countQuery = "SELECT COUNT(*) FROM board WHERE category = :category", nativeQuery = true)
	List<Board> findByCategoryOrderedByBoardIdx(@Param("category") String category, @Param("startRow") int startRow,
			@Param("endRow") int endRow, Pageable pageable);

	// 게시판 카테고리별 + 조회수별 정렬
	@Query(value = "SELECT * FROM (SELECT b.*, ROW_NUMBER() OVER (ORDER BY b.views DESC) AS rn FROM board b "
			+ "WHERE b.category = :category) "
			+ "WHERE rn BETWEEN :startRow AND :endRow", countQuery = "SELECT COUNT(*) FROM board WHERE category = :category", nativeQuery = true)
	List<Board> findByCategoryOrderedByViews(@Param("category") String category, @Param("startRow") int startRow,
			@Param("endRow") int endRow, Pageable pageable);

	// 게시판 리스트 page
	@Query(value = "SELECT * FROM (SELECT b.*, ROW_NUMBER() OVER (ORDER BY b.board_idx DESC) AS rn FROM board b) WHERE rn BETWEEN :startRow AND :endRow", countQuery = "SELECT COUNT(*) FROM board", nativeQuery = true)
	List<Board> findAllOrderedByBoardIdx(@Param("startRow") int startRow, @Param("endRow") int endRow,
			Pageable pageable);

	// 키워드 검색
	@Query(value = "SELECT * FROM (SELECT b.*, ROW_NUMBER() OVER (ORDER BY b.board_idx DESC) AS rn FROM board b "
			+ "WHERE b.title LIKE '%' || :keyword || '%' OR b.content LIKE '%' || :keyword || '%') "
			+ "WHERE rn BETWEEN :startRow AND :endRow", countQuery = "SELECT COUNT(*) FROM board WHERE title LIKE '%' || :keyword || '%' OR content LIKE '%' || :keyword || '%'", nativeQuery = true)
	List<Board> findByKeywordOrderedByBoardIdx(@Param("keyword") String keyword, @Param("startRow") int startRow,
			@Param("endRow") int endRow, Pageable pageable);

	// 키워드로 검색한 게시글의 개수
	@Query("SELECT COUNT(b) FROM Board b WHERE b.title LIKE CONCAT('%', :keyword, '%') OR b.content LIKE CONCAT('%', :keyword, '%')")
	long countByKeyword(@Param("keyword") String keyword);

	// 카테고리로 검색한 게시글의 개수
	@Query("SELECT COUNT(b) FROM Board b WHERE b.category = :category")
	long countByCategory(@Param("category") Category category);

}
