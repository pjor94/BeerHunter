package it.pjor94.beerhunter.core.strategy;

import it.pjor94.beerhunter.core.trading.BHTradingRecord;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ta4j.core.*;
import org.ta4j.core.num.DoubleNum;
import org.ta4j.core.num.Num;

public class BHStrategy implements Strategy {

    /** The logger */
    protected final Logger log = LoggerFactory.getLogger(getClass());

    /** The class name */
    private final String className = getClass().getSimpleName();

    /** Name of the strategy */
    private String name;

    /** The entry rule */
    private Rule entryRule;

    /** The exit rule */
    private Rule exitRule;

    @Setter @Getter
    private Num quoteAssetAmount = DoubleNum.valueOf(0);
    @Setter @Getter
    private Num baseAssetAmount = DoubleNum.valueOf(0);
    @Setter @Getter
    private Num tradeAmount = DoubleNum.valueOf(0);

    /**
     * The unstable period (number of bars).<br>
     * During the unstable period of the strategy any order placement will be
     * cancelled.<br>
     * I.e. no entry/exit signal will be fired before index == unstablePeriod.
     */
    private int unstablePeriod;

    /**
     * Constructor.
     *
     * @param entryRule the entry rule
     * @param exitRule  the exit rule
     */
    public BHStrategy(Rule entryRule, Rule exitRule) {
        this(null, entryRule, exitRule, 0);
    }

    /**
     * Constructor.
     *
     * @param entryRule      the entry rule
     * @param exitRule       the exit rule
     * @param unstablePeriod strategy will ignore possible signals at
     *                       <code>index</code> < <code>unstablePeriod</code>
     */
    public BHStrategy(Rule entryRule, Rule exitRule, int unstablePeriod) {
        this(null, entryRule, exitRule, unstablePeriod);
    }

    /**
     * Constructor.
     *
     * @param name      the name of the strategy
     * @param entryRule the entry rule
     * @param exitRule  the exit rule
     */
    public BHStrategy(String name, Rule entryRule, Rule exitRule) {
        this(name, entryRule, exitRule, 0);
    }

    /**
     * Constructor.
     *
     * @param name           the name of the strategy
     * @param entryRule      the entry rule
     * @param exitRule       the exit rule
     * @param unstablePeriod strategy will ignore possible signals at
     *                       <code>index</code> < <code>unstablePeriod</code>
     */
    public BHStrategy(String name, Rule entryRule, Rule exitRule, int unstablePeriod) {
        if (entryRule == null || exitRule == null) {
            throw new IllegalArgumentException("Rules cannot be null");
        }
        if (unstablePeriod < 0) {
            throw new IllegalArgumentException("Unstable period bar count must be >= 0");
        }
        this.name = name;
        this.entryRule = entryRule;
        this.exitRule = exitRule;
        this.unstablePeriod = unstablePeriod;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Rule getEntryRule() {
        return entryRule;
    }

    @Override
    public Rule getExitRule() {
        return exitRule;
    }

    @Override
    public int getUnstablePeriod() {
        return unstablePeriod;
    }

    @Override
    public void setUnstablePeriod(int unstablePeriod) {
        this.unstablePeriod = unstablePeriod;
    }

    @Override
    public boolean isUnstableAt(int index) {
        return index < unstablePeriod;
    }


    public boolean shouldEnter(int index, BHTradingRecord tradingRecord) {
        boolean enter = Strategy.super.shouldEnter(index, tradingRecord);
        traceShouldEnter(index, enter);
        tradingRecord.getLastTrade();
        return enter;
    }


    public boolean shouldExit(int index, BHTradingRecord tradingRecord) {
        boolean exit = Strategy.super.shouldExit(index, tradingRecord);
        traceShouldExit(index, exit);
        return exit;
    }

    @Override
    public Strategy and(org.ta4j.core.Strategy strategy) {
        String andName = "and(" + name + "," + strategy.getName() + ")";
        int unstable = Math.max(unstablePeriod, strategy.getUnstablePeriod());
        return and(andName, strategy, unstable);
    }

    @Override
    public Strategy or(org.ta4j.core.Strategy strategy) {
        String orName = "or(" + name + "," + strategy.getName() + ")";
        int unstable = Math.max(unstablePeriod, strategy.getUnstablePeriod());
        return or(orName, strategy, unstable);
    }

    @Override
    public Strategy opposite() {
        return new BHStrategy("opposite(" + name + ")", exitRule, entryRule, unstablePeriod);
    }

    @Override
    public Strategy and(String name, org.ta4j.core.Strategy strategy, int unstablePeriod) {
        return  new BHStrategy(name, entryRule.and(strategy.getEntryRule()), exitRule.and(strategy.getExitRule()),
                unstablePeriod);
    }

    @Override
    public Strategy or(String name, org.ta4j.core.Strategy strategy, int unstablePeriod) {
        return new BHStrategy(name, entryRule.or(strategy.getEntryRule()), exitRule.or(strategy.getExitRule()),
                unstablePeriod);
    }

    /**
     * @param index         the bar index
     * @param tradingRecord the potentially needed trading history
     * @return true to recommend an order, false otherwise (no recommendation)
     */
    @Override
    public boolean shouldOperate(int index, BHTradingRecord tradingRecord) {
        return shouldEnter(index, tradingRecord) || shouldExit(index, tradingRecord);
    }

    /**
     * Traces the shouldEnter() method calls.
     *
     * @param index the bar index
     * @param enter true if the strategy should enter, false otherwise
     */
    protected void traceShouldEnter(int index, boolean enter) {
        log.trace(">>> {}#shouldEnter({}): {}", className, index, enter);
    }

    /**
     * Traces the shouldExit() method calls.
     *
     * @param index the bar index
     * @param exit  true if the strategy should exit, false otherwise
     */
    protected void traceShouldExit(int index, boolean exit) {
        log.trace(">>> {}#shouldExit({}): {}", className, index, exit);
    }
}
