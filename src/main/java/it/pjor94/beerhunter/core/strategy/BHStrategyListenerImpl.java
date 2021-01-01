package it.pjor94.beerhunter.core.strategy;

import lombok.Getter;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Indicator;
import org.ta4j.core.Rule;
import org.ta4j.core.Strategy;
import org.ta4j.core.num.Num;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import it.pjor94.parser.StrategyBaseListener;
import it.pjor94.parser.StrategyParser;

/**
 * This class provides an implementation of {@link StrategyListener},
 */
public abstract class BHStrategyListenerImpl extends StrategyBaseListener
{

    protected Deque<Rule> ruleStack = new LinkedList<>();
    protected Deque<Indicator<Num>> indicatorStack = new LinkedList<>();
    protected Deque<Integer> timeFrameStack = new LinkedList<>();
    protected Deque<Operator> operatorStack = new LinkedList<>();
    @Getter
    private Strategy strategy;
    @Getter
    private List<ParserError> errors = new ArrayList<>();
    protected void addError(ParserError error)
    {
        errors.add(error);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     */
    @Override
    public void exitStrategy(StrategyParser.StrategyContext ctx)
    {
        try
        {
            Rule exitRule = ruleStack.pop();
            Rule enterRule = ruleStack.pop();
            strategy = new BHStrategy(enterRule, exitRule);
        }
        catch (Exception e)
        {
            addError(new ParserError("Rules are not defined correctly".concat(e.getMessage() != null ? e.getMessage() : "")));
        }
    }


    @Override
    public void exitLogicExpression(StrategyParser.LogicExpressionContext ctx)
    {
        Rule simpleRule = null;
        Rule right = ruleStack.pop();
        Rule left = ruleStack.pop();
        Operator operator = operatorStack.pop();
        switch (operator)
        {
            case AND:
                simpleRule = left.and(right);
                break;
            case OR:
                simpleRule = left.or(right);
                break;
        }
        ruleStack.push(simpleRule);
    }


    @Override
    public void exitOp(StrategyParser.OpContext ctx)
    {
        operatorStack.push(Operator.get(ctx.getText()));
    }


    @Override
    public void exitLogicOp(StrategyParser.LogicOpContext ctx)
    {
        operatorStack.push(Operator.get(ctx.getText()));
    }


    @Override
    public void exitTimeframe(StrategyParser.TimeframeContext ctx)
    {
        Integer timeFrame = Integer.parseInt(ctx.getText());
        timeFrameStack.push(timeFrame % 700);
    }


    @Override
    public void visitErrorNode(ErrorNode node)
    {
        errors.add(new ParserError(node.getText()));
    }

}

