package common.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.SocketOptions;
import io.lettuce.core.TimeoutOptions;
import lombok.RequiredArgsConstructor;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
@ConditionalOnProperty(name = "redis.enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
public class RedisConfig {
    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    @Value("${spring.data.redis.password}")
    private String password;

    @Value("${spring.data.redis.timeout}")
    private long timeout;

    // Connection Pool Settings
    @Value("${spring.data.redis.lettuce.pool.max-active}")
    private int maxActive;

    @Value("${spring.data.redis.lettuce.pool.max-idle}")
    private int maxIdle;

    @Value("${spring.data.redis.lettuce.pool.min-idle}")
    private int minIdle;

    @Value("${spring.data.redis.lettuce.pool.max-wait}")
    private long maxWait;

    private final ObjectMapper objectMapper;

    /**
     * Redis Connection Factory
     */
    @Bean
    @Primary
    @ConditionalOnMissingBean
    public LettuceConnectionFactory redisConnectionFactory() {
        // Standalone configuration
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(host);
        config.setPort(port);
        if (!password.isEmpty()) {
            config.setPassword(RedisPassword.of(password));
        }

        // Client configuration với pool
        LettuceClientConfiguration clientConfig = createClientConfiguration();

        return new LettuceConnectionFactory(config, clientConfig);
    }

    /**
     * RedisTemplate với Jackson serializer - cho Object
     */
    @Bean
    @Primary
    @ConditionalOnMissingBean(name = "redisTemplate")
    public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Jackson serializer
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer =
                new Jackson2JsonRedisSerializer<>(objectMapper, Object.class);


        // String serializer
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

        // Key serialization
        template.setKeySerializer(stringRedisSerializer);
        template.setHashKeySerializer(stringRedisSerializer);

        // Value serialization
        template.setValueSerializer(jackson2JsonRedisSerializer);
        template.setHashValueSerializer(jackson2JsonRedisSerializer);

        template.afterPropertiesSet();
        return template;
    }

    /**
     * StringRedisTemplate - cho String operations
     */
    @Bean
    @ConditionalOnMissingBean
    public StringRedisTemplate stringRedisTemplate(LettuceConnectionFactory connectionFactory) {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(connectionFactory);
        return template;
    }

    /**
     * ObjectMapper bean cho JSON operations
     */
    private LettuceClientConfiguration createClientConfiguration() {
        // Pool configuration
        GenericObjectPoolConfig<?> poolConfig = new GenericObjectPoolConfig<>();
        poolConfig.setMaxTotal(maxActive);
        poolConfig.setMaxIdle(maxIdle);
        poolConfig.setMinIdle(minIdle);
        poolConfig.setMaxWaitMillis(maxWait);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestWhileIdle(true);
        poolConfig.setTimeBetweenEvictionRunsMillis(30000);

        // Client options
        ClientOptions clientOptions = ClientOptions.builder()
                .timeoutOptions(TimeoutOptions.enabled(Duration.ofMillis(timeout)))
                .socketOptions(SocketOptions.builder()
                        .connectTimeout(Duration.ofMillis(timeout))
                        .keepAlive(true)
                        .build())
                .build();

        return LettucePoolingClientConfiguration.builder()
                .poolConfig(poolConfig)
                .clientOptions(clientOptions)
                .commandTimeout(Duration.ofMillis(timeout))
                .build();
    }
}
