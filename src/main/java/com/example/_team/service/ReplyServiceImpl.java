package com.example._team.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example._team.domain.Board;
import com.example._team.domain.Reply;
import com.example._team.domain.Users;
import com.example._team.dto.reply.ReplyRequestDto;
import com.example._team.dto.reply.ReplyResponseDto;
import com.example._team.exception.DataNotFoundException;
import com.example._team.repository.BoardRepository;
import com.example._team.repository.ReplyRepository;
import com.example._team.repository.UserRepository;

@Service
public class ReplyServiceImpl implements ReplyService{
	
	@Autowired
	private ReplyRepository replyRepository;
	
	@Autowired
	private BoardRepository boardRepository;
	
	@Autowired
	private UserRepository userRepository;

	
	// 댓글 작성
	@Transactional
    @Override
    public ReplyResponseDto createReply(ReplyRequestDto replyRequestDto, String email) {


        // 이메일로 사용자 정보 조회
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new DataNotFoundException("사용자를 찾을 수 없습니다."));

        // 게시글 조회
        Board board = boardRepository.findById(replyRequestDto.getBoardIdx())
                .orElseThrow(() -> new DataNotFoundException("유효하지 않은 게시글입니다."));

        // 댓글 엔티티 생성 및 저장
        Reply reply = replyRequestDto.toEntity(user, board);
        Reply saveReply = replyRepository.save(reply);
        
        return ReplyResponseDto.fromEntity(saveReply);
    }
	
	// 댓글 수정
	@Transactional
    @Override
    public ReplyResponseDto updateReply(Integer replyIdx, ReplyRequestDto replyRequestDto, String email) {
        // 댓글 조회
        Reply reply = replyRepository.findById(replyIdx)
                .orElseThrow(() -> new DataNotFoundException("Reply not found with ID: " + replyIdx));

        // 댓글 작성자 확인
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new DataNotFoundException("User not found with email: " + email));
        
        if (!reply.getUserIdx().equals(user)) {
            throw new IllegalStateException("User is not authorized to update this reply.");
        }

        // 댓글 수정
        reply.setContent(replyRequestDto.getContent());
        Reply updatedReply = replyRepository.save(reply);

        return ReplyResponseDto.fromEntity(updatedReply);
    }
	
	
	// 댓글 삭제
	@Transactional
	@Override
	public void deleteReply(Integer replyIdx) {
	    replyRepository.deleteById(replyIdx);
	}
	
	// 댓글 조회
	@Override
    public ReplyResponseDto getReplyById(Integer replyIdx) {
        // 댓글을 데이터베이스에서 조회
        Reply reply = replyRepository.findById(replyIdx)
            .orElseThrow(() -> new DataNotFoundException("댓글을 찾을 수 없습니다."));
        
        // Reply 엔티티를 ReplyResponseDto로 변환하여 반환
        return ReplyResponseDto.fromEntity(reply);
    }
	
	// 댓글 페이지
	@Override
	@Transactional(readOnly = true)
	public Page<ReplyResponseDto> getReplyList(Integer boardIdx, int page, int size) {
	    int startRow = page * size + 1;
	    int endRow = (page + 1) * size;
	    
	    // 댓글 페이징 처리
	    List<Reply> replies = replyRepository.findRepliesByBoardIdxWithPagination(boardIdx, startRow, endRow, Pageable.unpaged());

	    // 게시글에 대한 댓글 총 개수 조회
	    long total = replyRepository.countByBoardBoardIdx(boardIdx);

	    // Reply 엔티티를 DTO로 변환
	    List<ReplyResponseDto> replyDtos = replies.stream()
	            .map(ReplyResponseDto::fromEntity)
	            .collect(Collectors.toList());

	    return new PageImpl<>(replyDtos, Pageable.ofSize(size).withPage(page), total);
	}

}
