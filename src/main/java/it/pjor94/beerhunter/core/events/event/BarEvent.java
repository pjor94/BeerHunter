package it.pjor94.beerhunter.core.events.event;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;
import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;

public class BarEvent extends ApplicationEvent {
    private static final long serialVersionUID = 1L;
    @Getter
    @Setter
    private String pair;
    @Getter
    @Setter
    private String timeFrame;
    @Getter
    @Setter
    private Bar bar;
    @Getter
    @Setter
    private BarSeries barSeries;

    public BarEvent(Object source, String pair, String timeFrame, Bar bar, BarSeries barSeries) {
        super(source);
        this.pair =pair;
        this.timeFrame = timeFrame;
        this.bar = bar;
        this.barSeries = barSeries;
    }

}