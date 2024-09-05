package com.example._team.dto.board;

import com.example._team.domain.Board;
import com.example._team.domain.Users;
import com.example._team.domain.enums.Category;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BoardRequestDto {

    private String title;
    private String content;
    private Category category;
    private Integer views;
    private Integer status;
    private Integer userIdx; // Use Integer instead of Long
    private Integer answerBoardIdx; // Use Integer instead of Long

    @Builder
    public BoardRequestDto(String title, String content, Category category, Integer views, Integer status, Integer userIdx, Integer answerBoardIdx) {
        this.title = title;
        this.content = content;
        this.category = category;
        this.views = views;
        this.status = status;
        this.userIdx = userIdx;
        this.answerBoardIdx = answerBoardIdx;
    }
    
    // Optional: Convert DTO to Entity
    public Board toEntity(Users user, Board answerBoard) {
        return Board.builder()
                .title(this.title)
                .content(this.content)
                .category(this.category)
                .views(this.views)
                .status(this.status)
                .userIdx(user)
                .answerBoardIdx(answerBoard)
                .build();
    }
}
