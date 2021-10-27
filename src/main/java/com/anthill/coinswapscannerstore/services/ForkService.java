package com.anthill.coinswapscannerstore.services;

import com.anthill.coinswapscannerstore.beans.Fork;
import com.anthill.coinswapscannerstore.beans.ForkList;
import com.microsoft.signalr.HubConnection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ForkService {

    private final HubConnection hubConnection;
    private final ScheduledExecutorService executorService;
    private final RedisService redis;

    private final long forkTtl = 60 * 30; //30m
    private final String forkKey = Fork.class.getSimpleName();

    public ForkService(HubConnection hubConnection, ScheduledExecutorService executorService,
                       RedisService redis) {
        this.hubConnection = hubConnection;
        this.executorService = executorService;
        this.redis = redis;
    }

    public void save(Fork fork){
        redis.hSet(forkKey, UUID.randomUUID().toString(), fork);
        resetExpiration();
    }
    public void save(List<Fork> forks){
        Map<String, Object> forksMap = forks.stream()
                .collect(Collectors.toMap(
                        fork -> UUID.randomUUID().toString(), fork -> fork));

        redis.hSetAll(forkKey, forksMap);
        resetExpiration();
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

    public void init(){
        hubConnection.on("Send", (forks) -> {
            var forksList = forks.getItems();

            forksList.forEach(fork ->
                    log.info("Fork: " + fork));

            save(forksList);
        }, ForkList.class);

        hubConnection.onClosed((ex) -> {
            if(ex != null){
                ex.printStackTrace();
            }
            executorService.scheduleWithFixedDelay(
                    hubConnection::start,0,5, TimeUnit.SECONDS);
        });

        hubConnection.start();
    }

    private void resetExpiration(){
        if(redis.getExpire(forkKey) <= 0){
            redis.expire(forkKey, forkTtl);
        }
    }
}
