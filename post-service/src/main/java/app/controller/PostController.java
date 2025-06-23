package app.controller;

import app.entity.Post;
import common.annotation.CurrentUserId;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/posts")
public class PostController {

    @GetMapping
    public String getPosts(@CurrentUserId UUID userId) {
        System.out.println(userId.toString());
        return "Testing...";
    }
}
