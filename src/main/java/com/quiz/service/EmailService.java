package com.quiz.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(String to, String token) {
        // Localhost əvəzinə canlı linki yaz
        String link = "https://quiz-app-djn3.onrender.com/verify?token=" + token;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Email təsdiqi");
        message.setText("Hesabınızı təsdiqləmək üçün klik edin:\n" + link);
        mailSender.send(message);
    }
}
