package app.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterResponseDto {
    private String accessToken;
    private String refreshToken;
}
