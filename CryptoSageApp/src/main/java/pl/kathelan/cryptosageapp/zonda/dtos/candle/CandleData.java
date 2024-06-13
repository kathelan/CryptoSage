package pl.kathelan.cryptosageapp.zonda.dtos.candle;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CandleData {
    private double o; // Open
    private double c; // Close
    private double h; // High
    private double l; // Low
    private double v; // Volume

    public CandleData(@JsonProperty("o") double o, @JsonProperty("c") double c, @JsonProperty("h") double h,
                      @JsonProperty("l") double l, @JsonProperty("v") double v) {
        this.o = o;
        this.c = c;
        this.h = h;
        this.l = l;
        this.v = v;
    }
}
