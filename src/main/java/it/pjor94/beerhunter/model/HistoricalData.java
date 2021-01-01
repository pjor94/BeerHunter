package it.pjor94.beerhunter.model;

import lombok.Getter;
import lombok.Setter;

public class HistoricalData {

    @Getter @Setter
    private String id;
    @Getter @Setter
    private String pair;
    @Getter @Setter
    private Long start;
    @Getter @Setter
    private Long end;
    @Getter @Setter
    private String status;
}
