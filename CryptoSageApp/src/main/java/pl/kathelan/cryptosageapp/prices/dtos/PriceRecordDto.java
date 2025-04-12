package pl.kathelan.cryptosageapp.prices.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PriceRecordDto {
    private OffsetDateTime timestamp;
    private Double price;
    private String pair;
}
