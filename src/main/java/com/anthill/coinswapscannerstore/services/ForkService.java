package com.anthill.coinswapscannerstore.services;

import com.anthill.coinswapscannerstore.beans.Fork;
import com.anthill.coinswapscannerstore.constants.Global;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ForkService {
    private final RedisService redis;
    private final String forkKey = Fork.class.getSimpleName();

    public ForkService(RedisService redis) {
        this.redis = redis;
    }

    public void save(List<Fork> forks){
        Map<String, Object> forksMap = forks.stream()
                .collect(Collectors.toMap(
                        Fork::hashCodeString, fork -> fork));

        redis.hSetAll(forkKey, forksMap);
        redis.resetExpiration(forkKey, Global.FORK_TTL);
    }

    public Iterable<Fork> findAll(){
        return redis.hGetAll(forkKey)
                .values()
                .stream()
                .filter(o -> o instanceof Fork)
                .map(f -> (Fork) f)
                .collect(Collectors.toList());
    }

    public void deleteAll(){
        redis.flushAll();
    }
}
