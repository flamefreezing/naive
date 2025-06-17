package application.service;

import jakarta.mail.MessagingException;

import java.util.Map;

public interface MailService {

    void sendHtmlEmail(String to, String subject, Map<String, Object> variables, String mailTemplate);
}
