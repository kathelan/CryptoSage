package pl.kathelan.cryptosageapp.zonda.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.kathelan.cryptosageapp.zonda.dtos.CryptoPair;
import pl.kathelan.cryptosageapp.zonda.repositories.PriceRecordRepository;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@Slf4j
@RequiredArgsConstructor
public class PriceRecordService {
    private final PriceRecordRepository priceRecordRepository;

    public Long getStartTime(CryptoPair cryptoPair) {
        LocalDateTime earliestCreatedAt = priceRecordRepository.findEarliestCreatedAtByCryptoPair(cryptoPair);
        if (earliestCreatedAt != null) {
            return earliestCreatedAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        } else {
            return System.currentTimeMillis();
        }
    }
}
