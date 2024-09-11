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
import com.example._team.web.dto.travelalbum.TravelAlbumRequestDTO.createTravelAlbumDTO;
import com.example._team.web.dto.travelalbum.TravelAlbumResponseDTO.TravelAlbumDetailResponseDTO;
import com.example._team.web.dto.travelalbum.TravelAlbumResponseDTO.TravelAlbumImageListDTO;
import com.example._team.web.dto.travelalbum.TravelAlbumResponseDTO.TravelAlbumLikesResultDTO;
import com.example._team.web.dto.travelalbum.TravelAlbumResponseDTO.TravelAlbumListDTO;
import com.example._team.web.dto.travelalbum.TravelAlbumResponseDTO.TravelAlbumResultDTO;
import com.example._team.web.dto.travelalbum.TravelAlbumResponseDTO.TravelThemeListDTO;
import com.example._team.web.dto.user.UserResponseDTO.UserListByPostLikesDTO;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
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

    public TravelAlbumDetailResponseDTO getRandomTravelAlbum() {
        // 여행앨범 랜덤 조회
        TravelBoard travelBoard = travelRepository.findRandomTravelBoard();

        if (travelBoard == null) {
            throw new DataNotFoundException("여행 앨범이 존재하지 않습니다.");
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
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

        return TravelAlbumDetailResponseDTO.builder()
                .id(travelBoard.getId())
                .nickname(travelBoard.getUserIdx().getNickname()) // Users 엔티티의 닉네임
                .dateRange(formattedDateRange) // 여행 날짜
                .region(travelBoard.getRegion().toString()) // 지역
                .thumbnail(travelBoard.getThumbnail()) // 썸네일
                .title(travelBoard.getTitle()) // 타이틀
                .travelAlbumImageList(imageList) // 이미지 리스트
                .travelThemeList(themeList) // 테마 리스트
                .build();
    }

    public TravelAlbumLikesResultDTO postAlbumLikes(Integer travelIdx) {
        TravelBoard travelBoard = travelRepository.findById(travelIdx)
                .orElseThrow(() -> new DataNotFoundException("해당 여행앨범이 존재하지 않습니다."));

        boolean alreadyLiked = travelLikesRepository.existsByUserIdxAndTravelIdx(travelBoard.getUserIdx(), travelBoard);

        if (alreadyLiked) {
            throw new IllegalArgumentException("이미 이 앨범에 좋아요를 눌렀습니다.");
        }

        TravelLikes travelLikes = new TravelLikes();
        travelLikes.setTravelIdx(travelBoard);
        travelLikes.setUserIdx(travelBoard.getUserIdx());

        travelLikesRepository.save(travelLikes);
        return TravelAlbumLikesResultDTO.builder()
                .travelLikesIdx(travelLikes.getLikeIdx())
                .build();
    }

    public TravelAlbumLikesResultDTO cancelTravelAlbumLikes(Integer travelIdx) {
        TravelBoard travelBoard = travelRepository.findById(travelIdx)
                .orElseThrow(() -> new DataNotFoundException("해당 여행앨범이 존재하지 않습니다."));

        TravelLikes travelLikes = travelLikesRepository.findByUserIdxAndTravelIdx(travelBoard.getUserIdx(),
                travelBoard);

        travelLikesRepository.delete(travelLikes);

        return TravelAlbumLikesResultDTO.builder()
                .travelLikesIdx(travelLikes.getLikeIdx())
                .build();
    }

    public TravelAlbumResultDTO postTravelAlbum(String email, createTravelAlbumDTO request) {
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new DataNotFoundException("유저가 존재하지 않습니다."));

        TravelBoard newTravel = new TravelBoard();
        newTravel.setTitle(request.getTitle());
        newTravel.setContent(request.getContent());
        newTravel.setRegion(Region.valueOf(request.getRegion()));
        newTravel.setStatDate(request.getStatDate());
        newTravel.setEndDate(request.getEndDate());
        newTravel.setUserIdx(user);
        newTravel.setIsPublic(request.getIsPublic());

        // 썸네일을 S3에 업로드
        MultipartFile thumbnailFile = request.getThumbnail();

        String thumbnailFileName =
                UUID.randomUUID().toString().substring(0, 10) + "-" + thumbnailFile.getOriginalFilename();
        String thumbnailKeyName = "travel/thumbnail/" + thumbnailFileName;
        String thumbnailUrl = s3ImgService.uploadFile(thumbnailKeyName, thumbnailFile);

        // 썸네일 URL 설정
        newTravel.setThumbnail(thumbnailUrl);
        travelRepository.save(newTravel);

        // 사용자가 입력한 테마 리스트를 처리
        List<Theme> themes = request.getTravelThemeList().stream()
                .map(themeRequest -> {
                    Theme theme = new Theme();
                    theme.setName(themeRequest.getName());
                    theme.setTravelIdx(newTravel);  // 테마에 TravelBoard 설정
                    return themeRepository.save(theme);
                }).collect(Collectors.toList());

        List<String> imageUrls = extractImageUrlsFromContent(request.getContent());
        List<TravelImages> savedImgs = imageUrls.stream()
                .map(imgUrl -> {
                    TravelImages travelImage = new TravelImages();
                    travelImage.setImagePath(imgUrl);
                    travelImage.setUploadedAt(LocalDateTime.now());
                    travelImage.setTravelIdx(newTravel);  // 새로운 게시글과 연결
                    return travelImage;
                })
                .collect(Collectors.toList());

        travelImageRepository.saveAll(savedImgs);
        themeRepository.saveAll(themes);

        return TravelAlbumResultDTO.builder()
                .travelIdx(newTravel.getId())
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
                .travelAlbumImageList(imageList)
                .travelThemeList(themeList)
                .build();
    }

    // 삭제
//    @Override
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
        // 여행 앨범을 조회합니다.
        TravelBoard travelBoard = travelRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("해당 여행앨범이 존재하지 않습니다."));

        // 여행 앨범에 좋아요를 누른 유저 리스트를 조회합니다.
        List<TravelLikes> users = travelLikesRepository.findUsersByTravelIdx(travelBoard);

        // 유저 정보를 UserListByPostLikesDTO 리스트로 변환합니다.
        List<UserListByPostLikesDTO> userList = users.stream()
                .map(user -> UserListByPostLikesDTO.builder()
                        .userIdx(user.getUserIdx().getUserIdx()) // 유저의 ID를 설정합니다.
                        .nickname(user.getUserIdx().getNickname()) // 유저의 닉네임을 설정합니다.
                        .build())
                .collect(Collectors.toList());

        return userList;
    }
    public boolean addLike(Integer travelIdx, Long userIdx) {
        TravelBoard travelBoard = travelRepository.findById(travelIdx)
                .orElseThrow(() -> new DataNotFoundException("해당 여행앨범이 존재하지 않습니다."));
        Users user = userRepository.findById(userIdx)
                .orElseThrow(() -> new DataNotFoundException("해당 여행앨범이 존재하지 않습니다."));

        // 좋아요 추가
        TravelLikes travelLikes = new TravelLikes();
        travelLikes.setTravelIdx(travelBoard);
        travelLikes.setUserIdx(user);
        travelLikesRepository.save(travelLikes);

        return true;
    }

    public boolean removeLike(Integer travelIdx, Long userIdx) {
        TravelBoard travelBoard = travelRepository.findById(travelIdx)
                .orElseThrow(() -> new DataNotFoundException("해당 여행앨범이 존재하지 않습니다."));
        Users user = userRepository.findById(userIdx)
                .orElseThrow(() -> new DataNotFoundException("해당 여행앨범이 존재하지 않습니다."));
        // 좋아요 엔티티 찾기
        TravelLikes travelLikes = travelLikesRepository.findByUserIdxAndTravelIdx(user, travelBoard);

        if (travelLikes != null) {
            travelLikesRepository.delete(travelLikes);
            return true;
        }

        return false;
    }
}