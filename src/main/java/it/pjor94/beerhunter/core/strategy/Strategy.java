package it.pjor94.beerhunter.core.strategy;

import it.pjor94.beerhunter.core.trading.BHTradingRecord;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.TradingRecord;
import org.ta4j.core.num.Num;

public interface Strategy extends org.ta4j.core.Strategy {

    @Override
    Strategy or(String name, org.ta4j.core.Strategy strategy, int unstablePeriod);
    Num getBaseAssetAmount();
    Num getQuoteAssetAmount();
    Num getTradeAmount();
    void setBaseAssetAmount(Num assetAmount);
    void setQuoteAssetAmount(Num assetAmount);
    void setTradeAmount(Num amount);
    boolean shouldOperate(int index, BHTradingRecord tradingRecord);

}




