package com.anthill.coinswapscannerstore.beans;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Pair implements Serializable {

    private String title, url;

    private Exchange exchange;

    private Date updated;
    private BigDecimal price, volume24h;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair pair = (Pair) o;
        return title.equals(pair.title) &&
                exchange.equals(pair.exchange);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, exchange);
    }
}
