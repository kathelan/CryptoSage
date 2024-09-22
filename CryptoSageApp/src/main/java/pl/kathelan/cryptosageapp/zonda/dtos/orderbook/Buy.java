package pl.kathelan.cryptosageapp.zonda.dtos.orderbook;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Buy {
    public String ra;
    public String ca;
    public String sa;
    public String pa;
    public int co;
}
