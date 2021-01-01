package it.pjor94.beerhunter.core.bar;

import com.binance.api.client.domain.market.CandlestickInterval;
import it.pjor94.beerhunter.core.exchange.BinanceClient;
import it.pjor94.beerhunter.model.Candlestick;
import it.pjor94.beerhunter.model.HistoricalData;
import it.pjor94.beerhunter.repository.CandlestickRepository;
import it.pjor94.beerhunter.service.HistoricalDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.ta4j.core.BarSeries;

import java.util.HashMap;
import java.util.Optional;

@Component
public class BarSeriesCacheManager implements CacheManager {
    Logger log = LoggerFactory.getLogger(CacheManager.class);
    private final HashMap<String, MultiBarSeries> multiBarSeries = new HashMap<>();
    private final HashMap<String,HistoricalData> historicalDataHashMap = new HashMap<>();
    @Autowired
    private HistoricalDataService historicalDataService;
    @Autowired
    private CandlestickRepository candlestickRepository;
    @Autowired
    private ApplicationEventPublisher publisher;
    private final BinanceClient anonymusBinanceClient = new BinanceClient();

    @Async
    public void startMultiBarSeries(String pair)  {
        if(!historicalDataHashMap.containsKey(pair)) {
            historicalDataHashMap.put(pair, new HistoricalData());
            historicalDataHashMap.put(pair, historicalDataService.updateHistory(pair));
            CandelstickList candlesticks = new CandelstickList(candlestickRepository.findByPairOrderByOpenTimeDesc(pair, PageRequest.of(0, 10000)).getContent());
            MultiBarSeries series = new BarSeriesGenerator(pair, candlesticks, publisher);
            log.info(pair+" STARTING LIVE STREAM");
            anonymusBinanceClient.getWsClient().onCandlestickEvent(pair.toLowerCase(), CandlestickInterval.ONE_MINUTE, (candlestickEvent) -> {
                if (candlestickEvent.getBarFinal()) {
                    Candlestick newCandle = new Candlestick(pair, candlestickEvent);
                    series.addBar(newCandle);
                    saveNewCandle(pair, newCandle);
                }
            });
            multiBarSeries.put(pair, series);
        }else{
            log.error(pair+" MULTIBARSERIES  ALREADY STARTED");
        }
    }

    @Async
    public void saveNewCandle(String pair ,Candlestick newCandle) {

    }

    public Optional<BarSeries> getBarSeries(String pair, String timeframe){
        if(multiBarSeries.containsKey(pair)){
           return Optional.of(multiBarSeries.get(pair).getBarSeries(timeframe));
        }
        return Optional.empty();
    }
}
