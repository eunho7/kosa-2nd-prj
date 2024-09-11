package com.example._team.dto.reply;

import com.example._team.domain.Board;
import com.example._team.domain.Reply;
import com.example._team.domain.Users;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReplyRequestDto {
	private String content;
    private Integer userIdx;  // 작성자 ID
    private Integer boardIdx; // 게시글 ID
    
    @Builder
    public ReplyRequestDto(String content, Integer userIdx, Integer boardIdx) {
    		this.content = content;
    		this.userIdx = userIdx;
    		this.boardIdx = boardIdx;
    }
    
    public Reply toEntity(Users user, Board board) {
    		return Reply.builder()
    				.content(this.content)
    				.userIdx(user)
    				.board(board)
    				.build();
    }
}
