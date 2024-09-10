package com.example._team.service;

import com.example._team.domain.Reports;
import com.example._team.repository.ReportsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ReportsService {
    private final ReportsRepository reportsRepository;

    public void saveReports(Reports reports) {
        reportsRepository.save(reports);
    }
}
