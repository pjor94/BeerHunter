package it.pjor94.beerhunter.core.strategy;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class ParserError {

    @Getter
    int line;
    @Getter
    int charPositionInLine;
    @Getter
    String msg;


    public ParserError(String msg) {
        this(0, 0, msg);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("ParseError[");
        if (line > 0) builder.append(line).append(",");
        if (charPositionInLine > 0) builder.append(charPositionInLine).append(",");
        if (msg != null) builder.append(msg);
        builder.append("]");
        return builder.toString();
    }
}
