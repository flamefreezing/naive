package app.config;

import common.util.JwtUtil;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

@Configurable
@ConfigurationProperties(prefix = "app.jwt")
public class AuthConfig {

    private String secret;
    private long expiration;

    @Bean
    public JwtUtil jwtUtil() {
        return new JwtUtil(secret, expiration);
    }
}
