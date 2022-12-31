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

    private static final String JOIN_CHECK_MAIL_SUBJECT = "NONO DELUXE 회원가입 인증 메일입니다.";
    private static final String REISSUE_CHECK_MAIL_SUBJECT = "NONO DELUXE 비밀번호 재설정 인증 메일입니다.";
    private static final String REISSUE_PASSWORD_MAIL_SUBJECT = "NONO DELUXE 재설정된 비밀번호 메일입니다.";
    private static final String UPDATE_PASSWORD_MAIL_SUBJECT = "NONO DELUXE 비밀번호가 변경 되었습니다.";
    private static final String UPDATE_PASSWORD_MAIL_CONTENT = "비밀번호 변경을 요청하지 않았다면 비밀번호를 재발급하거나 관리자에 문의하세요.";

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
        postSimpleMail(email, JOIN_CHECK_MAIL_SUBJECT, verifyCode);
    }

    @Async("mailExecutor")
    public void postReissueCheckMail(String email, String verifyCode) {
        postSimpleMail(email, REISSUE_CHECK_MAIL_SUBJECT, verifyCode);
    }

    @Async("mailExecutor")
    public void postReissuePasswordMail(String email, String newPassword) {
        postSimpleMail(email, REISSUE_PASSWORD_MAIL_SUBJECT, newPassword);
    }

    @Async("mailExecutor")
    public void postUpdatePasswordMail(String email) {
        postSimpleMail(email, UPDATE_PASSWORD_MAIL_SUBJECT, UPDATE_PASSWORD_MAIL_CONTENT);
    }

    private void postSimpleMail(String email, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject(subject);
        message.setText(content);

        javaMailSender.send(message);

        log.info("Mail Posted To {}: {}", email, subject);
    }
}
