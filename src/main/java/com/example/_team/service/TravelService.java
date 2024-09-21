package com.example._team.service;

import com.example._team.domain.Theme;
import com.example._team.domain.TravelBoard;
import com.example._team.domain.TravelImages;
import com.example._team.domain.TravelLikes;
import com.example._team.domain.Users;
import com.example._team.domain.enums.Region;
import com.example._team.exception.DataNotFoundException;
import com.example._team.global.s3.AmazonS3Manager;
import com.example._team.repository.ThemeRepository;
import com.example._team.repository.TravelImageRepository;
import com.example._team.repository.TravelLikesRepository;
import com.example._team.repository.TravelRepository;
import com.example._team.repository.UserRepository;
import com.example._team.service.global.DateUtils;
import com.example._team.web.dto.travelalbum.TravelAlbumRequestDTO.createTravelAlbumDTO;
import com.example._team.web.dto.travelalbum.TravelAlbumResponseDTO;
import com.example._team.web.dto.travelalbum.TravelAlbumResponseDTO.TravelAlbumByDate;
import com.example._team.web.dto.travelalbum.TravelAlbumResponseDTO.TravelAlbumDetailResponseDTO;
import com.example._team.web.dto.travelalbum.TravelAlbumResponseDTO.TravelAlbumImageListDTO;
import com.example._team.web.dto.travelalbum.TravelAlbumResponseDTO.TravelAlbumLikesResultDTO;
import com.example._team.web.dto.travelalbum.TravelAlbumResponseDTO.TravelAlbumListDTO;
import com.example._team.web.dto.travelalbum.TravelAlbumResponseDTO.TravelAlbumResultDTO;
import com.example._team.web.dto.travelalbum.TravelAlbumResponseDTO.TravelAlbumResultMapDTO;
import com.example._team.web.dto.travelalbum.TravelAlbumResponseDTO.TravelThemeListDTO;
import com.example._team.web.dto.travelalbum.TravelAlbumResponseDTO.myTravelAlbumListDTO;
import com.example._team.web.dto.travelalbum.TravelAlbumUpdateRequestDTO;
import com.example._team.web.dto.user.UserResponseDTO.UserListByPostLikesDTO;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class TravelService {
    private final TravelRepository travelRepository;
    private final TravelImageRepository travelImageRepository;
    private final ThemeRepository themeRepository;
    private final TravelLikesRepository travelLikesRepository;
    private final UserRepository userRepository;
    private final AmazonS3Manager s3ImgService;

    public List<TravelAlbumListDTO> searchTravelListByTheme(String theme, Integer isPublic) {
        List<Object[]> results = travelRepository.findAllByThemeName(theme, isPublic);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

        return results.stream().map(record -> {
            Integer id = (Integer) record[0];
            String title = (String) record[1];
            LocalDateTime statDate = ((Timestamp) record[2]).toLocalDateTime();
            LocalDateTime endDate = ((Timestamp) record[3]).toLocalDateTime();
            String thumbnail = (String) record[4];

            // 포맷된 날짜 문자열 생성
            String formattedDateRange = statDate.format(formatter) + " - " + endDate.format(formatter);

            return new TravelAlbumListDTO(id, title, formattedDateRange, thumbnail);
        }).collect(Collectors.toList());
    }

    public List<TravelAlbumListDTO> searchTravelListByRegion(Region region, Integer isPublic) {
        List<TravelBoard> results = travelRepository.findAllByRegionAndIsPublic(region, isPublic);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

        return results.stream().map(record -> {
            Integer id = record.getId();
            String title = record.getTitle();
            LocalDate statDate = record.getStatDate();
            LocalDate endDate = record.getEndDate();
            String thumbnail = record.getThumbnail();

            // 포맷된 날짜 문자열 생성
            String formattedDateRange = statDate.format(formatter) + " - " + endDate.format(formatter);

            return new TravelAlbumListDTO(id, title, formattedDateRange, thumbnail);
        }).collect(Collectors.toList());
    }

    public List<TravelAlbumListDTO> searchTravelListByThemeAndRegion(String theme, Region region, Integer isPublic) {
        List<Object[]> results = travelRepository.findAllByThemeAndRegionAndIsPublic(theme, region.name(), isPublic);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

        return results.stream().map(record -> {
            Integer id = (Integer) record[0];
            String title = (String) record[1];
            LocalDateTime statDate = ((Timestamp) record[2]).toLocalDateTime();
            LocalDateTime endDate = ((Timestamp) record[3]).toLocalDateTime();
            String thumbnail = (String) record[4];

            // 포맷된 날짜 문자열 생성
            String formattedDateRange = statDate.format(formatter) + " - " + endDate.format(formatter);

            return new TravelAlbumListDTO(id, title, formattedDateRange, thumbnail);
        }).collect(Collectors.toList());
    }

    public List<TravelAlbumDetailResponseDTO> getRandomTravelAlbums() {
        // 여행앨범 랜덤 조회
        List<TravelBoard> travelBoards = travelRepository.findRandomTravelBoards();

        if (travelBoards.isEmpty()) {
            throw new DataNotFoundException("여행 앨범이 존재하지 않습니다.");
        }

        List<TravelAlbumDetailResponseDTO> responseList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

        for (TravelBoard travelBoard : travelBoards) {
            String formattedDateRange =
                    travelBoard.getStatDate().format(formatter) + " - " + travelBoard.getEndDate().format(formatter);

            // 이미지 리스트 조회
            List<TravelAlbumImageListDTO> imageList = travelImageRepository.findByTravelIdx(travelBoard)
                    .stream()
                    .map(image -> TravelAlbumImageListDTO.builder()
                            .id(image.getImageIdx())
                            .imagePath(image.getImagePath())
                            .build())
                    .collect(Collectors.toList());

            // 테마 리스트 조회
            List<TravelThemeListDTO> themeList = themeRepository.findByTravelIdx(travelBoard)
                    .stream()
                    .map(theme -> TravelThemeListDTO.builder()
                            .id(theme.getThemeIdx())
                            .name(theme.getName())
                            .build())
                    .collect(Collectors.toList());

            TravelAlbumDetailResponseDTO responseDTO = TravelAlbumDetailResponseDTO.builder()
                    .id(travelBoard.getId())
                    .nickname(travelBoard.getUserIdx().getNickname()) // Users 엔티티의 닉네임
                    .dateRange(formattedDateRange) // 여행 날짜
                    .region(travelBoard.getRegion().toString()) // 지역
                    .thumbnail(travelBoard.getThumbnail()) // 썸네일
                    .title(travelBoard.getTitle()) // 타이틀
                    .travelAlbumImageList(imageList) // 이미지 리스트
                    .travelThemeList(themeList) // 테마 리스트
                    .build();

            responseList.add(responseDTO);
        }

        return responseList;
    }

    public TravelAlbumResultDTO postTravelAlbum(String email, createTravelAlbumDTO request, Long travelIdx) {
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new DataNotFoundException("유저가 존재하지 않습니다."));


        // travelIdx가 있는 경우 기존 TravelBoard를 가져와서 수정, 없는 경우 새로 생성

        TravelBoard travelBoard = travelRepository.findById(Math.toIntExact(travelIdx)).orElseThrow(()-> new DataNotFoundException("x"));


        // TravelBoard 필드 설정
        travelBoard.setTitle(request.getTitle());
        travelBoard.setContent(request.getContent());
        travelBoard.setRegion(Region.valueOf(request.getRegion()));
        travelBoard.setStatDate(request.getStatDate());
        travelBoard.setEndDate(request.getEndDate());
        travelBoard.setUserIdx(user);  // 유저 설정
        travelBoard.setIsPublic(request.getIsPublic());

        // 썸네일 처리 (새로운 파일이 있으면 덮어씌움)
        MultipartFile thumbnailFile = request.getThumbnail();
        if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
            String thumbnailFileName =
                    UUID.randomUUID().toString().substring(0, 10) + "-" + thumbnailFile.getOriginalFilename();
            String thumbnailKeyName = "travel/thumbnail/" + thumbnailFileName;
            String thumbnailUrl = s3ImgService.uploadFile(thumbnailKeyName, thumbnailFile);
            travelBoard.setThumbnail(thumbnailUrl);  // 썸네일 URL 설정
        }

        // TravelBoard 저장 (신규 생성 또는 기존 수정)
        travelRepository.save(travelBoard);

        // 사용자가 입력한 테마 리스트를 처리
        List<Theme> themes = request.getTravelThemeList().stream()
                .map(themeRequest -> {
                    Theme theme = new Theme();
                    theme.setName(themeRequest.getName());
                    theme.setTravelIdx(travelBoard);  // 테마에 TravelBoard 설정
//                    return themeRepository.save(theme);
                    return theme;
                }).collect(Collectors.toList());

        // 본문에서 이미지 URL 추출 및 저장
        List<String> imageUrls = extractImageUrlsFromContent(request.getContent());
        List<TravelImages> savedImgs = imageUrls.stream()
                .map(imgUrl -> {
                    TravelImages travelImage = new TravelImages();
                    travelImage.setImagePath(imgUrl);
                    travelImage.setUploadedAt(LocalDateTime.now());
                    travelImage.setTravelIdx(travelBoard);  // TravelBoard와 연결
                    return travelImage;
                })
                .collect(Collectors.toList());

        themeRepository.saveAll(themes);
        travelImageRepository.saveAll(savedImgs);


        return TravelAlbumResultDTO.builder()
                .travelIdx(travelBoard.getId())  // TravelBoard의 ID 반환
                .build();
    }

    private List<String> extractImageUrlsFromContent(String content) {
        List<String> imageUrls = new ArrayList<>();
        Document doc = Jsoup.parse(content);  // Jsoup을 사용하여 HTML 파싱
        Elements imgElements = doc.select("img");  // 모든 <img> 태그를 선택
        for (Element imgElement : imgElements) {
            String imgUrl = imgElement.attr("src");  // 이미지 URL 추출
            imageUrls.add(imgUrl);
        }
        return imageUrls;
    }

    public TravelAlbumDetailResponseDTO getTravelBoard(Integer id, Users user) {
        TravelBoard travelBoard = travelRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("해당 여행앨범이 존재하지 않습니다."));
        Long postLikesCnt = travelLikesRepository.countAllByTravelIdx(travelBoard);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        String formattedDateRange =
                travelBoard.getStatDate().format(formatter) + " - " + travelBoard.getEndDate().format(formatter);

        List<TravelAlbumImageListDTO> imageList = travelImageRepository.findByTravelIdx(travelBoard)
                .stream()
                .map(image -> TravelAlbumImageListDTO.builder()
                        .id(image.getImageIdx())
                        .imagePath(image.getImagePath())
                        .build())
                .collect(Collectors.toList());

        List<TravelThemeListDTO> themeList = themeRepository.findByTravelIdx(travelBoard)
                .stream()
                .map(theme -> TravelThemeListDTO.builder()
                        .id(theme.getThemeIdx())
                        .name(theme.getName())
                        .build())
                .collect(Collectors.toList());
        boolean likedByCurrentUser = isLikedByUser(travelBoard, user);

        return TravelAlbumDetailResponseDTO.builder()
                .id(travelBoard.getId())
                .userIdx(travelBoard.getUserIdx().getUserIdx())
                .title(travelBoard.getTitle())
                .content(travelBoard.getContent())
                .thumbnail(travelBoard.getThumbnail())
                .region(travelBoard.getRegion().name())
                .dateRange(formattedDateRange)
                .postLikeCount(postLikesCnt)
                .likedByCurrentUser(likedByCurrentUser)
                .isPublic(travelBoard.getIsPublic())
                .travelAlbumImageList(imageList)
                .travelThemeList(themeList)
                .build();
    }

    // 삭제
    @Transactional
    public void deleteTravelBoard(Integer id) {

        TravelBoard travelBoard = travelRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("TravelBoard not found"));

        travelImageRepository.deleteByTravelIdx(travelBoard);

        themeRepository.deleteByTravelIdx(travelBoard);
        travelLikesRepository.deleteByTravelIdx(travelBoard);

        travelRepository.delete(travelBoard);

    }

    public boolean isLikedByUser(TravelBoard travelIdx, Users user) {
        return travelLikesRepository.existsByUserIdxAndTravelIdx(user, travelIdx);
    }

    public List<UserListByPostLikesDTO> getTravelLikesByUsers(Integer id) {
        TravelBoard travelBoard = travelRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("해당 여행앨범이 존재하지 않습니다."));

        List<TravelLikes> users = travelLikesRepository.findUsersByTravelIdx(travelBoard);

        List<UserListByPostLikesDTO> userList = users.stream()
                .map(user -> UserListByPostLikesDTO.builder()
                        .userIdx(user.getUserIdx().getUserIdx())
                        .nickname(user.getUserIdx().getNickname())
                        .build())
                .collect(Collectors.toList());

        return userList;
    }

    public boolean addLike(Integer travelIdx, Long userIdx) {
        return handleLike(travelIdx, userIdx, true);
    }

    @Transactional
    public boolean removeLike(Integer travelIdx, Long userIdx) {
        return handleLike(travelIdx, userIdx, false);
    }

    @Transactional
    public boolean handleLike(Integer travelIdx, Long userIdx, boolean isLike) {
        Optional<TravelBoard> travelOpt = travelRepository.findById(travelIdx);
        Users user = userRepository.findById(userIdx).orElseThrow(() -> new DataNotFoundException("X"));

        if (travelOpt.isPresent()) {
            TravelBoard travel = travelOpt.get();

            // 이미 좋아요한 유저인지 확인
            boolean alreadyLiked = travelLikesRepository.existsByUserIdxAndTravelIdx(user, travel);

            if (isLike && !alreadyLiked) {
                travel.addLike();
                // 좋아요 추가
                TravelLikes like = new TravelLikes();
                like.setTravelIdx(travel);
                like.setUserIdx(user);
                travelRepository.save(travel);
                travelLikesRepository.save(like);
                return true;
            } else if (!isLike && alreadyLiked) {
                travel.removeLike();
                travelRepository.save(travel);
                // 좋아요 취소
                travelLikesRepository.deleteByTravelIdxAndUserIdx(travel, user);
                return true;
            }
        }

        return false;
    }

    public List<myTravelAlbumListDTO> getMyTravelBoardSortList(Users user, String sort) {
        List<TravelBoard> travelBoards = travelRepository.findAllByUserIdxSorted(user, sort);

        return travelBoards.stream().map(board -> {
            myTravelAlbumListDTO dto = new myTravelAlbumListDTO();
            dto.setId(board.getId());
            dto.setThumbnail(board.getThumbnail());
            dto.setTitle(board.getTitle());
            dto.setDateRange(DateUtils.formatDateRange(board.getStatDate(), board.getEndDate()));
            dto.setLikes(board.getLikeCount());
            dto.setCreatedAt(board.getUpdatedAt());
            return dto;
        }).collect(Collectors.toList());
    }

    public TravelAlbumResponseDTO.TravelAlbumResultMapDTO createNullData() {
        TravelBoard travelBoard = new TravelBoard();
        TravelBoard response = travelRepository.save(travelBoard);
        Long idx = Long.valueOf(response.getId());
        return TravelAlbumResponseDTO.TravelAlbumResultMapDTO.builder()
                .travelIdx(idx)
                .build();
    }

    public List<TravelAlbumByDate> getTravelAlbumByDate () {
        List<TravelBoard> travelBoards = travelRepository.findRecentPublicAlbumsNative();
        return travelBoards.stream().map(board -> {
            TravelAlbumByDate dto = new TravelAlbumByDate();
            dto.setNickName(board.getUserIdx().getNickname());
            dto.setTitle(board.getTitle());
            dto.setDateRange(DateUtils.formatDateRange(board.getStatDate(), board.getEndDate()));
            dto.setThumbnail(board.getThumbnail());
            dto.setId(board.getId());
            return dto;
        }).collect(Collectors.toList());
    }

    @Transactional
    public boolean updateTravelBoard(Integer travelIdx, TravelAlbumUpdateRequestDTO request, Users user) {
        TravelBoard travelBoard = travelRepository.findById(travelIdx)
                .orElseThrow(() -> new DataNotFoundException("해당 여행앨범이 존재하지 않습니다."));

        // 현재 사용자가 소유자인지 확인
        if (!travelBoard.getUserIdx().equals(user)) {
            throw new IllegalArgumentException("수정 권한이 없습니다.");
        }

        // 여행 보드 업데이트
        travelBoard.setTitle(request.getTitle());
        travelBoard.setContent(request.getContent());
        travelBoard.setRegion(Region.valueOf(request.getRegion()));
        travelBoard.setStatDate(request.getStatDate());
        travelBoard.setEndDate(request.getEndDate());
        travelBoard.setIsPublic(request.getIsPublic());

        // 썸네일 이미지가 새로 업로드된 경우
        if (request.getThumbnail() != null && !request.getThumbnail().isEmpty()) {
            MultipartFile thumbnailFile = request.getThumbnail();
            String thumbnailFileName = UUID.randomUUID().toString().substring(0, 10) + "-" + thumbnailFile.getOriginalFilename();
            String thumbnailKeyName = "travel/thumbnail/" + thumbnailFileName;
            String thumbnailUrl = s3ImgService.uploadFile(thumbnailKeyName, thumbnailFile);
            travelBoard.setThumbnail(thumbnailUrl);
        }

        // 테마 업데이트 (기존 테마 삭제 후 새로운 테마 추가)
        themeRepository.deleteByTravelIdx(travelBoard);
        request.getTravelThemeList().forEach(themeRequest -> {
            Theme theme = new Theme();
            theme.setName(themeRequest.getName());
            theme.setTravelIdx(travelBoard);
            themeRepository.save(theme);
        });

        travelRepository.save(travelBoard);
        return true;
    }
}