package com.example._team.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // HTML 형식의 이메일을 전송하는 메서드
    public void sendHtmlMessage(String to, String subject, String htmlContent) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();  // 이메일 메시지 생성
        MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");

        helper.setTo(to);  // 수신자 이메일 주소
        helper.setSubject(subject);  // 이메일 제목
        helper.setText(htmlContent, true);  // true는 HTML 형식을 허용한다는 의미
        helper.setFrom("zoown99@gmail.com");  // 발신자 이메일 주소 (설정 필요)

        mailSender.send(message);  // 이메일 전송
    }

    public void sendPasswordResetEmail(String to, String resetLink) throws MessagingException {
        String subject = "비밀번호 재설정 요청";

        // 인라인 스타일 적용된 HTML 내용
        String htmlContent = """
        <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; background-color: #f9f9f9; padding: 20px; border-radius: 8px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);">
            <h2 style="font-size: 24px; font-weight: bold; color: #333; margin-bottom: 20px;">비밀번호 재설정 요청</h2>
            <p style="font-size: 16px; color: #555;">안녕하세요,</p>
            <p style="font-size: 16px; color: #555;">비밀번호 재설정을 요청하셨습니다. 아래 버튼을 클릭하여 비밀번호를 재설정하세요:</p>
            <a href="%s" style="display: inline-block; padding: 10px 20px; background-color: #28a745; color: white; text-decoration: none; border-radius: 5px; margin-top: 20px;">비밀번호 재설정</a>
            <p style="font-size: 16px; color: #555;">감사합니다.</p>
            <p style="font-size: 16px; color: #555;">팀 이름</p>
        </div>
    """.formatted(resetLink);  // 동적으로 resetLink 삽입

        // HTML 형식으로 이메일 전송
        sendHtmlMessage(to, subject, htmlContent);
    }
}
