package com.example._team.service;

import com.example._team.dto.board.BoardRequestDto;
import com.example._team.dto.board.BoardResponseDto;
import com.example._team.domain.Board;
import com.example._team.domain.Users;
import com.example._team.domain.enums.Category;
import com.example._team.exception.DataNotFoundException;
import com.example._team.repository.BoardRepository;
import com.example._team.repository.UsersRepositoty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BoardServiceImpl implements BoardService {

	@Autowired
	private BoardRepository boardRepository;

	@Autowired
	private UsersRepositoty usersRepository;

	@Transactional
	@Override
	public void savePost(BoardRequestDto boardDto) { // 게시글 작성
		Integer userId = boardDto.getUserIdx();
		Users user = usersRepository.findById(userId)
				.orElseThrow(() -> new DataNotFoundException("User not found with ID: " + userId));
		Board board = boardDto.toEntity(user, null);
		boardRepository.save(board);
	}

	@Transactional
	@Override
	public void update(Integer id, BoardRequestDto dto) { // 게시글 업데이트
		Board board = boardRepository.findById(id)
				.orElseThrow(() -> new DataNotFoundException("Board not found with ID: " + id));
		board.setTitle(dto.getTitle());
		board.setContent(dto.getContent());
		board.setCategory(dto.getCategory());
		boardRepository.save(board);
	}

	@Transactional
	@Override
	public void deletePost(Integer id) { // 게시글 삭제
		Board board = boardRepository.findById(id)
				.orElseThrow(() -> new DataNotFoundException("Board not found with ID: " + id));
		boardRepository.delete(board);
	}

	@Transactional
	@Override // 게시글 상세조회
	public BoardResponseDto getPost(Integer id) { // 게시글 검색 boardidx로 확인
		Board board = boardRepository.findById(id)
				.orElseThrow(() -> new DataNotFoundException("Board not found with ID: " + id));

		// 조회수를 증가시킵니다.
		board.setViews(board.getViews() + 1);

		// 업데이트된 게시글을 저장합니다.
		boardRepository.save(board);

		return BoardResponseDto.fromEntity(board);
	}

	@Override
	public List<BoardResponseDto> getBoardList(Integer pageNum) { // 게시글 리스트 목록 ( 메인 페이지 )
		int pageSize = 10; // 페이지당 항목 수
		List<Board> boards = boardRepository.findAll(); // 페이지네이션 로직을 추가할 수 있음
		return boards.stream()
				.map(BoardResponseDto::fromEntity).collect(Collectors.toList());
	}
	
	//페이징 테스트 코드
//	@Override
//    public Page<BoardResponseDto> getBoardList(Pageable pageable) {
//        // 게시판 리스트를 페이징 및 정렬하여 가져옴
//        Page<Board> boardPage = boardRepository.findAll(pageable);
//
//        // Board 엔티티를 BoardResponseDto로 변환
//        return boardPage.map(BoardResponseDto::fromEntity);
//    }


	@Transactional
	@Override
	public List<BoardResponseDto> searchPosts(String keyword) { // 게시글 키워드 검색
		List<Board> boards = boardRepository.findByTitleContaining(keyword);
		return boards.stream()
				.map(BoardResponseDto::fromEntity) // fromEntity 메소드 사용
				.collect(Collectors.toList());
	}

	@Transactional
    @Override
    public List<BoardResponseDto> getPostsByCategoryWithSorting(Category category) {
        List<Board> boards = boardRepository.findByCategoryOrderByBoardIdxDesc(category);
        return boards.stream()
                .map(BoardResponseDto::fromEntity)
                .collect(Collectors.toList());
    }
	
	 @Transactional
	 @Override
	 public List<BoardResponseDto> getBoardListByViews() {
	     List<Board> boards = boardRepository.findAllByOrderByViewsDesc();
	     return boards.stream()
	             .map(BoardResponseDto::fromEntity)
	             .collect(Collectors.toList());
	    }

}
