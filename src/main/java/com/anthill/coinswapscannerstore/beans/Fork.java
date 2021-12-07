package com.anthill.coinswapscannerstore.beans;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

@RedisHash(value = "Fork", timeToLive = 3600)
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class Fork implements Serializable {

    private String id;
    private Token token;
    private Pair firstPair, secondPair;

    private BigDecimal profitPercent;
    private String url;
    private Date recieveDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Fork fork = (Fork) o;
        return firstPair.equals(fork.firstPair) &&
                secondPair.equals(fork.secondPair);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstPair, secondPair);
    }

    public String hashCodeString(){
        return String.valueOf(hashCode());
    }
}

