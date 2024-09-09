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

import java.util.List;
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

	// 게시글 리스트
	@Override
    public Page<BoardResponseDto> getBoardList(String keyword, int page, int size, Category category, String sort) {
        List<Board> boards;
        int startRow = page * size + 1;
		int endRow = (page + 1) * size;
		long total;

		if (keyword != null && !keyword.trim().isEmpty()) {
            // 키워드 검색
            boards = boardRepository.findByKeywordOrderedByBoardIdx(keyword, startRow, endRow, Pageable.unpaged());
            total = boardRepository.countByKeyword(keyword);
        } else if (category != null) {
            // 카테고리 필터
            boards = boardRepository.findByCategoryOrderedByBoardIdx(category.name(), startRow, endRow, Pageable.unpaged());
            total = boardRepository.countByCategory(category);
        } else {
            // 정렬 방식에 따라 처리
            if ("views".equals(sort)) {
                // 조회수 기준 정렬
                boards = boardRepository.findAllOrderedByViews(startRow, endRow, Pageable.unpaged());
            } else {
                // 기본 정렬 (게시글 번호 기준)
                boards = boardRepository.findAllOrderedByBoardIdx(startRow, endRow, Pageable.unpaged());
            }
            total = boardRepository.count();
        }
        	
        List<BoardResponseDto> boardDtos = boards.stream()
				.map(BoardResponseDto::fromEntity)
				.collect(Collectors.toList());
        
        return new PageImpl<>(boardDtos, Pageable.ofSize(size).withPage(page), total);
    }

}
