package it.pjor94.beerhunter.service;

import com.binance.api.client.domain.market.CandlestickInterval;
import it.pjor94.beerhunter.core.exchange.BinanceClient;
import it.pjor94.beerhunter.model.Candlestick;
import it.pjor94.beerhunter.model.HistoricalData;
import it.pjor94.beerhunter.repository.CandlestickRepository;
import it.pjor94.beerhunter.repository.HistoricalDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HistoricalDataService {

    Logger log = LoggerFactory.getLogger(HistoricalDataService.class);

    @Autowired
    protected HistoricalDataRepository historicalDataRepository;
    @Autowired
    protected CandlestickRepository candlestickRepository;

    private HistoricalData donwloadData(String pair){
        log.info(pair+" START DOWNLOADING HISTORY");
        BinanceClient client = new BinanceClient();
        List<Candlestick> candlesticks = client.getCandlestickBars(pair,CandlestickInterval.ONE_MINUTE,1000,null,null);
        Long currenTimeStamp = candlesticks.get(0).getOpenTime();
        HistoricalData historicalData = historicalDataRepository.findByPair(pair);
        historicalData = historicalData != null ? historicalData : new HistoricalData();
        historicalData.setPair(pair);
        historicalData.setStatus("updating");
        historicalData.setEnd(currenTimeStamp);
        candlestickRepository.saveAll(candlesticks);
        historicalData = historicalDataRepository.save(historicalData);
        boolean hasToBreak = false;
        int count = 0;
        while (!hasToBreak) {
            count++;
            log.info(pair +" -- Request n: " + count);
            candlesticks = client.getCandlestickBars(pair,CandlestickInterval.ONE_MINUTE,1000,null,currenTimeStamp);
            if (!currenTimeStamp.equals(candlesticks.get(0).getOpenTime())) {
                currenTimeStamp = candlesticks.get(0).getOpenTime();
                candlestickRepository.saveAll(candlesticks);
                log.info(pair +" -- Current Candle:" + new Date(currenTimeStamp));
                historicalData.setStart(currenTimeStamp);
                historicalData = historicalDataRepository.save(historicalData);
            } else {
                hasToBreak = true;
            }
        }
        historicalData.setStatus("done");
        log.info(pair+"  HISTORY DOWNLOAD COMPLETED");
        return historicalDataRepository.save(historicalData);
    }

    public HistoricalData updateHistory(String pair){
        log.info(pair+" UPDATING HISTORY");
        HistoricalData historicalData = historicalDataRepository.findByPair(pair);
        if(historicalData == null){
            log.info(pair+" NO HISTORY FOUND");
            return donwloadData(pair);
        }
        historicalData.setStatus("updating");
        Long end = candlestickRepository.findFirstByPairOrderByOpenTimeDesc(pair).getOpenTime();
        Long start = candlestickRepository.findFirstByPairOrderByOpenTimeAsc(pair).getOpenTime();
        historicalData.setEnd(end);
        historicalData.setStart(start);
        historicalData = historicalDataRepository.save(historicalData);
        BinanceClient client = new BinanceClient();
        List<Candlestick> candlesticks = client.getCandlestickBars(pair,CandlestickInterval.ONE_MINUTE,1000,null,null);
        candlesticks = candlesticks.stream()
                .filter(candlestick -> candlestick.getOpenTime() > end )
                .collect(Collectors.toList());
        Long currenTimeStamp = candlesticks.get(0).getOpenTime();
        candlestickRepository.saveAll(candlesticks);
        boolean hasToBreak = false;
        int count = 0;
        while (!hasToBreak) {
            count++;
            if (currenTimeStamp > end) {
                log.info(pair + " -- Request n: " + count);
                candlesticks = client.getCandlestickBars(pair, CandlestickInterval.ONE_MINUTE, 1000,null, currenTimeStamp);
                candlesticks = candlesticks.stream()
                        .filter(candlestick -> candlestick.getOpenTime() >= end )
                        .collect(Collectors.toList());
                currenTimeStamp = candlesticks.get(0).getOpenTime();
                historicalData.setEnd(currenTimeStamp);
                candlestickRepository.saveAll(candlesticks);
                historicalData = historicalDataRepository.save(historicalData);
                log.info(pair +" -- Current Candle:" + new Date(currenTimeStamp).toString());
            } else {
                hasToBreak = true;
            }
        }
        log.info(pair+"  HISTORY UPDATE COMPLETED");
        return historicalData;

    }
}
