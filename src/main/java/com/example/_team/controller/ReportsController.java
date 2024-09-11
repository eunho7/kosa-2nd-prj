package com.example._team.controller;

import com.example._team.domain.Board;
import com.example._team.domain.Reports;
import com.example._team.domain.Users;
import com.example._team.dto.board.BoardResponseDto;
import com.example._team.dto.report.ReportsRequestDto;
import com.example._team.dto.report.ReportsResponseDto;
import com.example._team.repository.ReportsRepository;
import com.example._team.service.BoardAnswerService;
import com.example._team.service.BoardService;
import com.example._team.service.ReportsService;
import com.example._team.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class ReportsController {

    private final BoardAnswerService boardService;
    private final ReportsRepository reportsRepository;
    private final UserService userService;
    private final ReportsService reportsService;

    // 신고 입력 폼
    @GetMapping("/reports/{id}")
    public String reportForm(@PathVariable Integer id, HttpServletRequest request, Model model) {
        // 신고 게시물 URL 가져오기
        String url = request.getHeader("referer");

        // 신고 게시물 객체 생성
        Board board = boardService.findById(id);

        model.addAttribute("url", url);
        model.addAttribute("board", board);

        return "view/report/reportsForm";
    }

    @PostMapping("/reports")
    public String saveReport(@ModelAttribute ReportsRequestDto requestDto) {
        System.out.println(requestDto.toString());
        Users user = userService.findByEmail("admin@gmail.com");

        Board board = boardService.findById(requestDto.getBoardIdx());
        Reports report = reportsRepository.save(ReportsRequestDto.toSaveEntity(requestDto, user, board));

        return "redirect:/board/list";
    }

//    @GetMapping("/reports/list")
//    public String reportsList(Model model){
//        List<Reports> list = reportsService.findAll();
//
//        model.addAttribute("reportsList",list);
//
//        return "view/report/reports-list";
//
//    }

    @GetMapping("/reports/list")
    public String paging( @RequestParam(defaultValue = "0") int page,
                          @RequestParam(defaultValue = "10") int size, Model model) {

        // 게시글 목록 조회
        Page<ReportsResponseDto> reportsPage = reportsService.paging(page, size, 1);
        model.addAttribute("reportsList", reportsPage.getContent());
        model.addAttribute("page", reportsPage);

        return "view/report/reports-list";
    }
}
