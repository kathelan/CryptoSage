package pl.kathelan.cryptosageapp.zonda.dtos.trades;

import lombok.Data;

import java.util.ArrayList;

@Data
public class LastTradesResponse {
    public String status;
    public ArrayList<Item> items;
}
