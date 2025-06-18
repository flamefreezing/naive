package app.service;

import app.dto.LoginRequestDto;
import app.dto.LoginResponseDto;
import app.dto.RegisterRequestDto;
import app.dto.RegisterResponseDto;

public interface AuthService {
    String register(RegisterRequestDto registerRequestDto);
    LoginResponseDto login(LoginRequestDto loginRequestDto);
}
