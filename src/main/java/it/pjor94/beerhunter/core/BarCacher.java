package it.pjor94.beerhunter.core;

import com.binance.api.client.domain.event.CandlestickEvent;
import com.binance.api.client.domain.market.CandlestickInterval;
import it.pjor94.beerhunter.core.bar.CandelstickList;
import it.pjor94.beerhunter.core.events.event.BarEvent;
import it.pjor94.beerhunter.core.exchange.BinanceClient;
import it.pjor94.beerhunter.model.Candlestick;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBar;
import org.ta4j.core.BaseBarSeries;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Scope("prototype")
public class BarCacher implements ApplicationEventPublisherAware {
    Logger log = LoggerFactory.getLogger(BarCacher.class);

    @Autowired
    private ApplicationEventPublisher publisher;

    @PostConstruct
    public void init(){
        //stuff
        log.info("New Cacher Instance Created");
    }

    //You must override this method; It will give you acces to ApplicationEventPublisher
    public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @Getter
    @Setter
    private BinanceClient client;
    @Getter
    @Setter
    private String pair;
    @Getter
    @Setter
    private CandelstickList candlesticks;
    @Getter
    @Setter
    private Long controlltimestamp;

    @Getter
    @Setter
    private Map<String, BarSeries> timeFramesMap = new HashMap<>();

    private boolean hasToEmit;

    public void init(BinanceClient client, String pair, List<Candlestick> candlesticks) {
        log.info(pair + " CACHER INIT");
        this.client = client;
        this.pair = pair;
        this.candlesticks = new CandelstickList(candlesticks);
        controlltimestamp = Utils.toStartOfTheDay(this.candlesticks.get(0).getOpenTime());
        hasToEmit=false;
        log.info(pair +" CACHER WARMUP");
        for (Candlestick candle : this.candlesticks) {
            dispatcher(candle);
        }
        log.info(pair + " CACHER WARMUP COMPLETED");
        hasToEmit=true;
    }

    public void setUpStream() {
        log.info(pair + " CACHER STARTING LIVE STREAM");
        client.getWsClient().onCandlestickEvent(pair.toLowerCase(), CandlestickInterval.ONE_MINUTE, this::dispatcher);
    }

    private void dispatcher(CandlestickEvent event) {
        if (event.getBarFinal()) {
            Candlestick candlestick = new Candlestick(pair, event);
            candlesticks.add(candlestick);
            candlesticks.remove(0);
            _dispatcher(candlestick);
        }
    }

    private void dispatcher(Candlestick candlestick) {
        _dispatcher(candlestick);
    }

    private void _dispatcher(Candlestick candlestick) {
        Bar _1mBar = buildBar(candlestick);
        String _1mTimeframe = "1m";
        BarSeries _1mBarSeries = timeFramesMap.containsKey(_1mTimeframe) ? timeFramesMap.get(_1mTimeframe) : new BaseBarSeries(_1mTimeframe);
        if(_1mBarSeries.isEmpty() || (_1mBar.getEndTime().isAfter(_1mBarSeries.getLastBar().getEndTime()))){
            _1mBarSeries.addBar(_1mBar);
            if(hasToEmit){
                publisher.publishEvent(new BarEvent(this,pair, _1mTimeframe, _1mBar,_1mBarSeries));
            }
        }
        timeFramesMap.put(_1mTimeframe,_1mBarSeries);
        for (var timeframe :Utils.timeframes.entrySet()){
            BarSeries barSeries = timeFramesMap.containsKey(timeframe.getKey()) ? timeFramesMap.get(timeframe.getKey()) : new BaseBarSeries(timeframe.getKey());
            var candlestickOptional = candleCreator(candlestick, timeframe.getValue());
            if(candlestickOptional.isPresent()){
                Bar bar = buildBar(candlestickOptional.get());
                if(barSeries.isEmpty() || bar.getEndTime().isAfter(barSeries.getLastBar().getEndTime())){
                    barSeries.addBar(bar);
                    if(hasToEmit){
                        publisher.publishEvent(new BarEvent(this,pair, timeframe.getKey(), bar,barSeries));
                    }
                }
            }
            timeFramesMap.put(timeframe.getKey(),barSeries);
        }
    }

    private Optional<Candlestick> candleCreator(Candlestick candlestick, Long divider) {
        Long timestamp = Utils.addToTimestamp(candlestick.getCloseTime(),Calendar.MILLISECOND, 1);
        Long diff = minutesDifference(timestamp);
        if (diff % divider == 0) {
            Candlestick newCandle = new Candlestick();
            newCandle.setPair(pair);
            newCandle.setOpenTime(Utils.addToTimestamp(timestamp,Calendar.MINUTE, -divider.intValue()));
            newCandle.setCloseTime(Utils.addToTimestamp(timestamp,Calendar.MILLISECOND, -1));
            newCandle.setClose(BigDecimal.ZERO);
            newCandle.setOpen(BigDecimal.ZERO);
            newCandle.setLow(null);
            newCandle.setHigh(BigDecimal.ZERO);
            newCandle.setVolume(BigDecimal.ZERO);
            newCandle.setNumberOfTrades(0L);
            var candlesticksize=new CandelstickList(candlesticks.stream()
                    .filter((candle) -> candle.getOpenTime() >= newCandle.getOpenTime() && candle.getOpenTime() < newCandle.getCloseTime())
                    .collect(Collectors.toList()));
            candlesticksize.forEach((candle) -> {
                        newCandle.setLow(newCandle.getLow() != null ? newCandle.getLow() : candle.getLow());
                        newCandle.setLow((newCandle.getLow().compareTo(candle.getLow()) < 0) ? newCandle.getLow() : candle.getLow());
                        newCandle.setVolume(newCandle.getVolume().add(candle.getVolume()));
                        newCandle.setNumberOfTrades(newCandle.getNumberOfTrades() + candle.getNumberOfTrades());
                        newCandle.setHigh((newCandle.getHigh().compareTo(candle.getHigh()) > 0) ? newCandle.getHigh() : candle.getHigh());
                    });
            if(candlesticksize.size()> 0){
                newCandle.setOpen(candlesticksize.get(0).getOpen());
                newCandle.setClose(candlesticksize.get(candlesticksize.size()-1).getClose());
                return Optional.of(newCandle);
            }
        }
        return Optional.empty();
    }

    private Long minutesDifference(Long timestamp) {
        return (timestamp - controlltimestamp) / (60 * 1000);
    }

    private Bar buildBar(Candlestick candlestick){
        Instant start = Instant.ofEpochMilli(candlestick.getOpenTime());
        Instant end = Instant.ofEpochMilli(candlestick.getCloseTime());
        Duration timePeriod = Duration.between(start, end);
        ZoneId defaultZoneId = ZoneId.systemDefault();
        Instant instant = new Date(candlestick.getCloseTime()).toInstant();
        ZonedDateTime endTime = instant.atZone(defaultZoneId);
        return new BaseBar(timePeriod,endTime,candlestick.getOpen(),candlestick.getHigh(),candlestick.getLow(),candlestick.getClose(),candlestick.getVolume());
    }
}
