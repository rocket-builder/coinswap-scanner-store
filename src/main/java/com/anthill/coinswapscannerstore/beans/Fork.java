package com.anthill.coinswapscannerstore.beans;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@RedisHash(value = "Fork", timeToLive = 3600)
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class Fork implements Serializable {

    private Token token;
    private Pair firstPair, secondPair;

    private BigDecimal profitPercent;
    private String url;
    private Date recieveDate;
}

