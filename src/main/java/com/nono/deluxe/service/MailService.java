package com.nono.deluxe.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

@Slf4j
@EnableAsync
@RequiredArgsConstructor
@Service
public class MailService {

    private final JavaMailSender javaMailSender;

    @Async("mailExecutor")
    public void postJoinCheckMail(String email, String verifyCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("노노 Deluxe 회원가입 확인 인증번호");
        message.setText(verifyCode);

        javaMailSender.send(message);

        log.info("Mail Posted: joinCheckMail to {}", email);
    }

    @Async("mailExecutor")
    public void postReissueCheckMail(String email, String verifyCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("노노 Deluxe 비밀번호 재설정 확인 인증번호");
        message.setText(verifyCode);

        javaMailSender.send(message);

        log.info("Mail Posted: reissueCheckMail to {}", email);
    }

    @Async("mailExecutor")
    public void postReissuePasswordMail(String email, String newPassword) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("노노 Deluxe 재설정된 비밀번호");
        message.setText(newPassword);

        javaMailSender.send(message);

        log.info("Mail Posted: reissuePasswordMail to {}", email);
    }
}
