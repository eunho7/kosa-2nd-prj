package com.example._team.controller;

import com.example._team.domain.Board;
import com.example._team.domain.enums.Category;
import com.example._team.dto.board.BoardRequestDto;
import com.example._team.dto.board.BoardResponseDto;
import com.example._team.exception.DataNotFoundException;
import com.example._team.service.BoardService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/boards")
public class BoardApiController {

	@Autowired
	private final BoardService boardService;
	
	

	// 게시글 저장
//	@PostMapping
//	public ResponseEntity<String> savePost(@RequestBody BoardRequestDto boardRequestDto) {
//		try {
//			boardService.savePost(boardRequestDto);
//			return new ResponseEntity<>("Post saved successfully", HttpStatus.CREATED);
//		} catch (DataNotFoundException e) {
//			return new ResponseEntity<>("Failed to save post", HttpStatus.INTERNAL_SERVER_ERROR);
//		}
//	}
	
	// 게시글 저장
	@PostMapping
    public ResponseEntity<BoardResponseDto> savePost(@RequestBody BoardRequestDto boardDto) {
        BoardResponseDto responseDto = boardService.createBoard(boardDto);
        return ResponseEntity.ok(responseDto);
    }
	
	
	// 게시글 업데이트
//	@PutMapping("/{id}")
//	public ResponseEntity<String> updatePost(@PathVariable Integer id, @RequestBody BoardRequestDto boardRequestDto) {
//		try {
//			boardService.update(id, boardRequestDto);
//			return new ResponseEntity<>("Post updated successfully", HttpStatus.OK);
//		} catch (DataNotFoundException e) {
//			return new ResponseEntity<>("Failed to update post", HttpStatus.NOT_FOUND);
//		}
//	}
	
	// 게시글 업데이트
    @PutMapping("/{id}")
    public ResponseEntity<BoardResponseDto> updatePost(@PathVariable Integer id, @RequestBody BoardRequestDto boardDto) {
        BoardResponseDto responseDto = boardService.updateBoard(id, boardDto);
        return ResponseEntity.ok(responseDto);
    }

	// 게시글 삭제
	@DeleteMapping("/{id}")
	public ResponseEntity<String> deletePost(@PathVariable Integer id) {
		try {
			boardService.deleteBoard(id);
			return new ResponseEntity<>("Post deleted successfully", HttpStatus.NO_CONTENT);
		} catch (DataNotFoundException e) {
			return new ResponseEntity<>("Failed to delete post", HttpStatus.NOT_FOUND);
		}
	}

	// 게시글 상세 조회
	// 특정 게시글 조회
	@GetMapping("/{id}")
	public ResponseEntity<BoardResponseDto> getPost(@PathVariable Integer id) {
		try {
			BoardResponseDto board = boardService.getBoard(id);
			return new ResponseEntity<>(board, HttpStatus.OK);
		} catch (DataNotFoundException e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@GetMapping
    public ResponseEntity<Page<BoardResponseDto>> getBoards(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<BoardResponseDto> boardPage = boardService.getBoardList(page, size);
        return ResponseEntity.ok(boardPage);
    }
	
	
	// 게시글 카테고리별 검색
	@GetMapping("/category/{category}")
	public ResponseEntity<List<BoardResponseDto>> getPostsByCategory(@PathVariable Category category) {
		try {
			List<BoardResponseDto> posts = boardService.getBoardsByCategoryWithSorting(category);
			return new ResponseEntity<>(posts, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// 게시글 조회수 조회
	@GetMapping("/by-views")
    public ResponseEntity<List<BoardResponseDto>> getBoardListByViews() {
        List<BoardResponseDto> boardList = boardService.getBoardListByViews();
        return new ResponseEntity<>(boardList, HttpStatus.OK);
    }
	
	
	// 게시글 검색 키워드
	@GetMapping("/search")
	public ResponseEntity<List<BoardResponseDto>> searchPosts(@RequestParam("keyword") String keyword) {
		List<BoardResponseDto> boards = boardService.searchBoards(keyword);
		return new ResponseEntity<>(boards, HttpStatus.OK);
	}

}
