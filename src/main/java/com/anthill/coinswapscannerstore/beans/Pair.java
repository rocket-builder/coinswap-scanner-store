package com.anthill.coinswapscannerstore.beans;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Pair implements Serializable {

    private String title, url;

    private Exchange exchange;

    private Date updated;
    private BigDecimal price, volume24h;
}
