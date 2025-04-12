package pl.kathelan.cryptosageapp.prices.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaginationDto {
    private int page;
    private int size;
    private int totalPages;
    private long totalElements;
}
