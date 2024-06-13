package pl.kathelan.cryptosageapp.zonda.dtos.candle;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CandleItem {
    private long time;
    private CandleData data;

    public CandleItem(@JsonProperty("time") long time, @JsonProperty("data") CandleData data) {
        this.time = time;
        this.data = data;
    }
}
