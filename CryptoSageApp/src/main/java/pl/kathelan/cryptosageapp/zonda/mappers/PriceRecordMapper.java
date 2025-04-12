package pl.kathelan.cryptosageapp.zonda.mappers;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import pl.kathelan.cryptosageapp.prices.dtos.PriceRecordDto;
import pl.kathelan.cryptosageapp.zonda.model.PriceRecord;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PriceRecordMapper
{
    private final ModelMapper modelMapper;

    public PriceRecordMapper() {
        this.modelMapper = new ModelMapper();
        configureModelMapper();
    }

    private void configureModelMapper() {
        modelMapper.createTypeMap(PriceRecord.class, PriceRecordDto.class)
                .addMapping(PriceRecord::getCreatedAt, PriceRecordDto::setTimestamp)
                .addMappings(mapper ->
                        mapper.map(src -> src.getCryptoCurrencyPair().getCryptoPair().name(), PriceRecordDto::setPair)
                );
    }

    public PriceRecordDto toDto(PriceRecord priceRecord) {
        return modelMapper.map(priceRecord, PriceRecordDto.class);
    }

    public List<PriceRecordDto> toDtoList(List<PriceRecord> priceRecords) {
        return priceRecords.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
