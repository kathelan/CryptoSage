package pl.kathelan.cryptosageapp.prices.api;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.kathelan.cryptosageapp.prices.dtos.PaginationDto;
import pl.kathelan.cryptosageapp.prices.dtos.PriceRecordDto;
import pl.kathelan.cryptosageapp.prices.dtos.PriceRecordResponse;
import pl.kathelan.cryptosageapp.zonda.services.PriceRecordService;

import java.time.LocalDate;

@RestController
@RequestMapping("/price-records")
@RequiredArgsConstructor
public class PriceRecordController {
    private final PriceRecordService priceRecordService;

    @GetMapping
    public ResponseEntity<PriceRecordResponse> getPriceRecords(
            @RequestParam(required = false) String pair,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size
    ) {
        Page<PriceRecordDto> pricePage = priceRecordService.getPriceRecords(pair, startDate, endDate, page, size);

        PaginationDto pagination = new PaginationDto(
                page,
                size,
                pricePage.getTotalPages(),
                pricePage.getTotalElements()
        );

        return ResponseEntity.ok(new PriceRecordResponse(pricePage.getContent(), pagination));
    }

}
