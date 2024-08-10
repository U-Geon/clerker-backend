package conference.clerker.global.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;
    private final RedisTemplate<String, String> redisBlackListTemplate;

    @Value("${jwt.refresh-token.expiration}")
    private Long refreshTokenExpiration;

    public void saveRefreshToken(String key, String value) {
        redisTemplate.opsForValue().set(key, value, refreshTokenExpiration, TimeUnit.HOURS);
    }

    public String getRefreshToken(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void deleteRefreshToken(String key) {
        redisTemplate.delete(key);
    }

    public void setBlackList(String key, String o, Long milliSeconds) {
        redisBlackListTemplate.opsForValue().set(key, o, milliSeconds, TimeUnit.HOURS);
    }

    public Object getBlackList(String key) {
        return redisBlackListTemplate.opsForValue().get(key);
    }

    public Boolean deleteBlackList(String key) {
        return redisBlackListTemplate.delete(key);
    }

    public Boolean hasKeyBlackList(String key) {
        return redisBlackListTemplate.hasKey(key);
    }
}
