package com.example._team.service;

import com.example._team.domain.Users;
import com.example._team.domain.enums.Authority;
//import com.example._team.dto.AddUserRequest;
import com.example._team.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final EmailService emailService;
    private final HttpSession session;

    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder, EmailService emailService, HttpSession session) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.emailService = emailService;
        this.session = session;
    }

    //비밀번호 초기화
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

        // 이메일 본문을 HTML 형식으로 꾸밈
        String htmlContent = """
    <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border-radius: 10px; background-color: #f9f9f9; box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);">
        <h2 style="color: #007bff; text-align: center;">비밀번호 재설정 요청</h2>
        <p style="font-size: 16px; color: #555; text-align: center;">
            안녕하세요,<br>비밀번호 재설정을 요청하셨습니다. 아래 버튼을 클릭하여 비밀번호를 재설정하세요.
        </p>
        <div style="text-align: center; margin: 30px 0;">
            <a href="%s" style="display: inline-block; padding: 12px 30px; background-color: #007bff; color: white; font-weight: bold; text-decoration: none; border-radius: 5px;">비밀번호 재설정</a>
        </div>
        <p style="font-size: 14px; color: #555; text-align: center;">
            만약 본인이 요청한 것이 아니라면, 이 이메일을 무시하셔도 됩니다.<br>감사합니다.
        </p>
        <hr style="border: none; border-top: 1px solid #ddd;">
        <p style="font-size: 12px; color: #999; text-align: center;">
            &mdash; Team Name
        </p>
    </div>
    """.formatted(resetLink);

        // 이메일 전송
        emailService.sendHtmlMessage(email, "비밀번호 재설정 요청", htmlContent);
    }

    // 전화번호로 사용자 이메일 조회
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

    // 이메일로 인증번호 전송
    public String sendEmailVerification(String email) throws MessagingException {
        String verificationCode = generateVerificationCode();
        emailService.sendVerificationEmail(email, verificationCode); // 인증번호 전송
        session.setAttribute("verificationCode", verificationCode); // 세션에 인증번호 저장
        return verificationCode;
    }

    // 인증번호 생성
    private String generateVerificationCode() {
        Random random = new Random();
        int code = random.nextInt(999999);
        return String.format("%06d", code); // 6자리 인증번호 생성
    }

    // 인증번호 확인
    public boolean verifyCode(String inputCode, String correctCode) {
        return inputCode.equals(correctCode); // 입력된 인증번호와 세션에 저장된 인증번호를 비교
    }

    //토큰 생성
    private String generateToken() {
        return UUID.randomUUID().toString();
    }

    @Transactional
    public Long save(Users dto) {

        // 예시: DTO에 대한 유효성 검사 추가
        if (dto.getEmail() == null || dto.getPassword() == null || dto.getNickname() == null || dto.getPhone() == null) {
            throw new IllegalArgumentException("필수 입력 값이 누락되었습니다.");
        }

        // AddUserRequest의 값을 출력해 확인
        System.out.println("Email: " + dto.getEmail());
        System.out.println("Password: " + dto.getPassword());
        System.out.println("Nickname: " + dto.getNickname());
        System.out.println("Phone: " + dto.getPhone());

        Users user = Users.builder()
                .email(dto.getEmail())
                .password(bCryptPasswordEncoder.encode(dto.getPassword()))
                .nickname(dto.getNickname())
                .phone(dto.getPhone())
                .status(1)
                .authority(dto.getAuthority() != null ? dto.getAuthority() : Authority.USER)
                .build();

        return userRepository.save(user).getUserIdx();
    }
}
