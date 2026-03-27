package in.manikanta.moneymanager.service;

import jdk.jfr.Experimental;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    @Value("${app.mail.from:}")
    private String fromEmail;

    private final JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String body){
        try{
            if (fromEmail == null || fromEmail.isBlank()) {
                log.warn("Skipping email send because app.mail.from is not configured");
                return;
            }
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setText(body);
            message.setSubject(subject);
            mailSender.send(message);
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }
}
