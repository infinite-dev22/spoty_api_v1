package io.nomard.spoty_api_v1.services.implementations;

import io.nomard.spoty_api_v1.services.interfaces.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class EmailServiceImpl implements EmailService {
    @Autowired
    private JavaMailSender emailSender;

    @Override
    public void sendSimpleMessage(String from, String to, String subject, String body) throws MessagingException {
        var mimeMessage = emailSender.createMimeMessage();
        var message = new MimeMessageHelper(mimeMessage);

        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body, true);
        emailSender.send(message.getMimeMessage());
    }

    @Override
    public void sendMessageWithAttachment(String from, String to, String subject, String body, String pathToAttachment) throws MessagingException {
        var message = emailSender.createMimeMessage();
        var helper = new MimeMessageHelper(message, true);
        var file = new FileSystemResource(new File(pathToAttachment));

        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body);
        helper.addAttachment("Attachment", file);

        emailSender.send(message);
    }
}
