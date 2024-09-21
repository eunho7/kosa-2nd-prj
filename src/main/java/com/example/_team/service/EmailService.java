package com.example._team.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Random;

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
        helper.setFrom("zoown99@gmail.com");  // 발신자 이메일 주소

        mailSender.send(message);  // 이메일 전송
    }

    // 인증번호 이메일 전송
    public void sendVerificationEmail(String to, String verificationCode) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");

        String htmlContent = """
        <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border-radius: 10px; background-color: #f9f9f9; box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);">
            <h2 style="color: #3498db; text-align: center;">회원가입 인증번호</h2>
            <p style="font-size: 16px; color: #555; text-align: center;">
                안녕하세요,<br>아래 인증번호를 회원가입 페이지에 입력해주세요.
            </p>
            <div style="text-align: center; margin: 20px 0;">
                <span style="display: inline-block; padding: 10px 20px; font-size: 24px; font-weight: bold; color: #ffffff; background-color: #3498db; border-radius: 5px;">
                    %s
                </span>
            </div>
            <p style="font-size: 14px; color: #777; text-align: center;">
                인증번호는 10분간 유효합니다. 감사합니다.<br><br>
                <strong>촬칵! Team</strong> 드림
            </p>
            <hr style="border: none; border-top: 1px solid #ddd;">
            <p style="font-size: 12px; color: #999; text-align: center;">
                이 메일은 발신 전용입니다. 문의사항은 지원센터로 연락해주세요.
            </p>
        </div>
    """.formatted(verificationCode);

        helper.setTo(to);
        helper.setSubject("회원가입 인증번호");
        helper.setText(htmlContent, true); // HTML 형식의 인증번호 이메일
        mailSender.send(message);
    }

}
