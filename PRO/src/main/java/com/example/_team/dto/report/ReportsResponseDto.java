package com.example._team.dto.report;

import com.example._team.domain.Board;
import com.example._team.domain.Reports;
import com.example._team.domain.Users;
import com.example._team.dto.board.BoardResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportsResponseDto {
    private Integer reportsIdx;
    private String content;
    private Users userIdx;
    private Board boardIdx;
    private LocalDateTime createdAt;

    // Static method to convert from entity to DTO
    public static ReportsResponseDto fromEntity(Reports reports) {
        return ReportsResponseDto.builder()
                .reportsIdx(reports.getReportsIdx())
                .content(reports.getContent())
                .userIdx(reports.getUserIdx())
                .boardIdx(reports.getBoardIdx())
                .createdAt(reports.getCreatedAt())
                .build();
    }
}
