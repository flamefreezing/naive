package app.controller;

import app.dto.LoginRequestDto;
import app.dto.LoginResponseDto;
import app.dto.RegisterRequestDto;
import app.service.AuthService;
import common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ApiResponse<String> register(@Valid @RequestBody RegisterRequestDto registerRequestDto) {
        return ApiResponse.success(authService.register(registerRequestDto));
    }

    @GetMapping("/verify")
    public ApiResponse<String> verify(@RequestParam String token) {
        return ApiResponse.success(authService.verify(token));
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        return ApiResponse.success(authService.login(loginRequestDto));
    }
}
