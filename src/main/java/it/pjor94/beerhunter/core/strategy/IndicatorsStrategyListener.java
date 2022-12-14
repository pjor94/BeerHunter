package it.pjor94.beerhunter.core.strategy;

import it.pjor94.beerhunter.core.indicators.RRIIndicator;
import it.pjor94.beerhunter.core.indicators.SmaShift;
import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.*;
import org.ta4j.core.indicators.adx.ADXIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsLowerIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsMiddleIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsUpperIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.helpers.ConstantIndicator;
import org.ta4j.core.indicators.helpers.VolumeIndicator;
import org.ta4j.core.indicators.pivotpoints.PivotPointIndicator;
import org.ta4j.core.indicators.pivotpoints.TimeLevel;
import org.ta4j.core.indicators.statistics.StandardDeviationIndicator;
import org.ta4j.core.indicators.volume.*;
import org.ta4j.core.num.DoubleNum;
import org.ta4j.core.num.Num;
import org.ta4j.core.trading.rules.CrossedDownIndicatorRule;
import org.ta4j.core.trading.rules.CrossedUpIndicatorRule;
import org.ta4j.core.trading.rules.OverIndicatorRule;
import org.ta4j.core.trading.rules.UnderIndicatorRule;
import it.pjor94.parser.StrategyParser;

public class IndicatorsStrategyListener extends BHStrategyListenerImpl {

  private final BarSeries barSeries;
  private final ClosePriceIndicator closePrice;
  private final VolumeIndicator volume;

  public IndicatorsStrategyListener(BarSeries barSeries) {
    assert barSeries != null : "BarSeries cannot be null";    
    this.barSeries = barSeries;
    this.closePrice = new ClosePriceIndicator(barSeries);
    this.volume = new VolumeIndicator(barSeries);
  }

  @Override
  public void exitSimpleExpression(StrategyParser.SimpleExpressionContext ctx) {
      Indicator<Num> right = indicatorStack.pop();
      Indicator<Num> left = indicatorStack.pop();
      Operator operator = operatorStack.pop();
      switch (operator) {
        case GTE:
          ruleStack.push(new OverIndicatorRule(left, right));
          break;
        case LTE:
          ruleStack.push(new UnderIndicatorRule(left, right));
          break;
      }
  }

  @Override
  public void exitConstantExpression(StrategyParser.ConstantExpressionContext ctx) {
    try {
      Indicator<Num> right = indicatorStack.pop();
      Indicator<Num> left = indicatorStack.pop();
      Operator operator = operatorStack.pop();
      switch (operator) {
        case GTE:
          ruleStack.push(new CrossedUpIndicatorRule(left, right));
          break;
        case LTE:
          ruleStack.push(new CrossedDownIndicatorRule(left, right));
          break;
      }
    } catch (Exception e) {
      //addError(ParserError.builder().msg(e.getMessage()).build());
    }
  }


  @Override
  public void exitEma(StrategyParser.EmaContext ctx) {
    Integer timeFrame = timeFrameStack.pop();
    indicatorStack.push(new EMAIndicator(closePrice, timeFrame));
  }

  @Override
  public void exitSma(StrategyParser.SmaContext ctx) {
    Integer timeFrame = timeFrameStack.pop();
    indicatorStack.push(new SMAIndicator(closePrice, timeFrame));
  }

  @Override
  public void exitDema(StrategyParser.DemaContext ctx) {
    Integer timeFrame = timeFrameStack.pop();
    indicatorStack.push(new DoubleEMAIndicator(closePrice, timeFrame));
  }

  @Override
  public void exitTema(StrategyParser.TemaContext ctx) {
    Integer timeFrame = timeFrameStack.pop();
    indicatorStack.push(new TripleEMAIndicator(closePrice, timeFrame));
  }

  @Override
  public void exitWilliams(StrategyParser.WilliamsContext ctx) {
    Integer timeFrame = timeFrameStack.pop();
    indicatorStack.push(new WilliamsRIndicator(barSeries, timeFrame));
  }

  @Override
  public void exitWma(StrategyParser.WmaContext ctx) {
    Integer timeFrame = timeFrameStack.pop();
    indicatorStack.push(new WMAIndicator(closePrice, timeFrame));
  }

  @Override
  public void exitCci(StrategyParser.CciContext ctx) {
    Integer timeFrame = timeFrameStack.pop();
    indicatorStack.push(new CCIIndicator(barSeries, timeFrame));

  }

  @Override
  public void exitRsi(StrategyParser.RsiContext ctx) {
    Integer timeFrame = timeFrameStack.pop();
    indicatorStack.push(new RSIIndicator(closePrice, timeFrame));
  }

  @Override
  public void exitSrsi(StrategyParser.SrsiContext ctx) {
    Integer timeFrame = timeFrameStack.pop();
    indicatorStack.push(new StochasticRSIIndicator(closePrice, timeFrame));
  }

  @Override
  public void exitBbm(StrategyParser.BbmContext ctx) {
    Integer timeFrame = timeFrameStack.pop();
    indicatorStack.push(new BollingerBandsMiddleIndicator(new SMAIndicator(closePrice, timeFrame)));
  }

  @Override
  public void exitBbu(StrategyParser.BbuContext ctx) {
    Integer timeFrame = timeFrameStack.pop();
    var bbm = new BollingerBandsMiddleIndicator(new SMAIndicator(closePrice, timeFrame));
    StandardDeviationIndicator sd = new StandardDeviationIndicator(closePrice, timeFrame);
    indicatorStack.push(new BollingerBandsUpperIndicator(bbm, sd));
  }

  @Override
  public void exitBbl(StrategyParser.BblContext ctx) {
    Integer timeFrame = timeFrameStack.pop();
    var bbm = new BollingerBandsMiddleIndicator(new SMAIndicator(closePrice, timeFrame));
    StandardDeviationIndicator sd = new StandardDeviationIndicator(closePrice, timeFrame);
    indicatorStack.push(new BollingerBandsLowerIndicator(bbm, sd));
  }

  @Override
  public void exitCprice(StrategyParser.CpriceContext ctx) {
    indicatorStack.push(closePrice);
  }

  @Override
  public void exitVolume(StrategyParser.VolumeContext ctx) {
    indicatorStack.push(volume);
  }

  @Override
  public void exitUi(StrategyParser.UiContext ctx) {
    Integer timeFrame = timeFrameStack.pop();
    indicatorStack.push(new UlcerIndexIndicator(closePrice, timeFrame));
  }

  @Override
  public void exitAdx(StrategyParser.AdxContext ctx) {
    Integer timeFrame = timeFrameStack.pop();
    indicatorStack.push(new ADXIndicator(barSeries, timeFrame));
  }

  @Override
  public void exitHma(StrategyParser.HmaContext ctx) {
    Integer timeFrame = timeFrameStack.pop();
    indicatorStack.push(new HMAIndicator(closePrice, timeFrame));
  }

  @Override
  public void exitZlema(StrategyParser.ZlemaContext ctx) {
    Integer timeFrame = timeFrameStack.pop();
    indicatorStack.push(new ZLEMAIndicator(closePrice, timeFrame));
  }

  @Override
  public void exitKama(StrategyParser.KamaContext ctx) {
    Integer timeFrame3 = timeFrameStack.pop();
    Integer timeFrame2 = timeFrameStack.pop();
    Integer timeFrame1 = timeFrameStack.pop();
    indicatorStack.push(new KAMAIndicator(closePrice, timeFrame1, timeFrame2, timeFrame3));
  }

  @Override
  public void exitMacd(StrategyParser.MacdContext ctx) {
    Integer timeFrame2 = timeFrameStack.pop();
    Integer timeFrame1 = timeFrameStack.pop();
    indicatorStack.push(new MACDIndicator(closePrice, timeFrame1, timeFrame2));
  }

  @Override
  public void exitPpo(StrategyParser.PpoContext ctx) {
    Integer timeFrame2 = timeFrameStack.pop();
    Integer timeFrame1 = timeFrameStack.pop();
      indicatorStack.push(new PPOIndicator(closePrice, timeFrame1, timeFrame2));
  }

  @Override
  public void exitVwap(StrategyParser.VwapContext ctx) {
    Integer timeFrame = timeFrameStack.pop();
    indicatorStack.push(new VWAPIndicator(barSeries, timeFrame));
  }

  @Override
  public void exitCoi(StrategyParser.CoiContext ctx) {
    Integer timeFrame2 = timeFrameStack.pop();
    Integer timeFrame1 = timeFrameStack.pop();
    indicatorStack.push(new ChaikinOscillatorIndicator(barSeries, timeFrame1, timeFrame2));
  }

  @Override
  public void exitAdi(StrategyParser.AdiContext ctx) {
    indicatorStack.push(new AccumulationDistributionIndicator(barSeries));
  }

  @Override
  public void exitIii(StrategyParser.IiiContext ctx) {
    indicatorStack.push(new IIIIndicator(barSeries));
  }

  @Override
  public void exitCmf(StrategyParser.CmfContext ctx) {
    Integer timeFrame = timeFrameStack.pop();
    indicatorStack.push(new ChaikinMoneyFlowIndicator(barSeries, timeFrame));
  }

  @Override
  public void exitMvwap(StrategyParser.MvwapContext ctx) {
    Integer timeFrame2 = timeFrameStack.pop();
    Integer timeFrame1 = timeFrameStack.pop();
    VWAPIndicator vwap = new VWAPIndicator(barSeries, timeFrame1);
    indicatorStack.push(new MVWAPIndicator(vwap, timeFrame2));
  }

  @Override
  public void enterNvi(StrategyParser.NviContext ctx) {
    indicatorStack.push(new NVIIndicator(barSeries));
  }

  @Override
  public void exitRocv(StrategyParser.RocvContext ctx) {
    Integer timeFrame = timeFrameStack.pop();
    indicatorStack.push(new ROCVIndicator(barSeries, timeFrame));

  }

  @Override
  public void exitPvi(StrategyParser.PviContext ctx) {
    indicatorStack.push(new PVIIndicator(barSeries));
  }

  @Override
  public void exitObv(StrategyParser.ObvContext ctx) {
    indicatorStack.push(new OnBalanceVolumeIndicator(barSeries));
  }

  @Override
  public void exitRri(StrategyParser.RriContext ctx) {
    indicatorStack.push(new RRIIndicator(barSeries) );
  }

  @Override
  public void exitSmashift(StrategyParser.SmashiftContext ctx) {
    int timeframe = timeFrameStack.pop();
    int shifTime  =  timeFrameStack.pop();
    indicatorStack.push(new SmaShift(closePrice, timeframe,shifTime));
  }

  @Override
  public void exitPivot(StrategyParser.PivotContext ctx) {
    Integer timeframe = timeFrameStack.pop();
    indicatorStack.push(new PivotPointIndicator(barSeries,TimeLevel.values()[timeframe]));
  }

  @Override
  public void exitValue(StrategyParser.ValueContext ctx) {
    indicatorStack.push(new ConstantIndicator<>(barSeries, DoubleNum.valueOf(ctx.getText())));
  }

}
