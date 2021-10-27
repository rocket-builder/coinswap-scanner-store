package com.anthill.coinswapscannerstore.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/")
    public ResponseEntity<String> index(){
        return new ResponseEntity<>(
                "CoinswapScanner store works normally!", HttpStatus.OK);
    }
}
