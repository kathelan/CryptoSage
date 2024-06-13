package pl.kathelan.cryptosageapp.zonda.dtos.orderbook;

import lombok.Data;

import java.util.ArrayList;

@Data
public class OrderBookResponse {
    public String status;
    public ArrayList<Sell> sell;
    public ArrayList<Buy> buy;
    public String timestamp;
    public String seqNo;
}
