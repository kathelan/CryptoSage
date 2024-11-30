package pl.kathelan.cryptosageapp.zonda.mappers;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import pl.kathelan.commons.dto.AlertDTO;
import pl.kathelan.cryptosageapp.zonda.model.Alert;

@Component
@RequiredArgsConstructor
public class AlertMapper {
    private final ModelMapper modelMapper = new ModelMapper();

    public AlertDTO convertToDTO(Alert alert) {
        return modelMapper.map(alert, AlertDTO.class);
    }
}
