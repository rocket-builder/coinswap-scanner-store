package com.anthill.coinswapscannerstore.services;

import com.anthill.coinswapscannerstore.beans.Fork;
import com.anthill.coinswapscannerstore.constants.Global;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ForkUpdateService {

    private final String updatesKey = "ForkUpdate";

    private final RedisService redis;

    public ForkUpdateService(RedisService redis) {
        this.redis = redis;
    }

    public void save(List<Fork> forks) {
        var fork = forks.stream().findFirst();
        if(fork.isPresent()){
            String tokenTitle = fork.get().getToken().getTitle();

            Map<String, Object> updatesMap =
                    Map.of(tokenTitle, forks.stream().map(Fork::hashCode).toArray());

            redis.hSetAll(updatesKey, updatesMap);
            redis.resetExpiration(updatesKey, Global.FORK_TTL);
        }
    }

    public List<String> getTokenForksHashKeys(String tokenTitle){
        var res = redis.hGet(updatesKey, tokenTitle);

        var forkHashKey = new ArrayList<String>();
        if(res != null){
            forkHashKey = (ArrayList<String>) res;
        }

        return forkHashKey;
    }
}
