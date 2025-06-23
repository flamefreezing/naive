package common.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    /**
     * Set value với TTL
     */
    public void set(String key, Object value, Duration ttl) {
        try {
            redisTemplate.opsForValue().set(key, value, ttl);
            log.debug("Set key: {} with TTL: {}", key, ttl);
        } catch (Exception e) {
            log.error("Error setting key: {}", key, e);
            throw new RuntimeException("Redis set operation failed", e);
        }
    }

    /**
     * Set value không TTL
     */
    public void set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            log.debug("Set key: {}", key);
        } catch (Exception e) {
            log.error("Error setting key: {}", key, e);
            throw new RuntimeException("Redis set operation failed", e);
        }
    }

    /**
     * Get value
     */
    public <T> T get(String key, Class<T> clazz) {
        try {
            Object value = redisTemplate.opsForValue().get(key);
            if (value == null) {
                return null;
            }
            return objectMapper.convertValue(value, clazz);
        } catch (Exception e) {
            log.error("Error getting key: {}", key, e);
            return null;
        }
    }

    /**
     * Get string value
     */
    public String getString(String key) {
        try {
            return stringRedisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.error("Error getting string key: {}", key, e);
            return null;
        }
    }

    /**
     * Delete key
     */
    public boolean delete(String key) {
        try {
            Boolean result = redisTemplate.delete(key);
            log.debug("Delete key: {}, result: {}", key, result);
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            log.error("Error deleting key: {}", key, e);
            return false;
        }
    }

    /**
     * Delete multiple keys
     */
    public long delete(Collection<String> keys) {
        try {
            Long result = redisTemplate.delete(keys);
            log.debug("Delete keys: {}, count: {}", keys, result);
            return result != null ? result : 0;
        } catch (Exception e) {
            log.error("Error deleting keys: {}", keys, e);
            return 0;
        }
    }

    /**
     * Check if key exists
     */
    public boolean exists(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            log.error("Error checking key existence: {}", key, e);
            return false;
        }
    }

    /**
     * Set TTL cho key
     */
    public boolean expire(String key, Duration ttl) {
        try {
            Boolean result = redisTemplate.expire(key, ttl);
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            log.error("Error setting TTL for key: {}", key, e);
            return false;
        }
    }

    /**
     * Get TTL của key
     */
    public long getTtl(String key) {
        try {
            return redisTemplate.getExpire(key, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Error getting TTL for key: {}", key, e);
            return -1;
        }
    }

    /**
     * Increment counter
     */
    public long increment(String key) {
        return increment(key, 1);
    }

    public long increment(String key, long delta) {
        try {
            Long result = redisTemplate.opsForValue().increment(key, delta);
            return result != null ? result : 0;
        } catch (Exception e) {
            log.error("Error incrementing key: {}", key, e);
            throw new RuntimeException("Redis increment operation failed", e);
        }
    }

    /**
     * Get keys by pattern
     */
    public Set<String> keys(String pattern) {
        try {
            return redisTemplate.keys(pattern);
        } catch (Exception e) {
            log.error("Error getting keys with pattern: {}", pattern, e);
            return Collections.emptySet();
        }
    }

    /**
     * Pipeline operations
     */
    public List<Object> executePipelined(RedisCallback<Object> action) {
        try {
            return redisTemplate.executePipelined(action);
        } catch (Exception e) {
            log.error("Error executing pipelined operations", e);
            throw new RuntimeException("Redis pipeline operation failed", e);
        }
    }

    /**
     * Lua script execution
     */
    public <T> T executeScript(String script, List<String> keys, List<Object> args, Class<T> resultType) {
        try {
            DefaultRedisScript<T> redisScript = new DefaultRedisScript<>();
            redisScript.setScriptText(script);
            redisScript.setResultType(resultType);
            return redisTemplate.execute(redisScript, keys, args.toArray());
        } catch (Exception e) {
            log.error("Error executing Lua script", e);
            throw new RuntimeException("Redis script execution failed", e);
        }
    }

    // Hash operations
    public void hSet(String key, String field, Object value) {
        try {
            redisTemplate.opsForHash().put(key, field, value);
        } catch (Exception e) {
            log.error("Error setting hash field: {}:{}", key, field, e);
            throw new RuntimeException("Redis hash set operation failed", e);
        }
    }

    public <T> T hGet(String key, String field, Class<T> clazz) {
        try {
            Object value = redisTemplate.opsForHash().get(key, field);
            if (value == null) {
                return null;
            }
            return objectMapper.convertValue(value, clazz);
        } catch (Exception e) {
            log.error("Error getting hash field: {}:{}", key, field, e);
            return null;
        }
    }

    public Map<Object, Object> hGetAll(String key) {
        try {
            return redisTemplate.opsForHash().entries(key);
        } catch (Exception e) {
            log.error("Error getting all hash fields: {}", key, e);
            return Collections.emptyMap();
        }
    }

    // List operations
    public long lPush(String key, Object... values) {
        try {
            Long result = redisTemplate.opsForList().leftPushAll(key, values);
            return result != null ? result : 0;
        } catch (Exception e) {
            log.error("Error pushing to list: {}", key, e);
            throw new RuntimeException("Redis list push operation failed", e);
        }
    }

    public <T> T lPop(String key, Class<T> clazz) {
        try {
            Object value = redisTemplate.opsForList().leftPop(key);
            if (value == null) {
                return null;
            }
            return objectMapper.convertValue(value, clazz);
        } catch (Exception e) {
            log.error("Error popping from list: {}", key, e);
            return null;
        }
    }

    // Set operations
    public long sAdd(String key, Object... values) {
        try {
            Long result = redisTemplate.opsForSet().add(key, values);
            return result != null ? result : 0;
        } catch (Exception e) {
            log.error("Error adding to set: {}", key, e);
            throw new RuntimeException("Redis set add operation failed", e);
        }
    }

    public boolean sIsMember(String key, Object value) {
        try {
            Boolean result = redisTemplate.opsForSet().isMember(key, value);
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            log.error("Error checking set membership: {}", key, e);
            return false;
        }
    }

    // Sorted Set operations
    public boolean zAdd(String key, Object value, double score) {
        try {
            Boolean result = redisTemplate.opsForZSet().add(key, value, score);
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            log.error("Error adding to sorted set: {}", key, e);
            throw new RuntimeException("Redis sorted set add operation failed", e);
        }
    }

    public Set<Object> zRange(String key, long start, long end) {
        try {
            return redisTemplate.opsForZSet().range(key, start, end);
        } catch (Exception e) {
            log.error("Error getting sorted set range: {}", key, e);
            return Collections.emptySet();
        }
    }

    /**
     * Health check
     */
    public boolean isHealthy() {
        try {
            String pong = stringRedisTemplate.getConnectionFactory()
                    .getConnection().ping();
            return "PONG".equals(pong);
        } catch (Exception e) {
            log.error("Redis health check failed", e);
            return false;
        }
    }
}
