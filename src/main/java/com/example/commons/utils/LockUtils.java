package com.example.commons.utils;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * @auther hao.wang
 *      分布式锁
 * @date 2021/5/11
 */
public class LockUtils {
    public static LockEntity lock(String lockKey, Integer seconds, RedisTemplate redisTemplate){
        boolean hasLock = false;
        int count = 3;
        for (int i = 0; i < count; i++) {
            hasLock = redisTemplate.opsForValue().setIfAbsent(lockKey,"lock",seconds, TimeUnit.SECONDS);
            if(hasLock){
                break;
            }
            //  停止50ms
            LockSupport.parkNanos(50L * 1000 * 1000);
        }

        LockEntity entity = new LockEntity();
        entity.setLockKey(lockKey);
        entity.setLockTime(LocalDateTime.now());
        entity.setLockSeconds(seconds);
        entity.setHasLock(hasLock);

        return entity;
    }

    public static void release(LockEntity entity , RedisTemplate redisTemplate){
        if(entity == null || !entity.hasLock){
            return ;
        }

        if(Duration.between(entity.lockTime,LocalDateTime.now()).getSeconds() - entity.getLockSeconds() > 1){
            //  自然失效 1s的容差
            return ;
        }

        redisTemplate.delete(entity.lockKey);
    }

    @Setter
    @Getter
    public static class LockEntity {
        private String lockKey;
        private LocalDateTime lockTime;
        private Integer lockSeconds;
        private boolean hasLock = false;
    }
}
