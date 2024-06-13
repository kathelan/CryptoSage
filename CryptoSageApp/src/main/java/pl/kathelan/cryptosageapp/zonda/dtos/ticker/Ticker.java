package pl.kathelan.cryptosageapp.zonda.dtos.ticker;

import lombok.Data;

@Data
public class Ticker {
    private Market market;
    private String time;
    private String highestBid;
    private String lowestAsk;
    private String rate;
    private String previousRate;
}