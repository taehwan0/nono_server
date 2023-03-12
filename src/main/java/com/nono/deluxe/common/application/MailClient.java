package com.nono.deluxe.common.application;

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
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Slf4j
@EnableAsync
@RequiredArgsConstructor
@Component
public class MailClient {

    private static final String JOIN_CHECK_MAIL_SUBJECT = "[노노유통] 회원가입 인증 메일입니다.";
    private static final String REISSUE_CHECK_MAIL_SUBJECT = "[노노유통] 비밀번호 초기화 인증 메일입니다.";
    private static final String REISSUE_PASSWORD_MAIL_SUBJECT = "[노노유통] 비밀번호가 변경되었습니다.";
    private static final String UPDATE_PASSWORD_MAIL_SUBJECT = "[노노유통] 비밀번호가 변경되었습니다.";
    private static final String UPDATE_PASSWORD_MAIL_CONTENT = "비밀번호 변경을 요청하지 않았다면, 비밀번호를 재 설정 하거나 관리자에게 문의하세요.";
    private static final String EXCEPTION_MAIL_SUBJECT = "[노노유통] [알 수 없는 오류]";
    private static final String EXCEPTION_MAIL_CONTENT = "노노유통 서버에서 오류가 발생했습니다.관리자에게 문의하세요.";
    private static final String MONTHLY_DOCUMENT_MAIL_SUBJECT_SUCCESS_FORMAT = "[노노유통] %d년 %d월 노노유통 입/출고 현황";
    private static final String MONTHLY_DOCUMENT_MAIL_CONTENT_SUCCESS_FORMAT = "%d년 %d월 노노유통 입/출고 현황을 정리한 문서입니다.첨부파일을 확인 해 주세요.";
    private static final String MONTHLY_DOCUMENT_MAIL_SUBJECT_FAIL = "[노노유통] 월별 문서 생성 실패";
    private static final String MONTHLY_DOCUMENT_MAIL_CONTENT_FAIL_FORMAT = "%d년 %d월 입/출고 현황 문서 생성 과정에서 오류가 발생했습니다. 잠시 후 다시 시도해주세요. 만약 오류가 계속 될 경우, 관리자에게 문의하세요.";

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Async("mailExecutor")
    public void postMonthlyDocumentMail(String email, int year, int month, Optional<File> file)
        throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "UTF-8");

        messageHelper.setTo(email);

        if (file.isPresent()) {
            messageHelper.setSubject(String.format(MONTHLY_DOCUMENT_MAIL_SUBJECT_SUCCESS_FORMAT, year, month));
            messageHelper.setText(
                getTextWithTemplate(
                    String.format(MONTHLY_DOCUMENT_MAIL_CONTENT_SUCCESS_FORMAT, year, month),
                    false),
                true);
            messageHelper.addAttachment(MimeUtility.encodeText("excel.xlsx"), file.get());
        } else {
            messageHelper.setSubject(MONTHLY_DOCUMENT_MAIL_SUBJECT_FAIL);
            messageHelper.setText(
                getTextWithTemplate(
                    String.format(MONTHLY_DOCUMENT_MAIL_CONTENT_FAIL_FORMAT, year, month),
                    false),
                true);
        }
        javaMailSender.send(message);
    }

    @Async("mailExecutor")
    public void postJoinCheckMail(String email, String verifyCode) {
        postMail(email, JOIN_CHECK_MAIL_SUBJECT, verifyCode, true);
    }

    @Async("mailExecutor")
    public void postReissueCheckMail(String email, String verifyCode) {
        postMail(email, REISSUE_CHECK_MAIL_SUBJECT, verifyCode, true);
    }

    @Async("mailExecutor")
    public void postReissuePasswordMail(String email, String newPassword) {
        postMail(email, REISSUE_PASSWORD_MAIL_SUBJECT, newPassword, false);
    }

    @Async("mailExecutor")
    public void postUpdatePasswordMail(String email) {
        postMail(email, UPDATE_PASSWORD_MAIL_SUBJECT, UPDATE_PASSWORD_MAIL_CONTENT, false);
    }

    private void postMail(String email, String subject, String content, boolean isCode) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message);

            messageHelper.setTo(email);
            messageHelper.setSubject(subject);
            messageHelper.setText(getTextWithTemplate(content, isCode), true);

            javaMailSender.send(message);

            log.info("Mail Posted To {}: {}", email, subject);
        } catch (Exception e) {
            postExceptionMessage(email);
        }
    }

    private String getTextWithTemplate(String content, boolean isCode) {
        if (isCode) {
            return byCodeTemplate(content);
        }
        return byMessageTemplate(content);
    }

    private String byCodeTemplate(String content) {
        Context context = new Context();
        context.setVariable("subject", "인증 번호");
        context.setVariable("content", content);

        return templateEngine.process("code-form", context);
    }


    private String byMessageTemplate(String content) {
        Context context = new Context();
        context.setVariable("content", content);

        return templateEngine.process("message-form", context);
    }

    private void postExceptionMessage(String email) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject(EXCEPTION_MAIL_SUBJECT);
        message.setText(EXCEPTION_MAIL_CONTENT);

        javaMailSender.send(message);
    }
}
