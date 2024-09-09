package com.example._team.dto.reply;

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
    private String createdAt;
}
