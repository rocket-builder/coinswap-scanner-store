package com.anthill.coinswapscannerstore.services;

import com.anthill.coinswapscannerstore.beans.Fork;
import com.anthill.coinswapscannerstore.beans.ForkList;
import com.anthill.coinswapscannerstore.beans.Token;
import com.microsoft.signalr.HubConnection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
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
                var forks = forksList.stream()
                        .peek(fork -> {
                            fork.setId(fork.hashCodeString());
                            log.info("Fork: " + fork);
                        })
                        .collect(Collectors.toList());

                Token token = forks.get(0).getToken();
                var existsForksHashes = forkUpdateService.getTokenForksHashKeys(token);

                if(existsForksHashes.size() > 0){
                    var forkUpdates = forks.stream()
                            .filter(fork -> existsForksHashes.contains(fork.getId()))
                            .collect(Collectors.toList());

                    forkUpdates.forEach(
                            f -> log.info("Update fork: " + f));

                    simpMessagingTemplate.convertAndSend("/forks/update/", forkUpdates);
                } else {
                    simpMessagingTemplate.convertAndSend("/forks/new/", forks);
                }

                forkUpdateService.saveForkHashes(token, forksList);
                forkService.save(forksList);
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
