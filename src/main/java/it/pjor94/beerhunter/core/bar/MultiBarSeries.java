package it.pjor94.beerhunter.core.bar;

import it.pjor94.beerhunter.model.Candlestick;
import org.ta4j.core.BarSeries;

import java.util.HashMap;

public interface MultiBarSeries {
    String getPair();
    HashMap<String, BarSeries> getBarSeries();
    BarSeries getBarSeries(String timeframe);
    void addBar(Candlestick candlestick);
}
