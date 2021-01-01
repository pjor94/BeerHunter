package it.pjor94.beerhunter.core.events.processor;

import it.pjor94.beerhunter.core.Core;
import it.pjor94.beerhunter.core.events.event.BarEvent;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class BarEventProcessor implements ApplicationListener<BarEvent> {
    Logger log = LoggerFactory.getLogger(BarEventProcessor.class);

    @Autowired
    protected Core core;

    @SneakyThrows
    @Async
    @Override
    public void onApplicationEvent(BarEvent event) {
        log.info("NEW EVENT");
        core.newCandleEvent(event.getPair(),event.getTimeFrame(),event.getBar(),event.getBarSeries());
    }
}