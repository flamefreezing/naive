package application.controller;

import common.event.UserRegisteredEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CounterController {

    private int counter;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @GetMapping("/counter")
    public void add() {
        kafkaTemplate.send("counter", "OK", "OK");
    }

    @KafkaListener(topics = "counter", groupId = "counterId")
    public void handle(Object event) {
        counter++;
        System.out.println(counter);
    }
}
