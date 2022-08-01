package it.pjor94.beerhunter.core.indicators;

import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.Num;

public class SmaShift extends SMAIndicator {
    private final int shiftPeriod;
    private final int barCount;
    private final Indicator<Num> indicator;


    public int getShiftPeriod(int index) {
       return Math.max(0, index - shiftPeriod);
    }

    public SmaShift(Indicator<Num> indicator, int barCount, int shiftPeriod) {
        super(indicator,barCount);
        this.shiftPeriod = shiftPeriod;
        this.barCount = barCount;
        this.indicator = indicator;
    }

    @Override
    protected Num calculate(int index) {
        Num sum = numOf(0);
        for (int i = Math.max(0, getShiftPeriod(index) - barCount + 1); i <= getShiftPeriod(index); i++) {
            sum = sum.plus(indicator.getValue(i));
        }
        final int realBarCount = Math.min(barCount, getShiftPeriod(index) + 1);
        return sum.dividedBy(numOf(realBarCount));
    }
}