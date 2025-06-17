package app.service.impl;

import app.dto.LoginRequestDto;
import app.dto.LoginResponseDto;
import app.dto.RegisterRequestDto;
import app.dto.RegisterResponseDto;
import app.entity.CustomUserDetails;
import app.entity.User;
import app.repository.UserRepository;
import app.service.AuthService;
import app.util.JwtUtil;
import common.event.UserRegisteredEvent;
import common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public RegisterResponseDto register(RegisterRequestDto registerRequestDto) {
        boolean isExistUsername = userRepository.findByUsername(registerRequestDto.getUsername()).isPresent();
        if(isExistUsername){
          throw new BusinessException("Username already existed", HttpStatus.BAD_REQUEST);
        }

        boolean isExistEmail = userRepository.findByEmail(registerRequestDto.getEmail()).isPresent();
        if(isExistEmail){
            throw new BusinessException("Email already existed", HttpStatus.BAD_REQUEST);
        }

        User user = User.builder()
                .email(registerRequestDto.getEmail())
                .username(registerRequestDto.getUsername())
                .password(passwordEncoder.encode(registerRequestDto.getPassword()))
                .build();

        UserDetails userDetails = new CustomUserDetails(user);
        String accessToken = jwtUtil.generateAccessToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);

        UserRegisteredEvent event = UserRegisteredEvent.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .userName(user.getUsername())
                .timestamp(LocalDateTime.now())
                .build();

        kafkaTemplate.send("user.registered", event);

        return RegisterResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        User user = userRepository.findByUsername(loginRequestDto.getUsername()).orElseThrow(
                () -> new BusinessException("Username not found", HttpStatus.BAD_REQUEST)
        );

        if(!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())){
            throw new BusinessException("Wrong password", HttpStatus.BAD_REQUEST);
        }

        UserDetails userDetails = new CustomUserDetails(user);
        String accessToken = jwtUtil.generateAccessToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);

        return LoginResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
