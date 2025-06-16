package app.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @GetMapping("/register")
    public String test() {
        return "Register Testing...!!";
    }

    @GetMapping("/login")
    public String retest() {
        return "Login Testing...!!";
    }
}
