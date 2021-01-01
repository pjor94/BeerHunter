package it.pjor94.beerhunter.core.report;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelColumn {
    String name();

    String numberFormat() default "General";
} 