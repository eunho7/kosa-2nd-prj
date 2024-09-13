package com.example._team.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example._team.domain.Users;
import com.example._team.domain.enums.Category;
import com.example._team.dto.board.BoardRequestDto;
import com.example._team.dto.board.BoardResponseDto;
import com.example._team.dto.reply.ReplyRequestDto;
import com.example._team.dto.reply.ReplyResponseDto;
import com.example._team.service.BoardService;
import com.example._team.service.ReplyService;
import com.example._team.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardApiController {

	@Autowired
	private final BoardService boardService;

	@Autowired
	private final UserService userService;

	@Autowired
	private final ReplyService replyService;

	// 게시글 작성 폼 페이지
	@GetMapping("/create")
	public String createPostForm(Model model) {
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		Users user = userService.findByEmail(email);
		model.addAttribute("user", user); // 사용자 정보를 모델에 추가
		return "view/board/board-create"; // 게시글 작성 페이지로 이동
	}

//	// 게시글 작성 메소드
//	@PostMapping("/create")
//	public String createPost(@ModelAttribute BoardRequestDto boardRequestDto) {
//		// title 값이 제대로 들어왔는지 로그 출력해 확인
//		System.out.println("제목: " + boardRequestDto.getTitle());
//
//		boardService.createBoard(boardRequestDto); // 게시글 생성 서비스 호출
//		return "redirect:/board/list"; // 다시 게시글 작성 페이지로 리디렉션
//	}
	
	// 게시글 작성 메소드
	@PostMapping("/create")
	public String createPost(@ModelAttribute BoardRequestDto boardRequestDto, @RequestParam("imgFile") List<MultipartFile> files) {
	    // title 값이 제대로 들어왔는지 로그 출력해 확인
	    System.out.println("제목: " + boardRequestDto.getTitle());

	    // 업로드된 파일들을 DTO에 설정
	    boardRequestDto.setImgFile(files);

	    // 게시글 생성 서비스 호출
	    boardService.createBoard(boardRequestDto);

	    // 게시글 목록 페이지로 리디렉션
	    return "redirect:/board/list";
	}


	// 게시글 수정 폼 페이지
	@GetMapping("/edit/{id}")
	public String editPost(@PathVariable Integer id, Model model, Principal principal,
			RedirectAttributes redirectAttributes) {
		BoardResponseDto board = boardService.getBoard(id);

		// 현재 로그인한 사용자의 userIdx와 게시글 작성자의 userIdx를 비교
		if (board.getUserIdx() != getLoggedInUserId(principal)) {
			// 권한이 없을 경우 메시지를 모델에 추가하고 상세 페이지로 리디렉션
			redirectAttributes.addFlashAttribute("errorMessage", "게시글을 수정할 권한이 없습니다.");
			model.addAttribute("board", board); // 게시글 정보를 다시 전달
			return "redirect:/board/" + id; // 상세 페이지로 이동
		}

		model.addAttribute("board", board);
		return "view/board/board-edit"; // 권한이 있으면 수정 페이지로 이동
	}

//	// 게시글 수정 메소드
//	@PostMapping("/edit/{id}")
//	public String updatePost(@PathVariable Integer id, @ModelAttribute BoardRequestDto boardDto, Principal principal) {
//
//		boardService.updateBoard(id, boardDto);
//		return "redirect:/board/" + id; // 수정 후 상세 페이지로 이동
//	}
	
	// 게시글 수정 메소드
	@PostMapping("/edit/{id}")
	public String updatePost(@PathVariable Integer id, @ModelAttribute BoardRequestDto boardDto,
	                         @RequestParam(value = "deleteImgs", required = false) List<String> deleteImgs,
	                         @RequestParam(value = "imgFile", required = false) List<MultipartFile> imgFiles,
	                         Principal principal) {

	    // 이미지를 포함한 게시글 업데이트 처리
	    boardDto.setDeleteImgs(deleteImgs); // 삭제할 이미지 경로 리스트를 DTO에 추가
	    boardDto.setImgFile(imgFiles); // 업로드할 이미지 리스트를 DTO에 추가

	    boardService.updateBoard(id, boardDto);
	    
	    return "redirect:/board/" + id; // 수정 후 상세 페이지로 이동
	}

	// 게시글 삭제 메소드
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<Void> deletePost(@PathVariable Integer id, Principal principal) {
		BoardResponseDto board = boardService.getBoard(id);

		// 현재 로그인한 사용자의 userIdx와 게시글 작성자의 userIdx를 비교
		if (board.getUserIdx() != (getLoggedInUserId(principal))) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 권한 없음 상태 반환
		}

		boardService.deleteBoard(id); // 게시글 삭제 서비스 호출

		return ResponseEntity.noContent().build(); // 204 No Content 상태 반환
	}

	// 게시글 리스트 조회
	@GetMapping("/list")
	public String getBoards(@RequestParam(value = "category", required = false) String categoryStr,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
			@RequestParam(required = false) String keyword,
			@RequestParam(value = "sort", defaultValue = "createdAt") String sort, Model model) {

		// 로그인한 사용자 정보 가져오기
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Users user = (Users) authentication.getPrincipal(); // 로그인한 사용자 정보

		// 사용자 닉네임을 모델에 추가
		model.addAttribute("nickname", user.getNickname());

		Category category = null;
		if (categoryStr != null && !categoryStr.isEmpty()) {
			try {
				category = Category.valueOf(categoryStr); // 문자열을 Category enum으로 변환
			} catch (IllegalArgumentException e) {
				// 잘못된 카테고리 값 입력 시 에러 메시지 표시
				model.addAttribute("errorMessage", "잘못된 카테고리 값입니다.");
			}
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

		return "view/board/board-list"; // 게시글 목록 페이지로 이동
	}

	// 댓글 작성
	@PostMapping("/replies/add")
	public String createReply(@ModelAttribute ReplyRequestDto replyRequestDto) {
		// 로그인한 사용자 정보 가져오기
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Users user = (Users) authentication.getPrincipal(); // 로그인한 사용자 정보 가져오기

		String email = user.getEmail(); // Users 객체에서 이메일 가져오기

		// 댓글 생성 서비스 호출 (이메일 전달)
		replyService.createReply(replyRequestDto, email);

		// 댓글 작성 후 게시글 상세 페이지로 리다이렉션
		return "redirect:/board/" + replyRequestDto.getBoardIdx(); // 리다이렉션 경로를 게시글 상세 조회로
	}

	// 댓글 수정
	@PostMapping("/replies/update/{replyIdx}")
	public String updateReply(@PathVariable Integer replyIdx, @ModelAttribute ReplyRequestDto replyRequestDto,
			RedirectAttributes redirectAttributes) {
		// 로그인한 사용자 정보 가져오기
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Users user = (Users) authentication.getPrincipal(); // 로그인한 사용자 정보 가져오기

		String email = user.getEmail(); // Users 객체에서 이메일 가져오기

		try {
			// 댓글 수정 서비스 호출 (이메일 전달)
			replyService.updateReply(replyIdx, replyRequestDto, email);
		} catch (IllegalStateException e) {
			// 권한이 없을 경우 예외 메시지 설정
			redirectAttributes.addFlashAttribute("errorMessage", "댓글 수정 권한이 없습니다.");
			return "redirect:/board/" + replyRequestDto.getBoardIdx(); // 다시 게시글 상세 페이지로 리다이렉션
		}

		// 댓글 수정 후 게시글 상세 페이지로 리다이렉션
		return "redirect:/board/" + replyRequestDto.getBoardIdx();
	}

	// 댓글 삭제 메소드
	@DeleteMapping("/replies/delete/{replyIdx}")
	public ResponseEntity<String> deleteReply(@PathVariable Integer replyIdx, Principal principal) {
		ReplyResponseDto reply = replyService.getReplyById(replyIdx);

		// 현재 로그인한 사용자의 userIdx와 댓글 작성자의 userIdx를 비교
		if (reply.getUserIdx() != (getLoggedInUserId(principal))) {
			// 권한이 없을 경우 403 상태 반환
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 권한 없음 상태 반환
		}

		replyService.deleteReply(replyIdx); // 댓글 삭제 서비스 호출
		return ResponseEntity.noContent().build(); // 204 No Content 상태 반환
	}

	// 댓글 리스트 페이지
	@GetMapping("/{id}")
	public String getPost(@PathVariable Integer id, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size, Model model) {

		// 게시글 정보 가져오기
		BoardResponseDto board = boardService.getBoard(id);

		// 댓글 페이징 처리 (page, size)
		Page<ReplyResponseDto> replyPage = replyService.getReplyList(id, page, size);

		// 모델에 데이터 추가
		model.addAttribute("board", board); // 게시글 정보
		model.addAttribute("replyList", replyPage.getContent()); // 댓글 리스트
		model.addAttribute("page", replyPage); // 페이징 정보 추가

		return "view/board/board-detail";

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
