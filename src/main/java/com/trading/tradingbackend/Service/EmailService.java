package com.trading.tradingbackend.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
public class EmailService {

    private final JavaMailSender javaMailSender;

    // Constructor injection
    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendVerificationEmail(String email, String otp) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, StandardCharsets.UTF_8.name());
        String subject = "Verify OTP";
        String text = "Your Verification Code is: " + otp;
        mimeMessageHelper.setFrom("rchitsoni6@gmail.com");
        mimeMessageHelper.setSubject(subject);
        mimeMessageHelper.setText(text);
        mimeMessageHelper.setTo(email);
        try {
            javaMailSender.send(message);
        } catch (Exception e) {
            throw new MailSendException(e.getMessage());
        }
    }

    public void sendActivationEmail(String email, String code) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, StandardCharsets.UTF_8.name());
        String subject = "Account Activation Code";
        String text = "Your Activation Code is: " + code;
        mimeMessageHelper.setFrom("rchitsoni6@gmail.com");
        mimeMessageHelper.setSubject(subject);
        mimeMessageHelper.setText(text);
        mimeMessageHelper.setTo(email);
        try {
            javaMailSender.send(message);
        } catch (Exception e) {
            throw new MailSendException(e.getMessage());
        }
    }

    public void sendPasswordRecoveryEmail(String email, String otp) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, StandardCharsets.UTF_8.name());
        String subject = "Password Reset OTP";
        String text = "Your Verification Code is: " + otp;
        mimeMessageHelper.setFrom("rchitsoni6@gmail.com");
        mimeMessageHelper.setSubject(subject);
        mimeMessageHelper.setText(text);
        mimeMessageHelper.setTo(email);
        try {
            javaMailSender.send(message);
        } catch (Exception e) {
            throw new MailSendException(e.getMessage());
        }
    }

}
