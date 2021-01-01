package it.pjor94.beerhunter.core.exchange;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.BinanceApiWebSocketClient;
import com.binance.api.client.domain.market.CandlestickInterval;
import it.pjor94.beerhunter.model.Candlestick;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

public class BinanceClient {

    @Getter
    BinanceApiRestClient client;
    @Getter
    BinanceApiWebSocketClient wsClient;

    BinanceClient(String key,String secret){
        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance(key,secret);
        client = factory.newRestClient();
        wsClient = factory.newWebSocketClient();
    }

    public BinanceClient(){
        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance();
        client = factory.newRestClient();
        wsClient = factory.newWebSocketClient();
    }

    public List<Candlestick> getCandlestickBars(String symbol, CandlestickInterval interval, Integer limit, Long startTime, Long endTime){
        return client.getCandlestickBars(symbol,interval,limit,startTime,endTime).stream()
                .map((el)-> new Candlestick(symbol,el))
                .collect(Collectors.toList());
    }
}
