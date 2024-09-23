package pl.kathelan.cryptosageapp.zonda.dtos.trades;

import lombok.Data;

import java.util.ArrayList;

@Data
public class LastTradesResponse {
    private String status;
    private ArrayList<Item> items;
}
