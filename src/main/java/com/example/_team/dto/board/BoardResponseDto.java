package com.example._team.dto.board;

import java.time.LocalDateTime;

import com.example._team.domain.Board;
import com.example._team.domain.Reply;
import com.example._team.domain.enums.Category;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardResponseDto {

    private Integer boardIdx;
    private String title;
    private String content;
    private Category category;
    private Integer views;
    private Integer status;
    private long userIdx;
    private String nickname; // 작성자의 닉네임 필드 추가
    private LocalDateTime updatedAt;
    private LocalDateTime createdAt; // createdAt 필드 추가

    // Static method to convert from entity to DTO
    public static BoardResponseDto fromEntity(Board board) {
        return BoardResponseDto.builder()
                .boardIdx(board.getBoardIdx())
                .title(board.getTitle())
                .content(board.getContent())
                .category(board.getCategory())
                .views(board.getViews())
                .status(board.getStatus())
                .userIdx(board.getUserIdx() != null ? board.getUserIdx().getUserIdx() : null) // Corrected method to get ID
                .nickname(board.getUserIdx() != null ? board.getUserIdx().getNickname() : null) // 닉네임 가져오기
                .updatedAt(board.getUpdatedAt())
                .createdAt(board.getCreatedAt()) // createdAt 필드 설정
                .build();
    }
}
