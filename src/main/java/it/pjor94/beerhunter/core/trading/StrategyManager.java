package it.pjor94.beerhunter.core.trading;

import it.pjor94.beerhunter.core.CacheManager;
import it.pjor94.beerhunter.core.strategy.*;
import it.pjor94.parser.StrategyLexer;
import it.pjor94.parser.StrategyParser;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CodePointCharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ta4j.core.*;
import org.ta4j.core.cost.CostModel;
import org.ta4j.core.cost.ZeroCostModel;
import org.ta4j.core.num.Num;

import java.util.List;

public class StrategyManager  {
    Logger log = LoggerFactory.getLogger(StrategyManager.class);

    private Strategy strategy;
    @Getter @Setter
    private BarSeries barSeries;

    /** The trading cost models */
    @Getter @Setter
    private CostModel transactionCostModel;
    @SneakyThrows
    public void setStrategy(String strategy){
        this.setStrategy(parseStrategy(strategy,barSeries));
    }
    public void setStrategy(Strategy strategy){
        this.strategy=strategy;
    }

    private Strategy parseStrategy(String strategy,BarSeries timeSeries) throws ParserException {
        ErrorListener errorListener = new ErrorListener();
        CodePointCharStream stream = CharStreams.fromString(strategy);
        StrategyLexer lexer = new StrategyLexer(stream);
        lexer.removeErrorListeners();
        lexer.addErrorListener(errorListener);
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        StrategyParser taParser = new StrategyParser(tokenStream);
        taParser.removeErrorListeners();
        taParser.addErrorListener(errorListener);
        try {
            StrategyParser.StrategyContext context = taParser.strategy();
            if (!errorListener.getErrors().isEmpty()) {
                throw new ParserException(errorListener.getErrors());
            }
            BHStrategyListenerImpl listener = new IndicatorsStrategyListener(timeSeries);
            ParseTreeWalker walker = new ParseTreeWalker();
            walker.walk(listener, context);
            List<ParserError> errors = listener.getErrors();
            if (CollectionUtils.isNotEmpty(errors)) {
                throw new ParserException(errors);
            }
            return listener.getStrategy();
        } catch (ParserException e) {
            throw e;
        } catch (Throwable e) {
            throw new ParserException(e);
        }
    }
    public StrategyManager(BarSeries barSeries) {
        this(barSeries, new ZeroCostModel());
    }
    public StrategyManager(Strategy strategy, BarSeries barSeries, CostModel transactionCostModel) {
        this(barSeries, transactionCostModel);
        this.strategy = strategy;
    }
    public StrategyManager(BarSeries barSeries, CostModel transactionCostModel) {
        this.barSeries = barSeries;
        this.transactionCostModel = transactionCostModel;
    }
    @SneakyThrows
    public StrategyManager(String strategy, BarSeries barSeries, CostModel transactionCostModel) {
        this(barSeries, transactionCostModel);
        this.strategy= parseStrategy(strategy,barSeries);
    }

    public TradingRecord run (){
        return run( Order.OrderType.BUY);
    }
    public TradingRecord run( int startIndex, int finishIndex) {
        return run( Order.OrderType.BUY, barSeries.numOf(1), startIndex, finishIndex);
    }
    public TradingRecord run( Order.OrderType orderType) {
        return run( orderType, barSeries.numOf(1));
    }
    public TradingRecord run( Order.OrderType orderType, int startIndex, int finishIndex) {
        return run( orderType, barSeries.numOf(1), startIndex, finishIndex);
    }
    public TradingRecord run( Order.OrderType orderType, Num amount) {
        return run( orderType, amount, barSeries.getBeginIndex(), barSeries.getEndIndex());
    }

    public TradingRecord run( Order.OrderType orderType, Num amount, int startIndex, int finishIndex) {
        int runBeginIndex = Math.max(startIndex, barSeries.getBeginIndex());
        int runEndIndex = Math.min(finishIndex, barSeries.getEndIndex());
        log.trace("Running strategy (indexes: {} -> {}): {} (starting with {})", runBeginIndex, runEndIndex, strategy, orderType);
        BHTradingRecord tradingRecord = new BHTradingRecord(orderType, transactionCostModel, new ZeroCostModel());
        for (int i = runBeginIndex; i <= runEndIndex; i++) {
            // For each bar between both indexes...
            if (strategy.shouldOperate(i, tradingRecord)) {
                tradingRecord.operate(i, barSeries.getBar(i).getClosePrice(), amount);
            }
        }

        if (!tradingRecord.isClosed()) {
            // If the last trade is still opened, we search out of the run end index.
            // May works if the end index for this run was inferior to the actual number of
            // bars
            int seriesMaxSize = Math.max(barSeries.getEndIndex() + 1, barSeries.getBarData().size());
            for (int i = runEndIndex + 1; i < seriesMaxSize; i++) {
                // For each bar after the end index of this run...
                // --> Trying to close the last trade
                if (strategy.shouldOperate(i, tradingRecord)) {
                    tradingRecord.operate(i, barSeries.getBar(i).getClosePrice(), amount);
                    break;
                }
            }
        }
        return tradingRecord;
    }

}
