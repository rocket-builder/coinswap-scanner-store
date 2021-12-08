package com.anthill.coinswapscannerstore.services;

import com.anthill.coinswapscannerstore.beans.Fork;
import com.anthill.coinswapscannerstore.constants.Global;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ForkService {
    private final RedisService redis;
    private final String forkKey = Fork.class.getSimpleName();

    public ForkService(RedisService redis) {
        this.redis = redis;
    }
    public void save(Map<String, Object> forks){
        redis.hSetAll(forkKey, forks);
        redis.resetExpiration(forkKey, Global.FORK_TTL);
    }

    public Map<Object, Object> findAll(){
        return redis.hGetAll(forkKey);
    }

    public void deleteAll(){
        redis.flushAll();
    }
}
