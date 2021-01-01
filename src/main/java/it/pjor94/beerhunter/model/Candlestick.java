package it.pjor94.beerhunter.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;

@ToString
public class Candlestick  {

    @Id
    @Getter @Setter
    private String id;
    @Setter @Getter
    private String pair;
    @Getter @Setter
    private Long openTime;
    @Getter @Setter
    private BigDecimal open;
    @Getter @Setter
    private BigDecimal high;
    @Getter @Setter
    private BigDecimal low;
    @Getter @Setter
    private BigDecimal close;
    @Getter @Setter
    private BigDecimal volume;
    @Getter @Setter
    private Long closeTime;
    @Getter @Setter
    private String quoteAssetVolume;
    @Getter @Setter
    private Long numberOfTrades;
    @Getter @Setter
    private String takerBuyBaseAssetVolume;
    @Getter @Setter
    private String takerBuyQuoteAssetVolume;

    public Candlestick(){}

    public Candlestick(String pair,com.binance.api.client.domain.market.Candlestick candlestick){
        this.setOpenTime(candlestick.getOpenTime());
        this.setOpen(new BigDecimal(candlestick.getOpen()));
        this.setLow(new BigDecimal(candlestick.getLow()));
        this.setHigh(new BigDecimal(candlestick.getHigh()));
        this.setClose(new BigDecimal(candlestick.getClose()));
        this.setCloseTime(candlestick.getCloseTime());
        this.setVolume(new BigDecimal(candlestick.getVolume()));
        this.setNumberOfTrades(candlestick.getNumberOfTrades());
        this.setQuoteAssetVolume(candlestick.getQuoteAssetVolume());
        this.setTakerBuyQuoteAssetVolume(candlestick.getTakerBuyQuoteAssetVolume());
        this.setTakerBuyBaseAssetVolume(candlestick.getTakerBuyQuoteAssetVolume());
        this.setPair(pair);
    }

    public Candlestick(String pair,com.binance.api.client.domain.event.CandlestickEvent candlestick){
        this.setOpenTime(candlestick.getOpenTime());
        this.setOpen(new BigDecimal(candlestick.getOpen()));
        this.setLow(new BigDecimal(candlestick.getLow()));
        this.setHigh(new BigDecimal(candlestick.getHigh()));
        this.setClose(new BigDecimal(candlestick.getClose()));
        this.setCloseTime(candlestick.getCloseTime());
        this.setVolume(new BigDecimal(candlestick.getVolume()));
        this.setNumberOfTrades(candlestick.getNumberOfTrades());
        this.setQuoteAssetVolume(candlestick.getQuoteAssetVolume());
        this.setTakerBuyQuoteAssetVolume(candlestick.getTakerBuyQuoteAssetVolume());
        this.setTakerBuyBaseAssetVolume(candlestick.getTakerBuyQuoteAssetVolume());
        this.setPair(pair);
    }
}
