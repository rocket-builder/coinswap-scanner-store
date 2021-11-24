package com.anthill.coinswapscannerstore.services;

import com.anthill.coinswapscannerstore.beans.Fork;
import com.anthill.coinswapscannerstore.beans.ForkList;
import com.anthill.coinswapscannerstore.constants.Global;
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
    private final ForkUpdateService forkUpdateService;

    private final String forkKey = Fork.class.getSimpleName();

    public ForkService(HubConnection hubConnection, ScheduledExecutorService executorService,
                       RedisService redis, ForkUpdateService forkUpdateService) {
        this.hubConnection = hubConnection;
        this.executorService = executorService;
        this.redis = redis;
        this.forkUpdateService = forkUpdateService;
    }

    public void save(List<Fork> forks){
        Map<String, Object> forksMap = forks.stream()
                .collect(Collectors.toMap(
                        fork -> String.valueOf(fork.hashCode()), fork -> fork));

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

    public void init(){
        hubConnection.on("Send", (forks) -> {
            var forksList = forks.getItems();

            if (forksList.size() > 0){
                forksList.forEach(fork ->
                        log.info("Fork: " + fork));

                String tokenTitle = forksList.get(0).getToken().getTitle();
                var existsForksHashes = forkUpdateService.getTokenForksHashKeys(tokenTitle);
                if(existsForksHashes.size() > 0){
                    //TODO update exist forks on keys from existsForksHashes list
                    //redis.hSet(forkKey, existsForksHash, forkUpdate);

                    //TODO SEND UPDATED FORKS TO CLIENT UPDATE SOCKET
                } else {
                    save(forksList);

                    //TODO SEND NEW FORKS TO CLIENT SOCKET
                }
            }
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
}
