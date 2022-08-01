package it.pjor94.beerhunter.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.springframework.data.annotation.AccessType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.math.BigDecimal;

import static org.springframework.data.mongodb.core.mapping.FieldType.DECIMAL128;
import static org.springframework.data.mongodb.core.mapping.FieldType.STRING;

@ToString
@Document
public class Candlestick  {

    @Id
    @BsonProperty("id")
    @Getter @Setter
    private String id;
    @BsonProperty("pair")
    @Setter @Getter
    private String pair;
    @BsonProperty("openTime")
    @Getter @Setter
    private Long openTime;
    @Getter()
    @Setter()
    @AccessType(AccessType.Type.FIELD)
    @Field(targetType = STRING)
    private BigDecimal open;
    @BsonProperty("high")
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
