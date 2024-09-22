package pl.kathelan.cryptosageapp.zonda.services.calculation.trading;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.kathelan.cryptosageapp.zonda.dtos.CryptoPair;
import pl.kathelan.cryptosageapp.zonda.dtos.orderbook.Buy;
import pl.kathelan.cryptosageapp.zonda.dtos.orderbook.OrderBookResponse;
import pl.kathelan.cryptosageapp.zonda.dtos.orderbook.Sell;
import pl.kathelan.cryptosageapp.zonda.services.OrderBookService;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PriceServiceUnitTest {

    @Mock
    private OrderBookService orderBookService;

    @InjectMocks
    private PriceService priceService;


    @Test
    void getPrice_whenValidResponse_returnsCorrectPrice() {
        CryptoPair cryptoPair = CryptoPair.BTC_PLN;
        ArrayList<Buy> buyList = new ArrayList<>();
        buyList.add(Buy.builder().ra("100.5").build());
        ArrayList<Sell> sellList = new ArrayList<>();
        sellList.add(Sell.builder().ra("100.5").build());
        OrderBookResponse mockResponse = new OrderBookResponse();
        mockResponse.setStatus("Ok");
        mockResponse.setBuy(buyList);
        mockResponse.setSell(sellList);;
        when(orderBookService.getOrderBookResponseByTradingPair("BTC-PLN")).thenReturn(mockResponse);

        BigDecimal result = priceService.getPrice(cryptoPair);

        assertNotNull(result);
        assertEquals(new BigDecimal("100.5"), result);
    }

    @Test
    void getPrice_whenInvalidResponse_returnsNull() {
        CryptoPair cryptoPair = CryptoPair.BTC_PLN;
        when(orderBookService.getOrderBookResponseByTradingPair(anyString())).thenReturn(null);

        BigDecimal result = priceService.getPrice(cryptoPair);

        assertNull(result);
    }

}