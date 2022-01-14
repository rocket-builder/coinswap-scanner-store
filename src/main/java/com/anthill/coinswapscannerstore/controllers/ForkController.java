package com.anthill.coinswapscannerstore.controllers;

import com.anthill.coinswapscannerstore.beans.Fork;
import com.anthill.coinswapscannerstore.services.ForkService;
import com.anthill.coinswapscannerstore.services.ForkWebsocketMemoryService;
import com.anthill.coinswapscannerstore.services.ForkWebsocketService;
import com.anthill.coinswapscannerstore.services.MemoryForkService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequestMapping("/forks")
@RestController
public class ForkController {

    private final MemoryForkService forkService;

    public ForkController(ForkWebsocketMemoryService forkWebsocketService, MemoryForkService forkService) {
        this.forkService = forkService;

        forkWebsocketService.init();
    }

    @GetMapping()
    public ResponseEntity<Map<Integer, Fork>> getAll(){
        return new ResponseEntity<>(
                forkService.findAll(), HttpStatus.OK);
    }
    @GetMapping("/count")
    public ResponseEntity<Integer> count(){
        return new ResponseEntity<>(forkService.count(), HttpStatus.OK);
    }
    @GetMapping("/range")
    public ResponseEntity<Map<Integer, Fork>> getRange(@RequestParam("from") int from, @RequestParam("to") int to){
        return new ResponseEntity<>(
                forkService.range(from, to), HttpStatus.OK);
    }

    @GetMapping("/delete")
    public ResponseEntity<String> deleteAll(){
        forkService.deleteAll();

        return new ResponseEntity<>(
                "Successfully deleted!", HttpStatus.OK);
    }
}
