package it.pjor94.beerhunter.core;


import it.pjor94.beerhunter.core.bar.CacheManager;
import it.pjor94.beerhunter.core.strategy.BHStrategyParser;
import it.pjor94.beerhunter.core.strategy.ParserException;
import it.pjor94.beerhunter.repository.CandlestickRepository;
import it.pjor94.beerhunter.service.HistoricalDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Strategy;
import org.ta4j.core.indicators.RSIIndicator;
import org.ta4j.core.indicators.StochasticRSIIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;

import java.util.ArrayList;
import java.util.List;

@Service
public class Core {
    Logger log = LoggerFactory.getLogger(Core.class);

    @Autowired
    protected CandlestickRepository candlestickRepository;
    @Autowired
    protected HistoricalDataService historicalDataService;
    @Autowired
    protected CacheManager barSeriesCacheManager;




    public void start(){
        List<String> pairs= new ArrayList<>();
        pairs.add("BTCUSDT");
        pairs.add("ETHEUR");
        pairs.add("GRTUSDT");
        pairs.add("EOSUSDT");
        pairs.add("XRPUSDT");
        pairs.add("BCHUSDT");
        pairs.add("ETHUSDT");
        pairs.add("1INCHUSDT");
        pairs.add("DOTUSDT");
        pairs.parallelStream().forEach((pair)-> barSeriesCacheManager.startMultiBarSeries(pair));

    }

    @Async
    public void newCandleEvent(String pair,String timeframe, Bar bar,BarSeries barSeries) throws ParserException {
        String info = pair + " " + timeframe + " " + bar.toString();
        log.info(info);
        StochasticRSIIndicator stochasticRSIIndicator = new StochasticRSIIndicator(barSeries,7);
        RSIIndicator rsi = new RSIIndicator(new ClosePriceIndicator(barSeries), 7);
        stochasticRSIIndicator.getValue(barSeries.getEndIndex());

        BHStrategyParser parser = new BHStrategyParser();
        String rsiStrategy = "(RSI[7] <= 20) [GO_LONG] ; (RSI[7] >= 80) [GO_SHORT]";
        //Strategy strategy = parser.parse("((EMA[30] >= WMA[87]) OR (CCI[5] <= SMA[87])) [GO_LONG] ; (CCI[725] <= 2.6) [GO_SHORT]",barSeries);
        Strategy strategy2 = parser.parse(rsiStrategy,barSeries);
        //strategy2.
        if(strategy2.shouldEnter(barSeries.getEndIndex())){

            log.info("RSI STRATEGY LONG");
        }
        if(strategy2.shouldExit(barSeries.getEndIndex())){
            log.info("RSI STRATEGY SHORT");

        }


    }

    private void test2(){


    }


}
