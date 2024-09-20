package com.example._team.controller;

import com.example._team.domain.Users;
import com.example._team.service.UserService;
import jakarta.mail.MessagingException;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserViewController {
    @GetMapping("/login")
    public String login(){
        return "view/user/login";
    }

    @GetMapping("/signup")
    public String singup(){
        return "view/user/signup";
    }

    // 비밀번호 재설정 요청 페이지로 이동하는 매핑 추가
    @GetMapping("/password/reset-request")
    public String resetRequest() {
        return "view/user/reset-request"; // templates/reset-request.html 파일을 렌더링
    }

    @GetMapping("/user/find-id")
    public String showFindIdForm() {
        // 폼을 보여주는 GET 요청 처리
        return "view/user/find-id";  // find-id 폼 페이지로 이동
    }

    @Autowired
    private UserService userService;

    @GetMapping("/mypage")
    public String myPage(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();  // 현재 사용자 이름 가져오기
        Users user = userService.findByEmail(username);  // userService 인스턴스를 통해 호출
        model.addAttribute("user", user);
        return "view/user/mypage";  // templates/mypage.html 템플릿 렌더링
    }

    // 마이페이지에서 사용자 정보 수정 폼 요청
    @GetMapping("/mypage/edit")
    public String editUserInfo(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();  // 현재 사용자 이름 가져오기
        Users user = userService.findByEmail(username);  // userService 인스턴스를 통해 호출
        model.addAttribute("user", user);
        return "view/user/edit-mypage";  // templates/edit-mypage.html 템플릿 렌더링
    }

    @PostMapping("/mypage/edit")
    public ResponseEntity<String> updateUserInfo(@RequestBody Users updatedUser) {
        if (updatedUser.getUserIdx() == null) {
            throw new IllegalArgumentException("The given id must not be null");
        }

        Users user = userService.findByEmail(String.valueOf(updatedUser.getUserIdx()));


        user.setNickname(updatedUser.getNickname());
        user.setPhone(updatedUser.getPhone());

        userService.save(user);

        return ResponseEntity.ok("User updated successfully");
    }


    // 비밀번호 변경 페이지
    @GetMapping("/mypage/change-password")
    public String changePasswordForm() {
        return "view/user/change-password"; // 비밀번호 변경 페이지로 이동
    }

    // 비밀번호 변경 처리
    @PostMapping("/mypage/change-password")
    public String changePassword(@RequestParam("oldPassword") String oldPassword,
                                 @RequestParam("newPassword") String newPassword) {
        userService.changePassword(oldPassword, newPassword);  // 인스턴스 메서드 호출
        return "redirect:/mypage";  // 비밀번호 변경 후 마이페이지로 리디렉션
    }
}