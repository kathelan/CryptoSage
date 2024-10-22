package pl.kathelan.cryptosageapp.zonda.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.kathelan.cryptosageapp.zonda.model.Holding;
import pl.kathelan.cryptosageapp.zonda.model.TransactionHistory;
import pl.kathelan.cryptosageapp.zonda.model.TransactionType;
import pl.kathelan.cryptosageapp.zonda.repositories.TransactionHistoryRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionHistoryService {
    private final TransactionHistoryRepository transactionHistoryRepository;


    public TransactionHistory createHistoryTransaction(TransactionType transactionType, Holding holding, BigDecimal pricePerUnit, BigDecimal totalValue) {
        return TransactionHistory.builder()
                .transactionType(transactionType)
                .holding(holding)
                .pricePerUnit(pricePerUnit)
                .quantity(totalValue.divide(pricePerUnit, 8, RoundingMode.HALF_UP))
                .totalValue(totalValue)
                .build();
    }

    public Optional<TransactionHistory> getLatestTransactionByHoldingId(Long holdingId) {
        return transactionHistoryRepository.findFirstByHoldingIdOrderByIdDesc(holdingId);
    }
}
