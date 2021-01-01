package it.pjor94.beerhunter;

import it.pjor94.beerhunter.core.bar.BarSeriesGenerator;
import it.pjor94.beerhunter.core.bar.CandelstickList;
import it.pjor94.beerhunter.core.bar.MultiBarSeries;
import it.pjor94.beerhunter.core.report.ExcelReportService;
import it.pjor94.beerhunter.core.report.ExcelReportUtils;
import it.pjor94.beerhunter.core.report.OrderReport;
import it.pjor94.beerhunter.core.strategy.BHStrategyParser;
import it.pjor94.beerhunter.core.strategy.ParserException;
import it.pjor94.beerhunter.core.trading.BHTradingRecord;
import it.pjor94.beerhunter.core.trading.StrategyManager;
import it.pjor94.beerhunter.repository.CandlestickRepository;
import it.pjor94.beerhunter.service.HistoricalDataService;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.ta4j.core.*;
import org.ta4j.core.analysis.criteria.TotalProfitCriterion;
import org.ta4j.core.cost.LinearTransactionCostModel;
import org.ta4j.core.cost.ZeroCostModel;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class BeerHunterApplicationTests {

	@Autowired
	private HistoricalDataService historicalDataService;
	@Autowired
	private CandlestickRepository candlestickRepository;
	@Autowired
	private ExcelReportService excelReportService;

	interface Tr extends TradingRecord{}
	@Test
	void contextLoads() throws ParserException {
		String pair = "BTCUSDT";
		String timeframe = "5m";
		String strategyString = "(RSI[7] <= 20) [GO_LONG] ; (RSI[7] >= 80) [GO_SHORT]";
		String newStrategy = "((RSI[7] <= 20) AND (CMF[21] <= -0.2)) [GO_LONG] ; ((RSI[7] >= 80) AND (CMF[21] >= 0.6))[GO_SHORT]";
		CandelstickList candlesticks = new CandelstickList(candlestickRepository.findByPairOrderByOpenTimeDesc(pair, PageRequest.of(0, 100000000)).getContent());
		MultiBarSeries series = new BarSeriesGenerator(pair, candlesticks, null);
		BarSeries chosenBarSeries = series.getBarSeries(timeframe);
		StrategyManager manager = new StrategyManager(strategyString,chosenBarSeries,new LinearTransactionCostModel(0.0005));
//		Strategy strategy = new BHStrategyParser().parse(newStrategy,chosenBarSeries);
//		BarSeriesManager seriesManager = new BarSeriesManager(chosenBarSeries,new LinearTransactionCostModel(0.0005),new ZeroCostModel());


		TradingRecord tradingRecord = manager.run();
		//BaseTradingRecord
		excelReportService.createBacktestingReport(tradingRecord,chosenBarSeries);

//		ChaikinMoneyFlowIndicator flowIndicator = new ChaikinMoneyFlowIndicator(chosenBarSeries,21);
		System.out.println(chosenBarSeries.getSeriesPeriodDescription());
		System.out.println("Total profit for the strategy: " + new TotalProfitCriterion().calculate(chosenBarSeries, tradingRecord));
	}

}
