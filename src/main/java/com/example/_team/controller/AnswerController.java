package com.example._team.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example._team.domain.Users;
import com.example._team.dto.board.AnswerRequestDto;
import com.example._team.dto.board.BoardResponseDto;
import com.example._team.service.BoardAnswerService;
import com.example._team.service.BoardService;
import com.example._team.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
public class AnswerController {

    private final BoardService boardService;
    private final BoardAnswerService boardAnswerService;
    private final UserService userService;

    // 답변 Form
    @GetMapping("/answer/{id}")
    public String createAnswerForm(@PathVariable Integer id, Model model) {
        BoardResponseDto board = boardService.getBoard(id);

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Users user = userService.findByEmail(email);
        model.addAttribute("user", user);  // 사용자 정보를 모델에 추가

        model.addAttribute("board", board); // 답변할 게시글 추가

        return "view/board/board-answer-create";
    }

    // 답변 등록
    @PostMapping("/answer")
    public String createAnswer(@ModelAttribute AnswerRequestDto answerRequestDto) {

        boardAnswerService.saveAnswer(answerRequestDto);

        return "redirect:/board/list";  // 다시 게시글 작성 페이지로 리디렉션
    }

}
