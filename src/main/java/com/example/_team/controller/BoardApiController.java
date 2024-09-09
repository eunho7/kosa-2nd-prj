package com.example._team.controller;

import com.example._team.domain.Board;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

	// 게시글 작성 메소드
	@PostMapping("/create")
	public String createPost(@ModelAttribute BoardRequestDto boardRequestDto) {
		// title 값이 제대로 들어왔는지 로그 출력해 확인
		System.out.println("제목: " + boardRequestDto.getTitle());

		boardService.createBoard(boardRequestDto);  // 게시글 생성 서비스 호출
		return "redirect:/board/list";  // 다시 게시글 작성 페이지로 리디렉션
	}



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

	// 게시글 수정 메소드
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

	
	//게시글 삭제 메소드
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



	// 게시글 리스트 조회
	@GetMapping("/list")
    public String getBoards(
    			@RequestParam(value = "category", required = false) String categoryStr,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(value = "sort", defaultValue = "createdAt") String sort,
            Model model) {

		// 로그인한 사용자 정보 가져오기
				Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
				Users user = (Users) authentication.getPrincipal(); // 로그인한 사용자 정보

				// 사용자 닉네임을 모델에 추가
				model.addAttribute("nickname", user.getNickname());

				Category category = null;
			    try {
			        if (categoryStr != null && !categoryStr.isEmpty()) {
			            category = Category.valueOf(categoryStr); // 문자열을 Category enum으로 변환
			        }
			    } catch (IllegalArgumentException e) {
			        model.addAttribute("error", "잘못된 카테고리 값입니다.");
			        return "error/404"; // 잘못된 카테고리 값 처리
			    }
			    
			 // 정렬 기준 설정 (조회수 또는 생성일)
			    Sort sortBy = Sort.by(Sort.Direction.DESC, sort);

			    // Pageable 설정 (정렬 추가)
			    Pageable pageable = PageRequest.of(page, size, sortBy);
				

        Page<BoardResponseDto> boardPage = boardService.getBoardList(keyword, page, size, category, sort);
        model.addAttribute("boardList", boardPage);
        model.addAttribute("page", boardPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("category", category);
        model.addAttribute("sort", sort);  

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
