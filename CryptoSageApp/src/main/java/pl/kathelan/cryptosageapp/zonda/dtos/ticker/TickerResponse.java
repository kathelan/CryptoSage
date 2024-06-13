package pl.kathelan.cryptosageapp.zonda.dtos.ticker;

import lombok.Data;

@Data
public class TickerResponse {
    private String status;
    private Ticker ticker;
}


