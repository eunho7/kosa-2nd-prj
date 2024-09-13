package com.example._team.dto.board;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.example._team.domain.Board;
import com.example._team.domain.Users;
import com.example._team.domain.enums.Category;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardRequestDto {

    private String title;
    private String content;
    private Category category;
    private Integer views;
    private Integer status;
    private Integer userIdx; 
    private Integer answerBoardIdx;
    private List<MultipartFile> imgFile;
    private List<String> deleteImgFilePaths;
    private List<String> deleteImgs;

    // Optional: Convert DTO to Entity
    public Board toEntity(Users user, Board answerBoard) {
        return Board.builder()
                .title(this.title)
                .content(this.content)
                .category(this.category)
                .views(0)
                .status(1)
                .userIdx(user)
                .answerBoardIdx(answerBoard)
                .build();
    }
}
