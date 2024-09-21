package pl.kathelan.cryptosageapp.zonda.mappers;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import pl.kathelan.cryptosageapp.zonda.model.CryptoCurrencyPair;
import pl.kathelan.cryptosageapp.zonda.model.PriceRecord;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CryptoCurrencyMapper {
    private final ModelMapper modelMapper = new ModelMapper();

    public Set<PriceRecord> mapPriceRecordsFromDouble(List<Double> records, CryptoCurrencyPair cryptoCurrencyPair) {
        return records.stream().map(aDouble -> {
                    PriceRecord priceRecord = new PriceRecord();
                    priceRecord.setPrice(aDouble);
                    priceRecord.setCryptoCurrencyPair(cryptoCurrencyPair);
                    return priceRecord;
                })
                .collect(Collectors.toSet());
    }

    public List<Double> mapPriceRecordsToDouble(List<PriceRecord> records) {
        return records.stream().map(PriceRecord::getPrice).toList();
    }

    public PriceRecord mapPriceRecordToDouble(Double aDouble) {
        return modelMapper.map(aDouble, PriceRecord.class);
    }
}
