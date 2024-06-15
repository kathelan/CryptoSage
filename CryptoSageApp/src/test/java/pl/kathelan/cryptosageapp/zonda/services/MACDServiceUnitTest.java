package pl.kathelan.cryptosageapp.zonda.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.kathelan.cryptosageapp.zonda.dtos.CryptoPair;
import pl.kathelan.cryptosageapp.zonda.dtos.candle.CandleData;
import pl.kathelan.cryptosageapp.zonda.dtos.candle.CandleHistoryResponse;
import pl.kathelan.cryptosageapp.zonda.dtos.candle.CandleItem;


import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class MACDServiceTest {

    @Mock
    private CandleDataService candleDataService;

    @InjectMocks
    private MACDService macdService;


    @Test
    void shouldCalculateMACD() {
        Mockito.when(candleDataService.getHistory(anyString(), anyLong(), anyLong(), anyString())).thenReturn(createMockResponse());

        macdService.getHistoricalPrices(CryptoPair.BTC_PLN);

        verify(candleDataService, times(1)).getHistory(anyString(), anyLong(), anyLong(), anyString());

    }


    private CandleHistoryResponse createMockResponse() {
        List<CandleItem> items = new ArrayList<>();
        for (int i = 0; i < 26; i++) {
            CandleData candleData = new CandleData(100.0 + i, 105.0 + i, 110.0 + i, 95.0 + i, 1000.0 + i);
            CandleItem candleItem = new CandleItem(1620000000000L + i * 60000, candleData);
            items.add(candleItem);
        }
        CandleHistoryResponse response = new CandleHistoryResponse();
        response.setItems(items);
        return response;
    }
}
