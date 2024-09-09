package com.example._team.controller;

import com.example._team.domain.Users;
import com.example._team.domain.enums.Category;
import com.example._team.dto.board.BoardRequestDto;
import com.example._team.dto.board.BoardResponseDto;
import com.example._team.exception.DataNotFoundException;
import com.example._team.service.BoardService;
import com.example._team.service.UserService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardApiController {

	@Autowired
	private final BoardService boardService;

	@Autowired
	private final UserService userService;



	// 게시글 작성 폼 페이지
	@GetMapping("/create")
	public String createPostForm(Model model) {
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		Users user = userService.findByEmail(email);
		model.addAttribute("user", user);  // 사용자 정보를 모델에 추가
		return "view/board/board-create";  // 게시글 작성 페이지로 이동
	}

	@PostMapping("/create")
	public String createPost(@ModelAttribute BoardRequestDto boardRequestDto) {
		// title 값이 제대로 들어왔는지 로그 출력해 확인
		System.out.println("제목: " + boardRequestDto.getTitle());

		boardService.createBoard(boardRequestDto);  // 게시글 생성 서비스 호출
		return "redirect:/board/list";  // 다시 게시글 작성 페이지로 리디렉션
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
	
//	// 게시글 업데이트
//    @PutMapping("/{id}")
//    public ResponseEntity<BoardResponseDto> updatePost(@PathVariable Integer id, @RequestBody BoardRequestDto boardDto) {
//        BoardResponseDto responseDto = boardService.updateBoard(id, boardDto);
//        return ResponseEntity.ok(responseDto);
//    }

	// 게시글 수정 폼 페이지
	@GetMapping("/edit/{id}")
	public String editPost(@PathVariable Integer id, Model model, Principal principal) {
		BoardResponseDto board = boardService.getBoard(id);

		// 현재 로그인한 사용자의 userIdx와 게시글 작성자의 userIdx를 비교
		if (board.getUserIdx() != getLoggedInUserId(principal)) {
			return "error/403"; // 권한 없음 페이지로 이동
		}

		model.addAttribute("board", board);
		return "view/board/board-edit";
	}

	@PostMapping("/edit/{id}")
	public String updatePost(@PathVariable Integer id, @ModelAttribute BoardRequestDto boardDto, Principal principal) {
		BoardResponseDto board = boardService.getBoard(id);

		// 현재 로그인한 사용자의 userIdx와 게시글 작성자의 userIdx를 비교
		if (board.getUserIdx() != getLoggedInUserId(principal)) {
			return "error/403"; // 권한 없음 페이지로 이동
		}

		boardService.updateBoard(id, boardDto);
		return "redirect:/board/" + id; // 수정 후 상세 페이지로 이동
	}


//	// 게시글 삭제
//	@DeleteMapping("/{id}")
//	public ResponseEntity<String> deletePost(@PathVariable Integer id) {
//		try {
//			boardService.deleteBoard(id);
//			return new ResponseEntity<>("Post deleted successfully", HttpStatus.NO_CONTENT);
//		} catch (DataNotFoundException e) {
//			return new ResponseEntity<>("Failed to delete post", HttpStatus.NOT_FOUND);
//		}
//	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<Void> deletePost(@PathVariable Integer id, Principal principal) {
		BoardResponseDto board = boardService.getBoard(id);

		// 현재 로그인한 사용자의 userIdx와 게시글 작성자의 userIdx를 비교
		if (board.getUserIdx() != (getLoggedInUserId(principal))) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();  // 권한 없음 상태 반환
		}

		boardService.deleteBoard(id);  // 게시글 삭제 서비스 호출

		return ResponseEntity.noContent().build();  // 204 No Content 상태 반환
	}





	// 게시글 상세 조회
	// 특정 게시글 조회
//	@GetMapping("/{id}")
//	public ResponseEntity<BoardResponseDto> getPost(@PathVariable Integer id) {
//		try {
//			BoardResponseDto board = boardService.getBoard(id);
//			return new ResponseEntity<>(board, HttpStatus.OK);
//		} catch (DataNotFoundException e) {
//			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//		}
//	}
//
//	@GetMapping
//    public ResponseEntity<Page<BoardResponseDto>> getBoards(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size) {
//        Page<BoardResponseDto> boardPage = boardService.getBoardList(page, size);
//        return ResponseEntity.ok(boardPage);
//    }

	// 게시글 리스트 조회
	@GetMapping("/list")
	public String getBoards(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			Model model) {

		// 로그인한 사용자 정보 가져오기
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Users user = (Users) authentication.getPrincipal(); // 로그인한 사용자 정보

		// 사용자 닉네임을 모델에 추가
		model.addAttribute("nickname", user.getNickname());

		// 게시글 목록 조회
		Page<BoardResponseDto> boardPage = boardService.getBoardList(page, size);
		model.addAttribute("boardList", boardPage.getContent());
		model.addAttribute("page", boardPage);

		return "view/board/board-list";  // 게시글 목록 페이지로 이동
	}

	// 게시글 상세 조회
	@GetMapping("/{id}")
	public String getPost(@PathVariable Integer id, Model model) {
		try {
			BoardResponseDto board = boardService.getBoard(id);
			model.addAttribute("board", board);
			return "view/board/board-detail";
		} catch (DataNotFoundException e) {
			return "error/404";
		}
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

	// 로그인한 사용자의 userIdx 가져오기
	private Long getLoggedInUserId(Principal principal) {
		// Principal 객체에서 현재 사용자의 이메일을 추출
		String email = principal.getName();

		// UserService를 통해 이메일로 사용자 정보 가져오기
		Users user = userService.findByEmail(email);

		// 사용자 ID 반환
		return user.getUserIdx();
	}

}
