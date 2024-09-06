package com.example._team.service;

import com.example._team.domain.Board;
import com.example._team.domain.Users;
import com.example._team.domain.enums.Category;
import com.example._team.dto.board.BoardRequestDto;
import com.example._team.dto.board.BoardResponseDto;
import com.example._team.exception.DataNotFoundException;
import com.example._team.repository.BoardRepository;
import com.example._team.repository.UsersRepository;
import com.example._team.repository.UsersRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BoardServiceImpl implements BoardService {

	@Autowired
	private BoardRepository boardRepository;

	@Autowired
	private UsersRepository usersRepository;

	@Transactional
	@Override
	public BoardResponseDto createBoard(BoardRequestDto boardDto) {
		Integer userId = boardDto.getUserIdx();
		Users user = usersRepository.findById(userId)
				.orElseThrow(() -> new DataNotFoundException("User not found with ID: " + userId));
		Board board = boardDto.toEntity(user, null);
		Board savedBoard = boardRepository.save(board);
		return BoardResponseDto.fromEntity(savedBoard);
	}

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

	@Transactional
	@Override
	public void deleteBoard(Integer id) {
		Board board = boardRepository.findById(id)
				.orElseThrow(() -> new DataNotFoundException("Board not found with ID: " + id));
		boardRepository.delete(board);
	}

	@Override
	public BoardResponseDto getBoard(Integer id) {
		Board board = boardRepository.findById(id)
				.orElseThrow(() -> new DataNotFoundException("Board not found with ID: " + id));
		// 조회수를 증가시킵니다.
		board.setViews(board.getViews() + 1);
		Board updatedBoard = boardRepository.save(board);
		return BoardResponseDto.fromEntity(updatedBoard);
	}

	@Override
    public Page<BoardResponseDto> getBoardList(int page, int size) {
        int startRow = page * size + 1;
        int endRow = (page + 1) * size;

        List<Board> boards = boardRepository.findAllOrderedByBoardIdx(startRow, endRow, Pageable.unpaged());
        long total = boardRepository.count(); // 총 게시글 수 가져오기

        List<BoardResponseDto> boardDtos = boards.stream()
            .map(BoardResponseDto::fromEntity)
            .collect(Collectors.toList());

        return new PageImpl<>(boardDtos, Pageable.ofSize(size).withPage(page), total);
    }
	
	@Override
	public List<BoardResponseDto> getBoardsByCategoryWithSorting(Category category) {
		List<Board> boards = boardRepository.findByCategoryOrderByBoardIdxDesc(category);
		return boards.stream().map(BoardResponseDto::fromEntity).collect(Collectors.toList());
	}

	@Override
	public List<BoardResponseDto> getBoardListByViews() {
		List<Board> boards = boardRepository.findAllByOrderByViewsDesc();
		return boards.stream().map(BoardResponseDto::fromEntity).collect(Collectors.toList());
	}

	@Override
	public List<BoardResponseDto> searchBoards(String keyword) {
		List<Board> boards = boardRepository.findByTitleContaining(keyword);
		return boards.stream().map(BoardResponseDto::fromEntity).collect(Collectors.toList());
	}
}
