package com.example._team.contorller;

import com.example._team.service.EmailService;
import com.example._team.service.UserService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PasswordResetController {
    private final UserService userService;
    private final EmailService emailService;

    @Autowired
    public PasswordResetController(UserService userService,EmailService emailService) {
        this.userService = userService;
        this.emailService = emailService;
    }



    // 토큰을 URL에서 받는 부분
    @GetMapping("/password/reset")
    public String showResetPasswordPage(@RequestParam("token") String token, Model model) {
        model.addAttribute("token", token); // 토큰을 모델에 추가하여 폼에 넘깁니다.
        return "reset-password"; // 비밀번호 재설정 페이지로 이동
    }

    // 비밀번호 재설정 링크 전송 요청을 처리하는 POST 메서드
    @PostMapping("/password/reset-request")
    public String handlePasswordResetRequest(@RequestParam("email") String email) throws MessagingException {
        userService.sendPasswordResetLink(email);  // 비밀번호 재설정 링크 전송
        return "redirect:/login";  // 성공 시 보여줄 페이지
    }

    // 새로운 비밀번호를 설정하는 POST 메서드
    @PostMapping("/password/reset")
    public String handlePasswordReset(@RequestParam("token") String token, @RequestParam("newPassword") String newPassword) {
        boolean success = userService.resetPassword(token, newPassword);
        if (!success) {
            return "invalid-token";  // 토큰이 유효하지 않거나 만료된 경우
        }
        return "redirect:/login";  // 비밀번호 재설정 성공 시 이동할 페이지
    }
}
