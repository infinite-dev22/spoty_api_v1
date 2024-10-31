package io.nomard.spoty_api_v1.services.interfaces;

import jakarta.mail.MessagingException;

public interface EmailService {
    void sendSimpleMessage(String from, String to, String subject, String text) throws MessagingException;

    void sendMessageWithAttachment(String from, String to, String subject, String text, String pathToAttachment) throws MessagingException;
}
