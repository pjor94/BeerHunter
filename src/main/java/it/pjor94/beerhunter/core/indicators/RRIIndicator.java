package it.pjor94.beerhunter.core.indicators;

import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.CachedIndicator;
import org.ta4j.core.num.Num;

public class RRIIndicator extends CachedIndicator<Num> {
    public RRIIndicator(BarSeries series) {
        super(series);
    }

    @Override
    protected Num calculate(int index) {
        Bar lastBar = getBarSeries().getBar(index);
        Num quote  =lastBar.getClosePrice().min(lastBar.getClosePrice());
        Num base  =lastBar.getHighPrice().min(lastBar.getLowPrice());
        return quote.dividedBy(base);
    }
}
