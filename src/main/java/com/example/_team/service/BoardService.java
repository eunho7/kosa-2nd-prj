package com.example._team.service;

import com.example._team.domain.Board;
import com.example._team.domain.enums.Category;
import com.example._team.dto.board.BoardRequestDto;
import com.example._team.dto.board.BoardResponseDto;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BoardService {

	// 게시글 작성
	void savePost(BoardRequestDto boardDto);

	// 게시글 수정
	void update(Integer id, BoardRequestDto dto);

	// 게시글 삭제 boardidx
	void deletePost(Integer id);

	// 게시글 상세 조회 ( 클릭시 상세조회 페이지 이동 + 조회수 증가 )
	// 게시글 검색 boardidx ( 게시글 번호로 검색 )
	BoardResponseDto getPost(Integer id);

	// 게시글 리스트 페이지 ( 기본 )
	List<BoardResponseDto> getBoardList(Integer pageNum);
	//페이징 테스트 코드
//	Page<BoardResponseDto> getBoardList(Pageable pageable);
	
	// 게시글 검색 카테고리 리스트 
	List<BoardResponseDto> getPostsByCategoryWithSorting(Category category);
	
	// 게시글 검색 조회수 리스트 
	List<BoardResponseDto> getBoardListByViews();
	
	// 게시글 검색 키워드 리스트 
	List<BoardResponseDto> searchPosts(String keyword);


}
