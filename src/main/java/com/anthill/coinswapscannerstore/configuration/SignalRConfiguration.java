package com.anthill.coinswapscannerstore.configuration;

import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;
import com.microsoft.signalr.TransportEnum;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SignalRConfiguration {

    @Value("${signalr.url}")
    private String signalRUrl;

    @Bean
    public HubConnection hubConnection(){
        return HubConnectionBuilder.create(signalRUrl)
                .shouldSkipNegotiate(true)
                .withTransport(TransportEnum.WEBSOCKETS)
                .build();
    }
}
