package it.pjor94.beerhunter.core.bar;

import it.pjor94.beerhunter.core.Utils;
import it.pjor94.beerhunter.core.events.event.BarEvent;
import it.pjor94.beerhunter.model.Candlestick;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBar;
import org.ta4j.core.BaseBarSeriesBuilder;
import org.ta4j.core.num.DoubleNum;

import java.time.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.stream.IntStream;

public class BarSeriesGenerator implements MultiBarSeries {
    Logger log = LoggerFactory.getLogger(MultiBarSeries.class);


    private final HashMap<String, Bar> _tempBars = new HashMap<String, Bar>();
    @Getter
    private final HashMap<String, BarSeries> barSeries = new HashMap<String, BarSeries>();
    private final HashMap<String, Duration> timeframesDurations = Utils.timeframesDurations;
    private final HashMap<String, ArrayList<LocalTime>> endTimesGrid = new HashMap<>();
    private final ApplicationEventPublisher publisher;
    @Getter
    private final String pair;

    BarSeriesGenerator(String pair ,CandelstickList list){
        this(pair,list,null);
    }
    public BarSeriesGenerator(String pair, CandelstickList list, ApplicationEventPublisher publisher){
        log.info(pair +" INIT MULTIBARSERIES ");
        this.pair =pair;
        barSeries.put("1m",new BaseBarSeriesBuilder().withNumTypeOf(DoubleNum::valueOf).withName("1m").build());
        timeframesDurations.forEach((key, value) -> {
            barSeries.put(key, new BaseBarSeriesBuilder().withNumTypeOf(DoubleNum::valueOf).withName(key).build());
            endTimesGrid.put(key, new ArrayList<LocalTime>());
            fillEndTimesGrid(key);
        });
        list.forEach(this::addBar);
        this.publisher=publisher;
        log.info(pair +" INIT MULTIBARSERIES COMPLETED ");
    }

    public BarSeries getBarSeries(String timeframe){
        return barSeries.get(timeframe);
    }

    public void addBar(Candlestick candlestick){
        Bar simpleBar = buildBar(candlestick);
        if(barSeries.get("1m").isEmpty() || (simpleBar.getEndTime().isAfter(barSeries.get("1m").getLastBar().getEndTime()))){
            barSeries.get("1m").addBar(simpleBar);
            if(publisher!=null) publisher.publishEvent(new BarEvent(this,pair, "1m", simpleBar,barSeries.get("1m")));
            processAddBar(simpleBar);
        }
    }

    private Bar buildBar(Candlestick candlestick){
        return BaseBar.builder()
                .timePeriod(Duration.between(Instant.ofEpochMilli(candlestick.getOpenTime()), Instant.ofEpochMilli(candlestick.getCloseTime())))
                .endTime(new Date(candlestick.getCloseTime()).toInstant().atZone(ZoneOffset.UTC))
                .openPrice(DoubleNum.valueOf(candlestick.getOpen()))
                .highPrice(DoubleNum.valueOf(candlestick.getHigh()))
                .lowPrice(DoubleNum.valueOf(candlestick.getLow()))
                .closePrice(DoubleNum.valueOf(candlestick.getClose()))
                .volume(DoubleNum.valueOf(candlestick.getVolume()))
                .build();
    }

    private void processAddBar(Bar smallBar){
        for (var entry : timeframesDurations.entrySet()){
            var timeframe = entry.getKey();
            if(shouldCreateNewBigBar(timeframe,smallBar.getEndTime().withZoneSameInstant(ZoneOffset.UTC))){
                Bar newBigBar = createBigBar(timeframe,smallBar);
                if(_tempBars.containsKey(timeframe)){
                    Bar completedBar = _tempBars.get(timeframe);
                    if(barSeries.get(timeframe).isEmpty() || (completedBar.getEndTime().isAfter(barSeries.get(timeframe).getLastBar().getEndTime()))){
                        barSeries.get(timeframe).addBar(completedBar);
                        if(publisher!=null) publisher.publishEvent(new BarEvent(this,pair, timeframe, completedBar,barSeries.get(timeframe)));
                    }
                    _tempBars.remove(timeframe);
                }
                _tempBars.put(timeframe,newBigBar);
            }else {
                updateCurrentBigBar(timeframe,smallBar);
            }
        }
    }

    private void updateCurrentBigBar(String timeframe,Bar smallBar) {
        Bar currentBigBar = _tempBars.get(timeframe) ;
        Bar updatedCurrentBigBar = BaseBar.builder()
                .timePeriod(currentBigBar.getTimePeriod())
                .endTime(currentBigBar.getEndTime())
                .openPrice(currentBigBar.getOpenPrice())
                .highPrice(currentBigBar.getHighPrice().max(smallBar.getHighPrice()))
                .lowPrice(currentBigBar.getLowPrice().min(smallBar.getLowPrice()))
                .closePrice(smallBar.getClosePrice())
                .volume(smallBar.getVolume().plus(currentBigBar.getVolume()))
                .build();
        _tempBars.put(timeframe,updatedCurrentBigBar);
    }

    private boolean shouldCreateNewBigBar(String timeframe,ZonedDateTime smallBarEndTimeUTC) {
        return barSeries.get(timeframe).getBeginIndex() == -1 || barSeries.get(timeframe).getLastBar().getEndTime().isBefore(smallBarEndTimeUTC);
    }

    private Bar createBigBar(String timeframe,Bar smallBar) {
        ZonedDateTime smallBarBeginTimeUTC = smallBar.getBeginTime().withZoneSameInstant(ZoneOffset.UTC);
        int currentBigBarEndTimeIndex = findBigBarEndTimeIndex(timeframe,smallBarBeginTimeUTC.toLocalTime());
        ZonedDateTime midnightUTC = smallBarBeginTimeUTC.with(LocalTime.MIN);
        ZonedDateTime bigBarEndDateTimeUTC = midnightUTC.with(endTimesGrid.get(timeframe).get(currentBigBarEndTimeIndex));
        Duration bigBarTimePeriod = cutBigBarTimePeriodIfExceedMidnight(timeframe,currentBigBarEndTimeIndex);
        return new BaseBar(bigBarTimePeriod, bigBarEndDateTimeUTC,
                smallBar.getOpenPrice(), smallBar.getHighPrice(), smallBar.getLowPrice(), smallBar.getClosePrice(),
                smallBar.getVolume(), smallBar.getAmount());
    }

    private int findBigBarEndTimeIndex(String timeframe,LocalTime smallBarBeginTime) {
        return IntStream.range(0, endTimesGrid.get(timeframe).size()).filter(i -> {
            LocalTime bigBarEndTime = endTimesGrid.get(timeframe).get(i);
            return (smallBarBeginTime.isBefore(bigBarEndTime));
        }).findFirst().orElse(endTimesGrid.get(timeframe).size() - 1);
    }

    private Duration cutBigBarTimePeriodIfExceedMidnight(String timeframe,int currentBigBarEndTimeIndex) {
        return (currentBigBarEndTimeIndex == endTimesGrid.get(timeframe).size() - 1) && (endTimesGrid.get(timeframe).size() > 1)
                ? Duration.between(
                endTimesGrid.get(timeframe).get(currentBigBarEndTimeIndex - 1),
                LocalTime.of(23, 59, 59, 999999999)).plusNanos(1)
                : timeframesDurations.get(timeframe);
    }

    private void fillEndTimesGrid(String timeframe) {
        Duration interval = timeframesDurations.get(timeframe);
        LocalTime startOfBigBar = LocalTime.MIN;
        LocalTime endOfDay = LocalTime.MAX;
        while (startOfBigBar.isBefore(endOfDay)) {
            LocalTime endOfBigBar = startOfBigBar.plus(interval);
            if (endOfBigBar.equals(LocalTime.MIN) && !endTimesGrid.get(timeframe).isEmpty()) {
                endTimesGrid.get(timeframe).add(LocalTime.MIN);
                break;
            }
            if (isBigBarEndCrossMidnight(timeframe,endOfBigBar)) {
                endTimesGrid.get(timeframe).add(LocalTime.MIN);
                break;
            }
            endTimesGrid.get(timeframe).add(endOfBigBar);
            startOfBigBar = endOfBigBar;
        }
    }
    private boolean isBigBarEndCrossMidnight(String timeframe,LocalTime endOfBigBar) {
        if (endTimesGrid.get(timeframe).isEmpty()) {
            return false;
        }
        LocalTime previousBigBarEndTime = endTimesGrid.get(timeframe).get(endTimesGrid.get(timeframe).size() - 1);
        return endOfBigBar.isBefore(previousBigBarEndTime);
    }
}
