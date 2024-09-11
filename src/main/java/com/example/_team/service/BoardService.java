package com.example._team.service;

import org.springframework.data.domain.Page;

import com.example._team.domain.enums.Category;
import com.example._team.dto.board.BoardRequestDto;
import com.example._team.dto.board.BoardResponseDto;

public interface BoardService {

    // 게시글 작성
    BoardResponseDto createBoard(BoardRequestDto boardDto);

    // 게시글 수정
    BoardResponseDto updateBoard(Integer id, BoardRequestDto dto);

    // 게시글 삭제
    void deleteBoard(Integer id);

    // 게시글 상세 조회
    BoardResponseDto getBoard(Integer id);

    // 게시글 리스트 조회 (페이징, 키워드, 카테고리, 조회수)
    Page<BoardResponseDto> getBoardList(String keyword, int page, int size, Category category, String sort);


}
