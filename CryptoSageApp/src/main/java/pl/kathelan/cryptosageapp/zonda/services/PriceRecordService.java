package pl.kathelan.cryptosageapp.zonda.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kathelan.cryptosageapp.exceptions.*;
import pl.kathelan.cryptosageapp.prices.dtos.PriceRecordDto;
import pl.kathelan.cryptosageapp.zonda.dtos.CryptoPair;
import pl.kathelan.cryptosageapp.zonda.mappers.PriceRecordMapper;
import pl.kathelan.cryptosageapp.zonda.model.CryptoCurrencyPair;
import pl.kathelan.cryptosageapp.zonda.model.PriceRecord;
import pl.kathelan.cryptosageapp.zonda.repositories.PriceRecordRepository;

import java.time.*;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PriceRecordService {
    private final PriceRecordRepository priceRecordRepository;
    private final CryptoCurrencyPairService cryptoCurrencyPairService;
    private final PriceRecordMapper priceRecordMapper;

    public Long getStartTime(CryptoPair cryptoPair) {
        LocalDateTime earliestCreatedAt = priceRecordRepository.findEarliestCreatedAtByCryptoPair(cryptoPair);
        if (earliestCreatedAt != null) {
            return earliestCreatedAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        } else {
            return System.currentTimeMillis();
        }
    }

    /**
     * Pobiera wszystkie unikalne pary kryptowalut z tabeli PRICE_RECORD.
     */
    public List<CryptoCurrencyPair> getAllCryptoPairs() {
        return cryptoCurrencyPairService.getCryptoPairs();
    }


    /**
     * Usuwa dane cenowe starsze niż określona liczba dni.
     *
     * @param days liczba dni
     */
    @Transactional
    public void cleanOldRecords(int days) {
        LocalDateTime thresholdDate = LocalDateTime.now().minusDays(days);
        int deletedCount = priceRecordRepository.deleteOldRecords(thresholdDate);
        log.info("Deleted {} old data from table PRICE_RECORD", deletedCount);
    }

    /**
     * Agreguje dane cenowe dla danej pary kryptowalut, starsze niż określona liczba dni.
     *
     * @param cryptoPair para kryptowalut
     * @param days       liczba dni do agregacji
     * @return lista zagregowanych danych (data, średnia cena)
     */
    public List<Object[]> aggregatePriceData(CryptoCurrencyPair cryptoPair, int days) {
        LocalDateTime thresholdDate = LocalDateTime.now().minusDays(days);
        return priceRecordRepository.aggregatePriceData(cryptoPair, thresholdDate);
    }

    /**
     * Pobiera rekordy cenowe kryptowalut z możliwością filtrowania i paginacji.
     *
     * @param pairStr   Nazwa pary kryptowalut w formacie "BTC-PLN" (opcjonalne)
     * @param startDate Data początkowa zakresu (opcjonalne)
     * @param endDate   Data końcowa zakresu (opcjonalne)
     * @param page      Numer strony wyników (indeksowanie od 0)
     * @param size      Liczba rekordów na stronę
     * @return Stronicowany zbiór obiektów DTO reprezentujących rekordy cenowe
     * @throws InvalidDateRangeException  gdy data końcowa jest wcześniejsza niż początkowa
     * @throws InvalidCryptoPairException gdy podana para kryptowalut nie jest obsługiwana
     * @throws PriceDataNotFoundException gdy nie znaleziono danych spełniających kryteria
     * @throws MappingException           gdy wystąpił błąd podczas mapowania encji na DTO
     * @throws DataRetrievalException     gdy wystąpił inny błąd podczas pobierania danych
     */
    public Page<PriceRecordDto> getPriceRecords(String pairStr, LocalDate startDate, LocalDate endDate, int page, int size) {
        validateDateRange(startDate, endDate);

        CryptoPair cryptoPair = parseCryptoPair(pairStr);

        LocalDateTime startDateTime = convertToStartDateTime(startDate);
        LocalDateTime endDateTime = convertToEndDateTime(endDate);

        Pageable pageable = PageRequest.of(page, size);

        try {
            Page<PriceRecord> priceRecords = fetchPriceRecords(cryptoPair, startDateTime, endDateTime, pageable);
            return mapToDtoPage(priceRecords);
        } catch (Exception e) {
            if (e instanceof PriceDataNotFoundException || e instanceof MappingException) {
                throw e;
            }
            throw new DataRetrievalException("Error retrieving price records", e);
        }
    }

    /**
     * Waliduje zakres dat, sprawdzając czy data końcowa nie jest wcześniejsza niż początkowa.
     *
     * @param startDate Data początkowa
     * @param endDate   Data końcowa
     * @throws InvalidDateRangeException gdy data końcowa jest wcześniejsza niż początkowa
     */
    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
            throw new InvalidDateRangeException(startDate, endDate);
        }
    }

    /**
     * Parsuje string z nazwą pary kryptowalut na odpowiedni enum CryptoPair.
     *
     * @param pairStr Nazwa pary kryptowalut (np. "BTC-PLN")
     * @return Odpowiadający enum CryptoPair lub null jeśli pairStr jest null lub pusty
     * @throws InvalidCryptoPairException gdy podana para kryptowalut nie jest obsługiwana
     */
    private CryptoPair parseCryptoPair(String pairStr) {
        if (pairStr == null || pairStr.isEmpty()) {
            return null;
        }

        try {
            return Arrays.stream(CryptoPair.values())
                    .filter(cp -> cp.getValue().equals(pairStr))
                    .findFirst()
                    .orElseThrow(() -> new InvalidCryptoPairException(pairStr));
        } catch (Exception e) {
            throw new InvalidCryptoPairException(pairStr, e);
        }
    }

    /**
     * Konwertuje LocalDate na LocalDateTime ustawiony na początek dnia.
     *
     * @param date Data do konwersji
     * @return LocalDateTime ustawiony na początek dnia lub null jeśli date jest null
     */
    private LocalDateTime convertToStartDateTime(LocalDate date) {
        return date != null ? date.atStartOfDay() : null;
    }

    /**
     * Konwertuje LocalDate na LocalDateTime ustawiony na koniec dnia.
     *
     * @param date Data do konwersji
     * @return LocalDateTime ustawiony na koniec dnia (23:59:59.999999999) lub null jeśli date jest null
     */
    private LocalDateTime convertToEndDateTime(LocalDate date) {
        return date != null ? date.plusDays(1).atStartOfDay().minusNanos(1) : null;
    }

    /**
     * Pobiera rekordy cenowe z repozytorium z zastosowaniem filtrów.
     *
     * @param pair          Para kryptowalut do filtrowania
     * @param startDateTime Data i czas początkowe zakresu
     * @param endDateTime   Data i czas końcowe zakresu
     * @param pageable      Obiekt paginacji
     * @return Stronicowany zbiór rekordów cenowych
     * @throws PriceDataNotFoundException gdy nie znaleziono żadnych danych spełniających kryteria
     */
    private Page<PriceRecord> fetchPriceRecords(CryptoPair pair, LocalDateTime startDateTime,
                                                LocalDateTime endDateTime, Pageable pageable) {
        Page<PriceRecord> priceRecords = priceRecordRepository.findWithFilters(
                pair, startDateTime, endDateTime, pageable);

        if (priceRecords.getTotalElements() == 0) {
            throw new PriceDataNotFoundException(pair,
                    startDateTime != null ? startDateTime.toLocalDate() : null,
                    endDateTime != null ? endDateTime.toLocalDate() : null);
        }

        return priceRecords;
    }

    /**
     * Mapuje stronę encji PriceRecord na stronę DTO PriceRecordDto.
     *
     * @param priceRecords Strona z encjami PriceRecord
     * @return Strona z obiektami DTO PriceRecordDto
     * @throws MappingException gdy wystąpi błąd podczas mapowania
     */
    private Page<PriceRecordDto> mapToDtoPage(Page<PriceRecord> priceRecords) {
        try {
            return priceRecords.map(priceRecordMapper::toDto);
        } catch (Exception e) {
            throw new MappingException("Error mapping PriceRecord to PriceRecordDto", e);
        }
    }
}
