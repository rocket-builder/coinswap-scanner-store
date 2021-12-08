package com.anthill.coinswapscannerstore.services;

import com.anthill.coinswapscannerstore.beans.Fork;
import com.anthill.coinswapscannerstore.beans.ForkList;
import com.anthill.coinswapscannerstore.beans.Token;
import com.microsoft.signalr.HubConnection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ForkWebsocketService {
    private final HubConnection hubConnection;
    private final ScheduledExecutorService executorService;

    private final ForkUpdateService forkUpdateService;
    private final ForkService forkService;

    private final SimpMessagingTemplate simpMessagingTemplate;

    public ForkWebsocketService(HubConnection hubConnection, ScheduledExecutorService executorService,
                                ForkUpdateService forkUpdateService, ForkService forkService,
                                SimpMessagingTemplate simpMessagingTemplate) {
        this.hubConnection = hubConnection;
        this.executorService = executorService;
        this.forkUpdateService = forkUpdateService;
        this.forkService = forkService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    public void init(){
        hubConnection.on("Send", (input) -> {
            var forksList = input.getItems();

            if (forksList.size() > 0){
                Token token = forksList.get(0).getToken();

                Map<String, Object> forks = forksList.stream()
                        .collect(Collectors.toMap(Fork::hashCodeString, Function.identity()));

                var existsForksHashes = forkUpdateService.getTokenForksHashKeys(token);

                if(existsForksHashes.size() > 0){
                    var forkUpdates = forks.entrySet()
                            .stream()
                            .filter(fork -> existsForksHashes.contains(fork.getKey()))
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                    log.info("Received " + forkUpdates.size() + " updates");

                    simpMessagingTemplate.convertAndSend("/forks/update/", forkUpdates);
                } else {
                    log.info("Received " + forksList.size() + " forks");

                    simpMessagingTemplate.convertAndSend("/forks/new/", forks);
                }

                forkUpdateService.saveForkHashes(token, forks);
                forkService.save(forks);
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
