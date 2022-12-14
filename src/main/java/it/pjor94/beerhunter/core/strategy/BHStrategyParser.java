package it.pjor94.beerhunter.core.strategy;

import it.pjor94.parser.StrategyLexer;
import it.pjor94.parser.StrategyParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CodePointCharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.apache.commons.collections.CollectionUtils;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Strategy;

import java.util.List;

public class BHStrategyParser
{

    public Strategy parse(String strategy, BarSeries timeSeries) throws ParserException
    {
        ErrorListener errorListener = new ErrorListener();
        CodePointCharStream stream = CharStreams.fromString(strategy);
        StrategyLexer lexer = new StrategyLexer(stream);
        lexer.removeErrorListeners();
        lexer.addErrorListener(errorListener);
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        StrategyParser taParser = new StrategyParser(tokenStream);
        taParser.removeErrorListeners();
        taParser.addErrorListener(errorListener);
        try
        {
            StrategyParser.StrategyContext context = taParser.strategy();
            if (!errorListener.getErrors().isEmpty())
            {
                throw new ParserException(errorListener.getErrors());
            }
            BHStrategyListenerImpl listener = new IndicatorsStrategyListener(timeSeries);
            ParseTreeWalker walker = new ParseTreeWalker();

            walker.walk(listener, context);
            List<ParserError> errors = listener.getErrors();
            if (CollectionUtils.isNotEmpty(errors))
            {
                throw new ParserException(errors);
            }
            return listener.getStrategy();
        } catch (ParserException e) {
            throw e;
        } catch (Throwable e) {
            throw new ParserException(e);
        }
    }
}
