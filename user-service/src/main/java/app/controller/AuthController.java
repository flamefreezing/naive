package app.controller;

import app.dto.LoginRequestDto;
import app.dto.LoginResponseDto;
import app.dto.RegisterRequestDto;
import app.dto.RegisterResponseDto;
import app.service.AuthService;
import common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/auth/register")
    public ApiResponse<String> register(@Valid @RequestBody RegisterRequestDto registerRequestDto) {
        return ApiResponse.success(authService.register(registerRequestDto));
    }

    @PostMapping("/auth/login")
    public ApiResponse<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequestDto) {
        return ApiResponse.success(authService.login(loginRequestDto));
    }
}
