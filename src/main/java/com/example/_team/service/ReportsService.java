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

    // 신고 게시글 저장
    public void saveReports(Reports reports) {
        reportsRepository.save(reports);
    }

    // 신고 게시글 삭제
    public void deleteReports(Integer reportsIdx) { reportsRepository.deleteById(reportsIdx);}

    // 신고 리스트업
    public List<Reports> findAll() {
        return reportsRepository.findAll();
    }

    // 신고글 찾기
    public Reports findById(Integer reportsIdx) { return reportsRepository.findById(reportsIdx).orElse(null);}

    // 페이징
    public Page<ReportsResponseDto> paging(int page, int size) {
        int startRow = page * size + 1;
        int endRow = (page + 1) * size;

        List<Reports> reports = reportsRepository.findByStatusAndCreatedAt(startRow, endRow, Pageable.unpaged());
        long total = reportsRepository.count();

        List<ReportsResponseDto> reportsDto = reports.stream()
                .map(ReportsResponseDto::fromEntity)
                .collect(Collectors.toList());
        return new PageImpl<>(reportsDto, Pageable.ofSize(size).withPage(page), total);
    }
}