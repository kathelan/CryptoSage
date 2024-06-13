package pl.kathelan.cryptosageapp.zonda.dtos.ticker;

import lombok.Data;

@Data
public class CurrencyDetails {
    private String currency;
    private String minOffer;
    private Integer scale;
}
