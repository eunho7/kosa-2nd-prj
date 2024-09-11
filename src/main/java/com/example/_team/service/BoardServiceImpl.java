package com.example._team.service;

import com.example._team.domain.Board;
import com.example._team.domain.Users;
import com.example._team.domain.enums.Category;
import com.example._team.dto.board.BoardRequestDto;
import com.example._team.dto.board.BoardResponseDto;
import com.example._team.exception.DataNotFoundException;
import com.example._team.repository.BoardRepository;
import com.example._team.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BoardServiceImpl implements BoardService {

	@Autowired
	private BoardRepository boardRepository;

	@Autowired
	private UserRepository userRepository;

	// 게시글 작성
	@Transactional
	@Override
	public BoardResponseDto createBoard(BoardRequestDto boardDto) {
		// 현재 로그인한 사용자의 정보를 가져옴
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String email = authentication.getName();

		// 이메일을 사용해서 해당 사용자의 정보를 조회
		Users user = userRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

		// 게시글 생성 시 userIdx를 직접 넣는 대신, 로그인한 사용자의 정보를 사용
		Board board = boardDto.toEntity(user, null);
		Board savedBoard = boardRepository.save(board);

		return BoardResponseDto.fromEntity(savedBoard);
	}

	// 게시글 수정
	@Transactional
	@Override
	public BoardResponseDto updateBoard(Integer id, BoardRequestDto dto) {
		Board board = boardRepository.findById(id)
				.orElseThrow(() -> new DataNotFoundException("Board not found with ID: " + id));
		board.setTitle(dto.getTitle());
		board.setContent(dto.getContent());
		board.setCategory(dto.getCategory());
		Board updatedBoard = boardRepository.save(board);
		return BoardResponseDto.fromEntity(updatedBoard);
	}

	// 게시글 삭제
	@Transactional
	@Override
	public void deleteBoard(Integer id) {
		Board board = boardRepository.findById(id)
				.orElseThrow(() -> new DataNotFoundException("Board not found with ID: " + id));
		boardRepository.delete(board);
	}

	// 게시글 상세보기
	@Override
	public BoardResponseDto getBoard(Integer id) {
		Board board = boardRepository.findById(id)
				.orElseThrow(() -> new DataNotFoundException("Board not found with ID: " + id));
		board.setViews(board.getViews() + 1);
		Board updatedBoard = boardRepository.save(board);
		return BoardResponseDto.fromEntity(updatedBoard);
	}

	@Override
	public Page<BoardResponseDto> getBoardList(String keyword, int page, int size, Category category, String sort) {
	    Set<BoardResponseDto> boardDtos = new LinkedHashSet<>(); // Set으로 중복 제거
	    int startRow = page * size + 1;
	    int endRow = (page + 1) * size;
	    long total;

	    // 키워드 검색이 있는 경우
	    if (keyword != null && !keyword.trim().isEmpty()) {
	        List<Board> baseBoards = boardRepository.findByKeywordOrderedByBoardIdx(keyword, startRow, endRow, Pageable.unpaged());
	        total = boardRepository.countByKeyword(keyword);

	        // 기본 게시글 및 답변 게시글 추가
	        for (Board baseBoard : baseBoards) {
	            // 기본 게시글 추가
	            boardDtos.add(BoardResponseDto.fromEntity(baseBoard));
	            
	            // 답변 게시글 추가
	            List<Board> answers = boardRepository.findAnswersByBoard(baseBoard.getBoardIdx());
	            answers.forEach(answer -> boardDtos.add(BoardResponseDto.fromEntity(answer)));
	        }
	    }
	    // 카테고리가 있는 경우
	    else if (category != null) {
	        List<Board> baseBoards;
	        if ("views".equals(sort)) {
	            baseBoards = boardRepository.findByCategoryOrderedByViews(category.name(), startRow, endRow, Pageable.unpaged());
	        } else {
	            baseBoards = boardRepository.findByCategoryOrderedByBoardIdx(category.name(), startRow, endRow, Pageable.unpaged());
	        }
	        total = boardRepository.countByCategory(category);

	        // 기본 게시글 및 답변 게시글 추가
	        for (Board baseBoard : baseBoards) {
	            // 기본 게시글 추가
	            boardDtos.add(BoardResponseDto.fromEntity(baseBoard));
	            
	            // 답변 게시글 추가
	            List<Board> answers = boardRepository.findAnswersByBoard(baseBoard.getBoardIdx());
	            answers.forEach(answer -> boardDtos.add(BoardResponseDto.fromEntity(answer)));
	        }
	    }
	    // 카테고리가 없는 경우
	    else {
	        List<Board> baseBoards;
	        if ("views".equals(sort)) {
	            baseBoards = boardRepository.findAllOrderedByViews(startRow, endRow, Pageable.unpaged());
	        } else {
	            baseBoards = boardRepository.findAllOrderedByBoardIdx(startRow, endRow, Pageable.unpaged());
	        }
	        total = boardRepository.count();

	        // 기본 게시글 및 답변 게시글 추가
	        for (Board baseBoard : baseBoards) {
	            // 기본 게시글 추가
	            boardDtos.add(BoardResponseDto.fromEntity(baseBoard));

	            // 답변 게시글 추가
	            List<Board> answers = boardRepository.findAnswersByBoard(baseBoard.getBoardIdx());
	            answers.forEach(answer -> boardDtos.add(BoardResponseDto.fromEntity(answer)));
	        }
	    }

	    // 게시글 수가 size를 넘지 않도록 제한하여 반환
	    List<BoardResponseDto> paginatedList = boardDtos.stream().limit(size).collect(Collectors.toList());

	    // 페이징된 게시글과 총 게시글 수를 기반으로 Page 객체 반환
	    return new PageImpl<>(paginatedList, Pageable.ofSize(size).withPage(page), total);
	}







}
