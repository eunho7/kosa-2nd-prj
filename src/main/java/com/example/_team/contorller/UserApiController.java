package com.example._team.contorller;

import com.example._team.dto.AddUserRequest;
import com.example._team.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class UserApiController {

    private final UserService userService;
    
    @Autowired
    public UserApiController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/user")
    public RedirectView signup(AddUserRequest request){
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
}
