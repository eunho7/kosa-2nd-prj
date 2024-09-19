package com.example._team.service;

import org.springframework.data.domain.Page;

import com.example._team.dto.reply.ReplyRequestDto;
import com.example._team.dto.reply.ReplyResponseDto;

public interface ReplyService {

	// 댓글 작성
	ReplyResponseDto createReply(ReplyRequestDto replyRequestDto, String email);

	// 댓글 삭제
	void deleteReply(Integer replyIdx);

	// 댓글 수정
	ReplyResponseDto updateReply(Integer replyIdx, ReplyRequestDto replyRequestDto, String email);

	// 댓글 조회
	ReplyResponseDto getReplyById(Integer replyIdx);

	// 댓글 리스트
	Page<ReplyResponseDto> getReplyList(Integer boardIdx, int page, int size);

}
