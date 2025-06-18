package application.consumer;

import application.constant.EmailTemplate;
import application.service.MailService;
import common.event.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class UserServiceListener {

    private final MailService mailService;

    @KafkaListener(topics = "user.registered", groupId = "notification.email.user-registered")
    public void handleUserRegistered(UserRegisteredEvent event) {
        System.out.println("Received user registered event: " + event);
        Map<String, Object> variables = new HashMap<>();
        variables.put("username", event.getUserName());
        variables.put("email", event.getEmail());
        variables.put("token", event.getToken());
        mailService.sendHtmlEmail(event.getEmail(), "Verify email to activate your account", variables, EmailTemplate.REGISTRATION_EMAIL);
    }
}
