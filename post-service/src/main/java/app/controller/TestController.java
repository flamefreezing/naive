package app.controller;

import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    public String test() {
        return "Post Service Testing...";
    }
}
