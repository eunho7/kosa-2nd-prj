//package com.example._team.controller;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.ModelAttribute;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//
//import com.example._team.domain.Users;
//import com.example._team.dto.reply.ReplyRequestDto;
//import com.example._team.dto.reply.ReplyResponseDto;
//import com.example._team.service.ReplyService;
//import com.example._team.service.UserService;
//
//import lombok.RequiredArgsConstructor;
//
//@Controller
//@RequiredArgsConstructor
//public class ReplyController {
//
//    private final ReplyService replyService;
//
//    
//    // 댓글 작성
//    @PostMapping("/replies/add")
//    public String createReply(@ModelAttribute ReplyRequestDto replyRequestDto) {
//
//    	System.out.println("BoardIdx: " + replyRequestDto.getBoardIdx());
//        // 로그인한 사용자 정보 가져오기
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        Users user = (Users) authentication.getPrincipal(); // 로그인한 사용자 정보 가져오기
//        
//        String email = user.getEmail(); // Users 객체에서 이메일 가져오기
//        
//        // 댓글 생성 서비스 호출 (이메일 전달)
//        replyService.createReply(replyRequestDto, email);
//        
//        // 댓글 작성 후 게시글 상세 페이지로 리디렉션 (오타 수정)
//        return "redirect:/boards/" + replyRequestDto.getBoardIdx(); 
//    }
//
//    
//    
//    // 댓글 리스트
//    @GetMapping("/boards/{id}")
//    public String getReplies(
//    			@PathVariable Integer boardIdx,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size,
//            Model model) {
//
//        // 로그인한 사용자 정보 가져오기
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        Users user = (Users) authentication.getPrincipal(); // 로그인한 사용자 정보 가져오기
//
//        // 사용자 닉네임을 모델에 추가
//        model.addAttribute("nickname", user.getNickname());
//
//        // 정렬 기준 (작성일자 기준 최신순)
//        Sort sortBy = Sort.by(Sort.Direction.DESC, "createdAt");
//
//        // Pageable 설정 (정렬 추가)
//        Pageable pageable = PageRequest.of(page, size, sortBy);
//
//        // 댓글 리스트 가져오기 (해당 게시글에 대한 댓글)
//        Page<ReplyResponseDto> replyPage = replyService.getReplyList(boardIdx, page, size);
//
//        // 모델에 데이터 추가
//        model.addAttribute("replyList", replyPage.getContent()); // 댓글 리스트
//        model.addAttribute("page", replyPage); // 페이지 정보
//        model.addAttribute("boardIdx", boardIdx); // 해당 게시글 ID
//
//        return "view/board/reply-list";  // 댓글 목록 페이지로 이동
//    }
//}
