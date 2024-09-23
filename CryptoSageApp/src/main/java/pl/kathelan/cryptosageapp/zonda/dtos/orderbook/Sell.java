package pl.kathelan.cryptosageapp.zonda.dtos.orderbook;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Sell {
    private String ra;
    private String ca;
    private String sa;
    private String pa;
    private int co;
}
