package pl.kathelan.cryptosageapp.zonda.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.kathelan.cryptosageapp.zonda.model.Holding;
import pl.kathelan.cryptosageapp.zonda.model.TransactionHistory;
import pl.kathelan.cryptosageapp.zonda.model.TransactionType;

import java.math.BigDecimal;

@Service
@Slf4j
public class TransactionHistoryService {

    public TransactionHistory createHistoryTransaction(TransactionType transactionType, Holding holding, BigDecimal pricePerUnit, BigDecimal quantity) {
        return TransactionHistory.builder()
                .transactionType(transactionType)
                .holding(holding)
                .pricePerUnit(pricePerUnit)
                .quantity(quantity)
                .build();
    }
}
