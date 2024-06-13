package pl.kathelan.cryptosageapp.zonda.dtos.ticker;

import lombok.Data;

@Data
public class Market {
    private String code;
    private int amountPrecision;
    private int pricePrecision;
    private int ratePrecision;
    private CurrencyDetails first;
    private CurrencyDetails second;
}