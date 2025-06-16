package app.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    @GetMapping
    public String test() {
        return "Get users Testing...!!";
    }

    @GetMapping("/profile")
    public String retest() {
        return "Get profile Testing...!!";
    }
}
