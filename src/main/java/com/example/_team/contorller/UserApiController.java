package com.example._team.contorller;

import com.example._team.domain.Users;
//import com.example._team.dto.AddUserRequest;
import com.example._team.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class UserApiController {

    private final UserService userService;
    private final HttpSession session;

    @Autowired
    public UserApiController(UserService userService, HttpSession session) {
        this.userService = userService;
        this.session = session;
    }

    @PostMapping("/user")
    public RedirectView signup(Users request){
        userService.save(request); //회원 가입 메서드 호출
        return new RedirectView("/login"); //회원 가입이 완료된 이후에 로그인 페이지 이동
    }

    // 전화번호로 이메일 찾기
    @PostMapping("/user/find-id")
    public String findIdByPhone(@RequestParam("phone") String phone, Model model) {
        String email = userService.findEmailByPhone(phone);

        if (email != null) {
            model.addAttribute("email", email);
            return "find-id-result"; // 리디렉션 없이 바로 결과 페이지 렌더링
        } else {
            model.addAttribute("errorMessage", "해당 전화번호로 등록된 이메일이 없습니다.");
            return "find-id-result"; // 리디렉션 없이 결과 페이지에서 오류 메시지 표시
        }
    }

    //회원탈퇴
    @PostMapping("/mypage/deactivate")
    public String deactivateAccount() {
        // 현재 로그인된 사용자 이메일 가져오기
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // 회원 탈퇴 처리
        userService.deactivateAccount(username);

        // 로그아웃 처리 및 로그인 페이지로 리디렉션
        return "redirect:/login";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response){
        new SecurityContextLogoutHandler().logout(request, response,
                SecurityContextHolder.getContext().getAuthentication());
        return "redirect:/login";
    }

    // 인증번호 전송
    @PostMapping("/signup/send-code")
    public String sendVerificationCode(@RequestParam("email") String email, Model model) throws MessagingException {
        String verificationCode = userService.sendEmailVerification(email);
        session.setAttribute("verificationCode", verificationCode);
        model.addAttribute("email", email);
        model.addAttribute("verificationCode", verificationCode);
        return "signup"; // 인증번호 입력 후 회원가입 페이지 유지
    }

    // 인증번호 확인 및 회원가입 처리
    @PostMapping("/signup/verify")
    public String verifyAndRegister(@RequestParam("verificationCode") String inputCode,
                                    @ModelAttribute Users userRequest,
                                    Model model) {
        String correctCode = (String) session.getAttribute("verificationCode");

        if (userService.verifyCode(inputCode, correctCode)) {
            userService.save(userRequest);  // 회원 정보 저장
            session.removeAttribute("verificationCode");
            return "redirect:/login";  // 인증 성공 후 로그인 페이지로 이동
        } else {
            model.addAttribute("error", "인증번호가 올바르지 않습니다.");
            return "signup";  // 인증번호가 틀린 경우 다시 회원가입 페이지로 이동
        }
    }

    @PostMapping("/signup/complete")
    public String completeSignup(@ModelAttribute Users user) {
        userService.save(user); // UserService의 save 메서드 호출
        return "redirect:/login"; // 회원가입 완료 후 로그인 페이지로 리디렉션
    }
}
