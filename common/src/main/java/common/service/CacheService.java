package common.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class CacheService {
    private final RedisService redisService;

    @Value("${cache.redis.default-ttl:3600}")
    private long defaultTtl;

    @Value("${cache.redis.key-prefix:myapp}")
    private String keyPrefix;

    /**
     * Build cache key with prefix
     */
    public String buildKey(String... parts) {
        return keyPrefix + ":" + String.join(":", parts);
    }

    /**
     * Cache with default TTL
     */
    public void cache(String key, Object value) {
        cache(key, value, Duration.ofSeconds(defaultTtl));
    }

    /**
     * Cache with custom TTL
     */
    public void cache(String key, Object value, Duration ttl) {
        String cacheKey = buildKey(key);
        redisService.set(cacheKey, value, ttl);
    }

    /**
     * Get from cache
     */
    public <T> T getFromCache(String key, Class<T> clazz) {
        String cacheKey = buildKey(key);
        return redisService.get(cacheKey, clazz);
    }

    /**
     * Cache or get pattern
     */
    public <T> T cacheOrGet(String key, Class<T> clazz, java.util.function.Supplier<T> supplier) {
        return cacheOrGet(key, clazz, supplier, Duration.ofSeconds(defaultTtl));
    }

    public <T> T cacheOrGet(String key, Class<T> clazz, java.util.function.Supplier<T> supplier, Duration ttl) {
        T cached = getFromCache(key, clazz);
        if (cached != null) {
            return cached;
        }

        T value = supplier.get();
        if (value != null) {
            cache(key, value, ttl);
        }
        return value;
    }

    /**
     * Invalidate cache
     */
    public void invalidate(String key) {
        String cacheKey = buildKey(key);
        redisService.delete(cacheKey);
    }

    /**
     * Invalidate cache by pattern
     */
    public void invalidateByPattern(String pattern) {
        String searchPattern = buildKey(pattern);
        Set<String> keys = redisService.keys(searchPattern);
        if (!keys.isEmpty()) {
            redisService.delete(keys);
        }
    }
}
