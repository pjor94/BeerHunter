package it.pjor94.beerhunter.core;


import com.binance.api.client.domain.market.CandlestickInterval;
import it.pjor94.beerhunter.core.bar.BarSeriesGenerator;
import it.pjor94.beerhunter.core.bar.CandelstickList;
import it.pjor94.beerhunter.core.bar.MultiBarSeries;
import it.pjor94.beerhunter.core.exchange.BinanceClient;
import it.pjor94.beerhunter.model.Candlestick;
import it.pjor94.beerhunter.model.HistoricalData;
import it.pjor94.beerhunter.repository.CandlestickRepository;
import it.pjor94.beerhunter.service.HistoricalDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.ta4j.core.BarSeries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class CacheManager {
    Logger log = LoggerFactory.getLogger(CacheManager.class);

    private final BinanceClient anonymusBinanceClient = new BinanceClient();
    @Autowired
    protected HistoricalDataService historicalDataService;
    @Autowired
    private CandlestickRepository candlestickRepository;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private ApplicationEventPublisher publisher;

    private final HashMap<String, MultiBarSeries> cachers = new HashMap<>();
    private final List<String> loadingCachers = new ArrayList<>();

    public void addCacher(String pair){
        if(!loadingCachers.contains(pair) && !cachers.containsKey(pair)){
            loadingCachers.add(pair);
            Thread thread =  new Thread(() -> {
                HistoricalData data = historicalDataService.updateHistory(pair);
                List<Candlestick> candlesticks = candlestickRepository.findByPairOrderByOpenTimeDesc(pair,PageRequest.of(0, 10000)).getContent();
                MultiBarSeries barSeriesGenerator = new BarSeriesGenerator(pair,new CandelstickList(candlesticks),publisher);
                anonymusBinanceClient.getWsClient().onCandlestickEvent(pair.toLowerCase(), CandlestickInterval.ONE_MINUTE, (candlestickEvent)-> {
                    if (candlestickEvent.getBarFinal()) {
                        barSeriesGenerator.addBar(new Candlestick(pair, candlestickEvent));
                    }
                });
                BarSeries _5mSeries = barSeriesGenerator.getBarSeries("5m");
                log.info(_5mSeries.getSeriesPeriodDescription());
                cachers.put(pair,barSeriesGenerator);
                loadingCachers.remove(pair);
            });
            thread.start();
        }else if(loadingCachers.contains(pair)){
            log.error(pair + " CACHER IS ALREADY LOADING");
        }if(cachers.containsKey(pair)){
            log.error(pair + " CACHER IS ALREADY STARTED");
        }

    }

    public MultiBarSeries getCacher(String pair){
        return cachers.get(pair);
    }



}
