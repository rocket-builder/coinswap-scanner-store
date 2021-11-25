package com.anthill.coinswapscannerstore.services;

import com.anthill.coinswapscannerstore.beans.Fork;
import com.anthill.coinswapscannerstore.beans.Token;
import com.anthill.coinswapscannerstore.constants.Global;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ForkUpdateService {
    private final RedisService redis;
    private final String updatesKey = "ForkUpdate";

    public ForkUpdateService(RedisService redis) {
        this.redis = redis;
    }

    public void saveForkHashes(Token token, List<Fork> forks) {
        Map<String, Object> updatesMap =
                Map.of(token.getTitle(), forks.stream().map(Fork::hashCodeString).toArray());

        redis.hSetAll(updatesKey, updatesMap);
        redis.resetExpiration(updatesKey, Global.FORK_TTL);
    }

    public List<String> getTokenForksHashKeys(Token token){
        var hashes = redis.hGet(updatesKey, token.getTitle());

        var forkHashKey = new ArrayList<String>();
        if(hashes != null){
            forkHashKey = (ArrayList<String>) hashes;
        }

        return forkHashKey;
    }
}
