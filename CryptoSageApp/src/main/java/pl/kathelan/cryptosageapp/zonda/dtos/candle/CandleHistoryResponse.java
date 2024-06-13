package pl.kathelan.cryptosageapp.zonda.dtos.candle;

import lombok.Data;

import java.util.List;

@Data
public class CandleHistoryResponse {
    private String status;
    private List<CandleItem> items;
}