package com.anthill.coinswapscannerstore.beans;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Exchange implements Serializable {

    private long id;
    private String title;
    private Quote quote;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Exchange exchange = (Exchange) o;
        return title.equals(exchange.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title);
    }
}
