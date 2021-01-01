package it.pjor94.beerhunter.core.strategy;

import lombok.SneakyThrows;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Strategy;

public class BackTester {
    String strategyStr;
    BarSeries series;
    Strategy strategy;

    @SneakyThrows
    BackTester(String strategyStr, BarSeries series){
        this.strategyStr=strategyStr;
        this.series =series;
        this.strategy = new BHStrategyParser().parse(strategyStr,this.series);
    }
}
