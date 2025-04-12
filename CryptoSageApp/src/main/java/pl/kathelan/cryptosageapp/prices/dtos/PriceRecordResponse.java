package pl.kathelan.cryptosageapp.prices.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PriceRecordResponse {
    private List<PriceRecordDto> data;
    private PaginationDto pagination;
}
