package pl.kathelan.cryptosageapp.zonda.dtos.orderbook;

import lombok.Data;

import java.util.ArrayList;

@Data
public class OrderBookResponse {
    private String status;
    private ArrayList<Sell> sell;
    private ArrayList<Buy> buy;
    private String timestamp;
    private String seqNo;
}
