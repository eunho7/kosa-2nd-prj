package com.example._team.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
import org.springframework.web.multipart.MultipartFile;

import com.example._team.domain.Board;
import com.example._team.domain.BoardFiles;
import com.example._team.domain.Users;
import com.example._team.domain.enums.Category;
import com.example._team.dto.board.BoardRequestDto;
import com.example._team.dto.board.BoardResponseDto;
import com.example._team.exception.DataNotFoundException;
import com.example._team.global.s3.AmazonS3Manager;
import com.example._team.repository.BoardFileRepository;
import com.example._team.repository.BoardRepository;
import com.example._team.repository.UserRepository;

@Service
public class BoardServiceImpl implements BoardService {

	@Autowired
	private BoardRepository boardRepository;

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BoardFileRepository boardFileRepository;
	
	@Autowired
	private AmazonS3Manager amazonS3Manager;

	
	//게시글작성
	@Transactional
	@Override
	public BoardResponseDto createBoard(BoardRequestDto boardDto) {
	    // 현재 로그인한 사용자의 정보를 가져옴
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    String email = authentication.getName();

	    // 이메일을 사용해서 해당 사용자의 정보를 조회
	    Users user = userRepository.findByEmail(email)
	            .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

	    // 게시글 생성
	    Board board = boardDto.toEntity(user, null);
	    Board savedBoard = boardRepository.save(board);

	 // 이미지 파일 업로드 처리 (다중 파일 처리)
	    if (boardDto.getImgFile() != null && !boardDto.getImgFile().isEmpty()) {
	        for (MultipartFile file : boardDto.getImgFile()) {
	            // 파일이 비어있지 않은 경우에만 업로드 처리
	            if (!file.isEmpty()) {
	                // S3에 파일 업로드 후 파일 URL 반환
	                String filePath = amazonS3Manager.uploadFile("boards/" + savedBoard.getBoardIdx(), file);

	                // 업로드된 파일 정보를 BoardFiles 엔티티에 저장
	                BoardFiles boardFile = new BoardFiles();
	                boardFile.setBoard(savedBoard);  // 게시글과 파일 연결
	                boardFile.setFilepath(filePath);  // S3에 저장된 파일의 URL
	                boardFile.setUploadedAt(LocalDateTime.now());
	                boardFileRepository.save(boardFile);  // 파일 정보 저장
	            }
	        }
	    }

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

	    // 삭제할 이미지 처리
	    if (dto.getDeleteImgs() != null && !dto.getDeleteImgs().isEmpty()) {
	        for (String filePath : dto.getDeleteImgs()) {
	            Optional<BoardFiles> fileOptional = boardFileRepository.findByFilepath(filePath);
	            if (fileOptional.isPresent()) {
	                BoardFiles file = fileOptional.get();
	                boardFileRepository.delete(file); // 데이터베이스에서 파일 정보 삭제
	            }
	        }
	    }

	    // 새로 추가할 이미지 처리
	    if (dto.getImgFile() != null && !dto.getImgFile().isEmpty()) {
	        for (MultipartFile file : dto.getImgFile()) {
	            if (!file.isEmpty()) {
	                String filePath = amazonS3Manager.uploadFile("boards/" + board.getBoardIdx(), file);

	                BoardFiles boardFile = new BoardFiles();
	                boardFile.setBoard(board);
	                boardFile.setFilepath(filePath);
	                boardFile.setUploadedAt(LocalDateTime.now());
	                boardFileRepository.save(boardFile);
	            }
	        }
	    }

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
	    Set<BoardResponseDto> boardDtos = new LinkedHashSet<>(); // 중복 방지를 위한 Set 사용
	    int startRow = page * size + 1; // 시작 행 번호
	    int endRow = (page + 1) * size; // 종료 행 번호
	    long total = 0; // 총 게시글 수

	    // 메인 게시글과 답변 게시글을 합친 전체 게시글 리스트
	    List<Board> baseBoards;

	    // 키워드 검색이 있는 경우 (공개된 게시글만 포함)
	    if (keyword != null && !keyword.trim().isEmpty()) {
	        baseBoards = boardRepository.findByKeywordOrderedByBoardIdx(keyword, startRow, endRow, Pageable.unpaged());
	        total = boardRepository.countByKeywordAndStatus(keyword, 1); // 공개된 게시글만 계산
	    }
	    // 카테고리가 있는 경우 (공개된 게시글만 포함)
	    else if (category != null) {
	        if ("views".equals(sort)) {
	            baseBoards = boardRepository.findByCategoryOrderedByViews(category.name(), startRow, endRow, Pageable.unpaged());
	        } else {
	            baseBoards = boardRepository.findByCategoryOrderedByBoardIdx(category.name(), startRow, endRow, Pageable.unpaged());
	        }
	        total = boardRepository.countByCategoryAndStatus(category, 1); // 공개된 게시글만 계산
	    }
	    // 카테고리가 없는 경우 (공개된 게시글만 포함)
	    else {
	        if ("views".equals(sort)) {
	            baseBoards = boardRepository.findAllOrderedByViews(startRow, endRow, Pageable.unpaged());
	        } else {
	            baseBoards = boardRepository.findAllOrderedByBoardIdx(startRow, endRow, Pageable.unpaged());
	        }
	        total = boardRepository.countByStatus(1); // 공개된 게시글만 계산
	    }

	    // 메인 게시글과 그에 해당하는 답변 게시글을 모두 포함하여 처리
	    for (Board baseBoard : baseBoards) {
	        if (boardDtos.size() >= size) {
	            break; // 10개 초과 시 중단
	        }
	        boardDtos.add(BoardResponseDto.fromEntity(baseBoard)); // 메인 게시글 추가

	        // 해당 메인 게시글의 답변 게시글 추가
	        List<Board> answers = boardRepository.findAnswersByBoard(baseBoard.getBoardIdx());
	        for (Board answer : answers) {
	            if (boardDtos.size() >= size) {
	                break; // 10개 초과 시 중단
	            }
	            boardDtos.add(BoardResponseDto.fromEntity(answer)); // 답변 게시글 추가
	        }
	    }

	    // 총 페이지 수 계산
	    int totalPages = (int) Math.ceil((double) total / size);

	    // 페이지 보정 처리
	    if (page >= totalPages && totalPages > 0) {
	        page = totalPages - 1; // 존재하지 않는 페이지 요청 시 마지막 페이지로 보정
	    }

	    // Set에서 List로 변환하여 PageImpl 반환
	    return new PageImpl<>(new ArrayList<>(boardDtos), PageRequest.of(page, size), total); // 중복 방지된 Set을 List로 변환하여 반환
	}



}
