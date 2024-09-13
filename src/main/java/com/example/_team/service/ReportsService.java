package com.example._team.service;

import com.example._team.domain.Reports;
import com.example._team.dto.report.ReportsResponseDto;
import com.example._team.repository.ReportsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ReportsService {
    private final ReportsRepository reportsRepository;

    public void saveReports(Reports reports) {
        reportsRepository.save(reports);
    }

    public List<Reports> findAll() {
        return reportsRepository.findAll();
    }

    public Page<ReportsResponseDto> paging(int page, int size, int statusValue) {
        int startRow = page * size + 1;
        int endRow = (page + 1) * size;

        List<Reports> reports = reportsRepository.findByStatusAndCreatedAt(startRow, endRow, statusValue, Pageable.unpaged());
        long total = reportsRepository.count();

        List<ReportsResponseDto> reportsDto = reports.stream()
                .map(ReportsResponseDto::fromEntity)
                .collect(Collectors.toList());
        return new PageImpl<>(reportsDto, Pageable.ofSize(size).withPage(page), total);
    }


//    public Page<ReportsResponseDto> getBoardList(int page, int size) {
//        int startRow = page * size + 1;
//        int endRow = (page + 1) * size;
//
//        List<Board> boards = reportsRepository.findByStatusAndCreatedAt(startRow, endRow, stautsValue, Pageable.unpaged());
//        long total = reportsRepository.count(); // 전체 게시글 수를 가져옵니다.
//
//        List<BoardResponseDto> reportsDtos = boards.stream()
//                .map(BoardResponseDto::fromEntity)
//                .collect(Collectors.toList());
//
//        return new PageImpl<>(boardDtos, Pageable.ofSize(size).withPage(page), total);
//    }
}
