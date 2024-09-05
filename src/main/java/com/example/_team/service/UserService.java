package com.example._team.service;

import com.example._team.domain.Users;
import com.example._team.domain.enums.Authority;
import com.example._team.dto.AddUserRequest;
import com.example._team.repository.UserRepository;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final EmailService emailService;

    @Autowired
    public UserService(UserRepository userRepository,BCryptPasswordEncoder bCryptPasswordEncoder,EmailService emailService) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.emailService = emailService;
    }

    public boolean resetPassword(String token, String newPassword) {
        Users user = userRepository.findByResetToken(token)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 토큰입니다."));

        // 토큰 만료 확인
        if (user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            return false; // 만료된 토큰
        }
        user.setPassword(bCryptPasswordEncoder.encode(newPassword));
        user.setResetToken(null); // 토큰을 사용 후 제거
        userRepository.save(user);

        return true;
    }

    // 비밀번호 변경 메서드
    public void changePassword(String oldPassword, String newPassword) {
        // 현재 로그인된 사용자 이메일로 사용자 조회
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Users user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        // 기존 비밀번호가 맞는지 확인
        if (bCryptPasswordEncoder.matches(oldPassword, user.getPassword())) {
            // 새 비밀번호 암호화 후 저장
            user.setPassword(bCryptPasswordEncoder.encode(newPassword));
            userRepository.save(user);
        } else {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }
    }

    // 토큰 유효성 검증 메서드
    public boolean validateResetToken(String token) {
        // 토큰을 가진 사용자를 검색
        Users user = userRepository.findByResetToken(token)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 토큰입니다."));

        // 토큰 만료 여부 확인
        if (user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("토큰이 만료되었습니다.");
        }

        return userRepository.existsByResetToken(token); // 토큰이 유효하다면 true 반환
    }

    public void sendPasswordResetLink(String email) throws MessagingException {
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("해당 이메일을 가진 사용자가 없습니다."));

        // 고유한 토큰 생성
        String token = generateToken();
        user.setResetToken(token);
        user.setResetTokenExpiry(LocalDateTime.now().plusHours(1));
        userRepository.save(user);

        // 비밀번호 재설정 링크 생성
        String resetLink = "http://localhost:8081/password/reset?token=" + token;

        // 이메일 전송
        String message = "비밀번호를 재설정하려면 다음 링크를 클릭하세요: " + resetLink;
        emailService.sendHtmlMessage(email, "비밀번호 재설정 요청", message);
    }

    public String findEmailByPhone(String phone){
        return userRepository.findEmailByPhone(phone);
    }
    // 이메일로 사용자 조회
    public Users findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
    }

    // 사용자 정보 업데이트
    public void updateUser(Users updatedUser) {
        Users existingUser = userRepository.findById(updatedUser.getUserIdx())
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 없습니다."));
        existingUser.setNickname(updatedUser.getNickname());
        existingUser.setPhone(updatedUser.getPhone());
        userRepository.save(existingUser);  // 사용자 정보 업데이트
    }

    // 회원 탈퇴 처리 (status를 0으로 변경)
    public void deactivateAccount(String email) {
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("해당 사용자를 찾을 수 없습니다."));

        // status 값을 0으로 변경하여 탈퇴 처리
        user.setStatus(0);
        userRepository.save(user);  // 변경 사항 저장
    }

    private String generateToken() {
        return UUID.randomUUID().toString();
    }

    public Long save(AddUserRequest dto){

        Users user = Users.builder()
                .email(dto.getEmail())
                .password(bCryptPasswordEncoder.encode(dto.getPassword()))
                .nickname(dto.getNickname())
                .phone(dto.getPhone())
                .status(1) // STATUS 필드에 값을 설정
                .authority(dto.getAuthority() != null ? dto.getAuthority() : Authority.USER) // 기본값으로 USER 설정
                .build();

        return userRepository.save(user).getUserIdx();
    }
}
