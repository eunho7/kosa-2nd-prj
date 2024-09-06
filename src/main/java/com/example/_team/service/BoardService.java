package com.example._team.service;

import com.example._team.domain.Board;
import com.example._team.domain.enums.Category;
import com.example._team.dto.board.BoardRequestDto;
import com.example._team.dto.board.BoardResponseDto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BoardService {

    // 게시글 작성
    BoardResponseDto createBoard(BoardRequestDto boardDto);

    // 게시글 수정
    BoardResponseDto updateBoard(Integer id, BoardRequestDto dto);

    // 게시글 삭제
    void deleteBoard(Integer id);

    // 게시글 상세 조회
    BoardResponseDto getBoard(Integer id);

    // 게시글 리스트 조회 (페이징)
    Page<BoardResponseDto> getBoardList(int page, int size);

    // 게시글 카테고리 검색
    List<BoardResponseDto> getBoardsByCategoryWithSorting(Category category);

    // 게시글 조회수 정렬
    List<BoardResponseDto> getBoardListByViews();

    // 게시글 검색
    List<BoardResponseDto> searchBoards(String keyword);
}
