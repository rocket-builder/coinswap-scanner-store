package com.anthill.coinswapscannerstore.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Configuration
public class ExecutorServiceConfiguration {

    @Bean
    public ScheduledExecutorService scheduledExecutorService(){
        return Executors.newSingleThreadScheduledExecutor();
    }
}
