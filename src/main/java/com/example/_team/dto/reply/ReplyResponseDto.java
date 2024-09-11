package com.example._team.dto.reply;

import java.time.LocalDateTime;

import com.example._team.domain.Reply;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReplyResponseDto {

	private Integer replyIdx;
    private String content;
    private String nickname; // 작성자의 닉네임
    private Integer boardIdx;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long userIdx;
    
    public static ReplyResponseDto fromEntity(Reply reply) {
    	
    		return ReplyResponseDto.builder()
    				.replyIdx(reply.getReplyIdx())
    				.content(reply.getContent())
    				.nickname(reply.getUserIdx() != null ? reply.getUserIdx().getNickname() : null)
    				.boardIdx(reply.getBoard() != null ? reply.getBoard().getBoardIdx() : null)
    				.userIdx(reply.getUserIdx() != null ? reply.getUserIdx().getUserIdx() : null)
    				.createdAt(reply.getCreatedAt())
    				.updatedAt(reply.getUpdatedAt())
    				.build();
    				
    }
}
