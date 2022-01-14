package com.anthill.coinswapscannerstore.services;

import com.anthill.coinswapscannerstore.beans.Fork;
import org.springframework.stereotype.Service;

import javax.xml.crypto.dsig.keyinfo.KeyValue;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class MemoryForkService {
    private ScheduledExecutorService executorService;

    private final LinkedHashMap<Integer, Fork> map = new LinkedHashMap<>();

    public MemoryForkService(ScheduledExecutorService executorService) {
        this.executorService = executorService;

        executorService.schedule(this::deleteAll, 30, TimeUnit.MINUTES);
    }

    public void save(Map<Integer, Fork> forks) {
        map.putAll(forks);
    }
    public boolean exists(int hashcode){
        return map.containsKey(hashcode);
    }

    public int count(){
        return map.entrySet().size();
    }
    public Map<Integer, Fork> findAll() {
        return map;
    }

    public Map<Integer, Fork> range(int from, int to){
        return map.entrySet().stream()
                .skip(from)
                .limit(to)
                .collect(Collectors.toMap(
                        Map.Entry::getKey, Map.Entry::getValue));
    }

    public void deleteAll() {
        map.clear();
    }
}
