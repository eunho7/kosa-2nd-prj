package com.example._team.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example._team.domain.Board;
import com.example._team.domain.enums.Category;

public interface BoardRepository extends JpaRepository<Board, Integer> {

    // 게시판 조회수별 정렬 (공개된 게시글만)
    @Query(value = "SELECT * FROM (SELECT b.*, ROW_NUMBER() OVER (ORDER BY b.views DESC) AS rn FROM board b WHERE b.status = 1) WHERE rn BETWEEN :startRow AND :endRow", countQuery = "SELECT COUNT(*) FROM board WHERE status = 1", nativeQuery = true)
    List<Board> findAllOrderedByViews(@Param("startRow") int startRow, @Param("endRow") int endRow, Pageable pageable);

    // 게시판 카테고리별 정렬 (공개된 게시글만)
    @Query(value = "SELECT * FROM (SELECT b.*, ROW_NUMBER() OVER (ORDER BY CASE WHEN b.answer_board_idx IS NULL THEN b.board_idx ELSE b.answer_board_idx END DESC, b.answer_board_idx NULLS FIRST, b.board_idx ASC) AS rn FROM board b WHERE b.category = :category AND b.status = 1) WHERE rn BETWEEN :startRow AND :endRow", countQuery = "SELECT COUNT(*) FROM board WHERE category = :category AND status = 1", nativeQuery = true)
    List<Board> findByCategoryOrderedByBoardIdx(@Param("category") String category, @Param("startRow") int startRow, @Param("endRow") int endRow, Pageable pageable);

    // 게시판 카테고리별 + 조회수별 정렬 (공개된 게시글만)
    @Query(value = "SELECT * FROM (SELECT b.*, ROW_NUMBER() OVER (ORDER BY b.views DESC) AS rn FROM board b WHERE b.category = :category AND b.status = 1) WHERE rn BETWEEN :startRow AND :endRow", countQuery = "SELECT COUNT(*) FROM board WHERE category = :category AND status = 1", nativeQuery = true)
    List<Board> findByCategoryOrderedByViews(@Param("category") String category, @Param("startRow") int startRow, @Param("endRow") int endRow, Pageable pageable);

    // 키워드 검색 (공개된 게시글만)
    @Query(value = "SELECT * FROM (SELECT b.*, ROW_NUMBER() OVER (ORDER BY b.board_idx DESC) AS rn FROM board b WHERE (b.title LIKE '%' || :keyword || '%' OR b.content LIKE '%' || :keyword || '%') AND b.status = 1) WHERE rn BETWEEN :startRow AND :endRow", countQuery = "SELECT COUNT(*) FROM board WHERE (title LIKE '%' || :keyword || '%' OR content LIKE '%' || :keyword || '%') AND status = 1", nativeQuery = true)
    List<Board> findByKeywordOrderedByBoardIdx(@Param("keyword") String keyword, @Param("startRow") int startRow, @Param("endRow") int endRow, Pageable pageable);

    // 키워드와 상태별로 게시글의 개수 계산 (공개된 게시글만)
    @Query("SELECT COUNT(b) FROM Board b WHERE (b.title LIKE CONCAT('%', :keyword, '%') OR b.content LIKE CONCAT('%', :keyword, '%')) AND b.status = :status")
    long countByKeywordAndStatus(@Param("keyword") String keyword, @Param("status") int status);

    // 카테고리와 상태별로 게시글의 개수 계산 (공개된 게시글만)
    @Query("SELECT COUNT(b) FROM Board b WHERE b.category = :category AND b.status = :status")
    long countByCategoryAndStatus(@Param("category") Category category, @Param("status") int status);

    // 상태별로 게시글의 개수 계산 (공개된 게시글만)
    @Query("SELECT COUNT(b) FROM Board b WHERE b.status = :status")
    long countByStatus(@Param("status") int status);

    // 답변 게시판 정렬 (공개된 답변만)
    @Query("SELECT b FROM Board b WHERE b.answerBoardIdx.boardIdx = :boardIdx AND b.status = 1 ORDER BY b.createdAt ASC")
    List<Board> findAnswersByBoard(@Param("boardIdx") Integer boardIdx);
    
    // 게시판 리스트 정렬 (공개된 게시글만)
    @Query(value = "SELECT * FROM ( SELECT b.*, ROW_NUMBER() OVER (ORDER BY CASE WHEN b.answer_board_idx IS NULL THEN b.board_idx ELSE b.answer_board_idx END DESC, b.answer_board_idx NULLS FIRST, b.board_idx ASC) AS rn FROM board b WHERE b.status = 1 START WITH b.answer_board_idx IS NULL CONNECT BY PRIOR b.board_idx = b.answer_board_idx) WHERE rn BETWEEN :startRow AND :endRow", countQuery = "SELECT COUNT(*) FROM board WHERE status = 1", nativeQuery = true)
    List<Board> findAllOrderedByBoardIdx(@Param("startRow") int startRow, @Param("endRow") int endRow, Pageable pageable);
}

