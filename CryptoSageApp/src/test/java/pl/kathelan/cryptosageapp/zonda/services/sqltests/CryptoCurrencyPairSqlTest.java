package pl.kathelan.cryptosageapp.zonda.services.sqltests;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import pl.kathelan.cryptosageapp.zonda.dtos.CryptoPair;
import pl.kathelan.cryptosageapp.zonda.model.CryptoCurrencyPair;
import pl.kathelan.cryptosageapp.zonda.model.PriceRecord;
import pl.kathelan.cryptosageapp.zonda.repositories.CryptoCurrencyPairRepository;

import java.util.Comparator;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource(properties = "app.scheduling.enable=false")
class CryptoCurrencyPairSqlTest {

    @Autowired
    private CryptoCurrencyPairRepository cryptoCurrencyPairRepository;

    @Test
    @Order(1)
    void shouldSaveCryptoCurrencyPair() {
        CryptoCurrencyPair cryptoCurrencyPair = new CryptoCurrencyPair();
        cryptoCurrencyPair.setCryptoPair(CryptoPair.BTC_PLN);
        PriceRecord priceRecord = new PriceRecord(1.0, cryptoCurrencyPair);
        PriceRecord priceRecord1 = new PriceRecord(2.0, cryptoCurrencyPair);
        PriceRecord priceRecord2 = new PriceRecord(3.0, cryptoCurrencyPair);
        cryptoCurrencyPair.setPriceRecords(Set.of(priceRecord, priceRecord1, priceRecord2));

        CryptoCurrencyPair result = cryptoCurrencyPairRepository.save(cryptoCurrencyPair);
        PriceRecord firstPrice = result.getPriceRecords().stream()
                .min(Comparator.comparingDouble(PriceRecord::getPrice))
                .orElse(null);

        assertThat(result.getPriceRecords()).hasSize(3);
        assertEquals(firstPrice.getPrice(), priceRecord.getPrice());
    }

    @Test
    @Order(2)
    void shouldUpdateCryptoCurrencyPair() {
        CryptoCurrencyPair cryptoCurrencyPair = cryptoCurrencyPairRepository.findCurrencyPairWithRecordsById(1L).get();

        PriceRecord priceRecord = new PriceRecord(4.0, cryptoCurrencyPair);
        cryptoCurrencyPair.getPriceRecords().add(priceRecord);

        CryptoCurrencyPair result = cryptoCurrencyPairRepository.save(cryptoCurrencyPair);

        assertThat(result.getPriceRecords()).hasSize(4);
    }

    @Test
    @Order(3)
    void shouldReturnAll() {
        CryptoCurrencyPair cryptoCurrencyPair = cryptoCurrencyPairRepository.findAllWithPriceRecords();

        assertThat(cryptoCurrencyPair.getPriceRecords()).hasSize(4);
    }


    @Test
    @Order(4)
    void shouldDelete() {
        CryptoCurrencyPair cryptoCurrencyPair = cryptoCurrencyPairRepository.findCurrencyPairWithRecordsById(1L).get();

        cryptoCurrencyPairRepository.delete(cryptoCurrencyPair);

        CryptoCurrencyPair result = cryptoCurrencyPairRepository.findCurrencyPairWithRecordsById(1L).orElse(null);

        assertNull(result);
    }


}
