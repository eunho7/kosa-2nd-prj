package com.example._team.controller;

import com.example._team.service.ReportsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.example._team.domain.Board;
import com.example._team.domain.Reports;
import com.example._team.domain.Users;
import com.example._team.dto.report.ReportsRequestDto;
import com.example._team.dto.report.ReportsResponseDto;
import com.example._team.repository.ReportsRepository;
import com.example._team.service.BoardAnswerService;
import com.example._team.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Controller
public class ReportsController {

    private final BoardAnswerService boardService;
    private final ReportsRepository reportsRepository;
    private final UserService userService;
    private final ReportsService reportsService;
    private final BoardAnswerService boardAnswerService;

    // 신고 입력 폼
    @GetMapping("/reports/{id}")
    public String reportForm(@PathVariable Integer id, HttpServletRequest request, Model model) {
        // 신고 게시물 URL 가져오기
        String url = request.getHeader("referer");

        // 신고 게시물 객체 생성
        Board board = boardService.findById(id);

        model.addAttribute("url", url);
        model.addAttribute("board", board);

        return "view/report/reports-form";
    }

    @PostMapping("/reports")
    public String saveReport(@ModelAttribute ReportsRequestDto requestDto) {
        System.out.println(requestDto.toString());
        Users user = userService.findByEmail("admin@gmail.com");

        Board board = boardService.findById(requestDto.getBoardIdx());
        Reports report = reportsRepository.save(ReportsRequestDto.toSaveEntity(requestDto, user, board));

        return "redirect:/board/list";
    }

    @GetMapping("/admin/reports/list")
    public String paging( @RequestParam(defaultValue = "0") int page,
                          @RequestParam(defaultValue = "10") int size, Model model) {

        // 게시글 목록 조회
        Page<ReportsResponseDto> reportsPage = reportsService.paging(page, size);
        model.addAttribute("reportsList", reportsPage.getContent());
        model.addAttribute("page", reportsPage);

        return "view/report/reports-list";
    }

    @PostMapping("/admin/reports/delete")
    public String deleteById(@RequestParam("deleteBoardIdx") List<Integer> boardIdx) {
        if (!boardIdx.isEmpty()) {
            for (int i = 0; i < boardIdx.size(); i++) {
//                Board board = boardService.findById(boardIdx.get(i));
                reportsService.deleteReports(boardIdx.get(i));
            }
        }
        return "redirect:/admin/reports/list";
    }

    @PostMapping("/admin/reports/inactive")
    public String inactive(@RequestParam("inactiveBoardIdx") List<Integer> boardIdx) {

        // 중복 제거
        List<Integer> list = boardIdx.stream().distinct().collect(Collectors.toList());

        if (!list.isEmpty()) {
            for (int i = 0; i < list.size(); i++) {
                Board board = boardService.findById(list.get(i));
                if (board.getStatus() == 1) {
                    board.setStatus(0);
                    boardAnswerService.save(board);
                } else if(board.getStatus() == 0) {
                    board.setStatus(1);
                    boardAnswerService.save(board);
                }
            }
        }
        return "redirect:/admin/reports/list";
    }
}
