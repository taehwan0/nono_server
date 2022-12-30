package com.nono.deluxe.application.client;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Optional;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

@Slf4j
@EnableAsync
@RequiredArgsConstructor
@Component
public class MailClient {

    private final JavaMailSender javaMailSender;

    @Async("mailExecutor")
    public void postExcelFile(String email, String subject, Optional<File> file)
        throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "UTF-8");

        messageHelper.setTo(email);
        messageHelper.setSubject(subject);

        if (file.isPresent()) {
            messageHelper.setText("success", true);
            messageHelper.addAttachment(MimeUtility.encodeText("excel.xlsx"), file.get());
        } else {
            messageHelper.setText("fail", true);
        }

        javaMailSender.send(message);
    }

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
