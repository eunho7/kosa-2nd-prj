package com.example._team.dto.board;

import com.example._team.domain.Board;
import com.example._team.domain.Users;
import com.example._team.domain.enums.Category;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AnswerRequestDto {
    private String title;
    private String content;
    private String category;
    private Integer views;
    private Integer status;
    private Integer userIdx;
    private Integer answerBoardIdx;

    public static Board toSaveAnswerDto(Users user, AnswerRequestDto requestDto, Board answerBoard) {
        Board board = new Board();

        String title = " ↳RE : " + requestDto.getTitle();

        board.setTitle(title);
        board.setContent(requestDto.getContent());
        board.setCategory(Category.valueOf(requestDto.getCategory()));
        board.setViews(0);
        board.setStatus(1);
        board.setUserIdx(user);
        board.setAnswerBoardIdx(answerBoard);

        return board;
    }
}
