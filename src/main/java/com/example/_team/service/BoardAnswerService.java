package com.example._team.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example._team.domain.Board;
import com.example._team.domain.Users;
import com.example._team.dto.board.AnswerRequestDto;
import com.example._team.repository.BoardRepository;
import com.example._team.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class BoardAnswerService {
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;

    public void saveAnswer(AnswerRequestDto answerRequestDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        
        Users user = userRepository.findByEmail(email).get();
        Board answerBoard = boardRepository.findById(answerRequestDto.getAnswerBoardIdx()).get();

        Board board =  AnswerRequestDto.toSaveAnswerDto(user, answerRequestDto, answerBoard);
        
        boardRepository.save(board);
    }

    public Board findById(Integer id) {
        return boardRepository.findById(id).get();
    }

    public void save(Board board) {
        boardRepository.save(board);
    }

}
