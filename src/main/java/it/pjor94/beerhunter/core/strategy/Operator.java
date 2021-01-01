package it.pjor94.beerhunter.core.strategy;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Operator {
    GTE(">="), LTE("<="), AND("AND"), OR("OR"), EQ("=");
    private final String operator;
    public static Operator get(String op) {
        switch (op) {
            case ">=":
                return GTE;
            case "<=":
                return LTE;
            case "OR":
                return OR;
            case "AND":
                return AND;
            default:
                throw new IllegalArgumentException(op);
        }
    }
}
