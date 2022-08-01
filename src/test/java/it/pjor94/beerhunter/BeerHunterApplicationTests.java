package it.pjor94.beerhunter;

import com.mongodb.*;
import com.mongodb.client.FindIterable;
import it.pjor94.beerhunter.core.bar.BarSeriesGenerator;
import it.pjor94.beerhunter.core.bar.CandelstickList;
import it.pjor94.beerhunter.core.bar.MultiBarSeries;
import it.pjor94.beerhunter.core.report.ExcelReportService;
import it.pjor94.beerhunter.core.strategy.ParserException;
import it.pjor94.beerhunter.core.trading.BHTradingRecord;
import it.pjor94.beerhunter.core.trading.StrategyManager;
import it.pjor94.beerhunter.model.Candlestick;
import it.pjor94.beerhunter.repository.CandlestickRepository;
import it.pjor94.beerhunter.repository.ProvaRepo;
import it.pjor94.beerhunter.service.HistoricalDataService;
import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.ta4j.core.*;
import org.ta4j.core.analysis.criteria.TotalProfitCriterion;
import org.ta4j.core.cost.LinearTransactionCostModel;
import org.ta4j.core.num.DoubleNum;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@SpringBootTest
class BeerHunterApplicationTests {
    private static final Logger logger = LoggerFactory.getLogger(BeerHunterApplicationTests.class);

    @Autowired
	private HistoricalDataService historicalDataService;
	@Autowired
	private CandlestickRepository candlestickRepository;
	@Autowired
	private ExcelReportService excelReportService;

	@Autowired
    private MongoTemplate mongoTemplate;
	@Autowired
    private ProvaRepo provaRepo;

	interface Tr extends TradingRecord{}
	@Test
	void contextLoads() throws ParserException {
//		pairs.add("BTCUSDT");
//		pairs.add("ETHEUR");
//		pairs.add("GRTUSDT");
//		pairs.add("EOSUSDT");
//		pairs.add("XRPUSDT");
//		pairs.add("BCHUSDT");
//		pairs.add("ETHUSDT");

		String pair = "BTCUSDT";
		String timeframe = "4h";
		String strategyString = "(SRSI[14] <= 0.20) [GO_LONG] ; (SRSI[14] >= 0.80) [GO_SHORT]";
		String prova = " ((SRSI[14] <= 0.20) AND (EMA[50] >= EMA[200])) [GO_LONG] ; ((SRSI[14] >= 0.80) AND (EMA[50] >= EMA[200])) [GO_SHORT]";
		String TEST_POLLI = " ((SRSI[14]<=0.20) AND (EMA[50]>EMA[200])) [GO_LONG] ; ((SRSI[14]>=0.8) AND (EMA[50]>EMA[200])) [GO_SHORT]";//"    (SRSI[14] <= 0.20) [GO_LONG] ; (SRSI[14] >= 0.80) [GO_SHORT]";
		String BBL = "((BBL[20] <= CPRICE) AND  (SRSI[7] <= 0.22))[GO_LONG] ; ((BBM[20] >= CPRICE) AND (SRSI[7] >= 0.82)) [GO_SHORT]";
		String BBM = "((BBM[20] <= CPRICE) AND  (SRSI[7] <= 0.22))[GO_LONG] ; ((BBM[20] >= CPRICE) AND (SRSI[7] >= 0.82)) [GO_SHORT]";
		String BBMATTI = "(BBL[20]<=CPRICE) [GO_LONG] ; (BBU[20]>=CPRICE) [GO_SHORT]";
		String stochRsi = "((SRSI[7] <= 0.2) AND (CMF[21] <= -0.2)) [GO_LONG] ; ((SRSI[7] >= 0.8) AND (CMF[21] >= 0.6))[GO_SHORT]";
		String alebb = "((SRSI[7]<=0.22) AND (CPRICE<=BBL[20])) [GO_LONG] ; ((SRSI[7]>=0.88) AND (CPRICE>=BBU[20])) [GO_SHORT];";
//		String prova = "((CPRICE>PIVOT[1]) AND (SRSI[100]>0.5) AND (MACD[10,100]>0) AND (SMASHIFT[ 13 , 8]>SMASHIFT[ 8 , 5])) [GO_LONG] ; " +
//				"((CPRICE<PIVOT[1]) AND (SRSI[100]<0.5) AND (MACD[10,100]>0) AND (SMASHIFT[ 13 , 8]<SMASHIFT[ 5 , 8]) [GO_SHORT]";				//String newStrategy = "((RSI[7] <= 20) AND (CMF[21] <= -0.1)) [GO_LONG] ; ((RSI[7] >= 80) AND (CMF[21] >= 0.5))[GO_SHORT]";

		String aa = "((CPRICE >= PIVOT[1]) AND (SRSI[100] >= 0.5) AND (MACD[10,100] >= 0 ) AND (SMASHIFT[13,8] >= SMASHIFT[8,5])) [GO_LONG] ;" +
				"  ((CPRICE <= PIVOT[1]) AND (SRSI[100] <= 0.5) AND (MACD[10,100] >= 0 ) AND (SMASHIFT[13,8] <= SMASHIFT[8,5])) [GO_SHORT] ";

//		CandelstickList candlesticks = new CandelstickList(candlestickRepository.findByPairOrderByOpenTimeDesc(pair, Pageable.unpaged()).getContent());
//		;
		MultiBarSeries series = new BarSeriesGenerator(pair, new CandelstickList(new ArrayList<>(provaRepo.searchPair(pair))), null);
		BarSeries chosenBarSeries = series.getBarSeries(timeframe);
		StrategyManager manager = new StrategyManager(
				prova,
				chosenBarSeries,
				new LinearTransactionCostModel(0.00075),
				DoubleNum.valueOf(0),
				DoubleNum.valueOf(1000)
		);
		manager.setTradeAmount(DoubleNum.valueOf(0.01));

		//manager.s
//		Strategy strategy = new BHStrategyParser().parse(newStrategy,chosenBarSeries);
//		BarSeriesManager seriesManager = new BarSeriesManager(chosenBarSeries,new LinearTransactionCostModel(0.0005),new ZeroCostModel());


		BHTradingRecord tradingRecord = manager.run();
		System.out.println("Total Base: " + tradingRecord.getBaseAssetAmount());
		System.out.println("Total Quote: " + tradingRecord.getQuoteAssetAmount());

		//BaseTradingRecord
		excelReportService.createReport(tradingRecord,chosenBarSeries,DoubleNum.valueOf(0),DoubleNum.valueOf(1000),pair,prova);

//		ChaikinMoneyFlowIndicator flowIndicator = new ChaikinMoneyFlowIndicator(chosenBarSeries,21);
		System.out.println(chosenBarSeries.getSeriesPeriodDescription());
		System.out.println("Total profit for the strategy: " + new TotalProfitCriterion().calculate(chosenBarSeries, tradingRecord));
	}


//	@Test
//	void strategyParserTest() throws ParserException {
//		String pair = "BTCUSDT";
//		String timeframe = "5m";
//		CandelstickList candlesticks = new CandelstickList(candlestickRepository.findByPairOrderByOpenTimeDesc("BTCUSDT", PageRequest.of(0, 1000)).getContent());
//		MultiBarSeries series = new BarSeriesGenerator(pair, candlesticks, null);
//		BarSeries chosenBarSeries = series.getBarSeries(timeframe);
//		String aa = "((CPRICE >= PIVOT[1]) AND (SRSI[100] >= 0.5) AND (MACD[10,100] >= 0 ) AND (SMASHIFT[13,8] >= SMASHIFT[5,8])) [GO_LONG] ;  ((CPRICE <= PIVOT[1]) AND (SRSI[100] <= 0.5) AND (MACD[10,100] >= 0 ) AND (SMASHIFT[13,8] >= SMASHIFT[5,8])) [GO_SHORT] ";
//
//		String prova = "((CPRICE>PIVOT[1]) AND (SRSI[100]>0.5) AND (MACD[10,100]>0) AND (SMASHIFT[ 13 , 8]>SMASHIFT[ 5 , 8])) [GO_LONG] ; " +
//				"((CPRICE<PIVOT[1]) AND (SRSI[100]<0.5) AND (MACD[10,100]>0) AND (SMASHIFT[ 13 , 8]<SMASHIFT[ 5 , 8]) [GO_SHORT]";
//		StrategyManager manager = new StrategyManager(
//				aa,
//				chosenBarSeries,
//				new LinearTransactionCostModel(0.00075),
//				DoubleNum.valueOf(0),
//				DoubleNum.valueOf(10000)
//		);
//
//	}




//	private void pippo(String pair){
//        DBObject query = new BasicDBObject(); //setup the query criteria
//        query.put("pair", pair);
//        logger.debug("query: {}", query);
//
//        DBObject fields = new BasicDBObject(); //only get the needed fields.
//
//        fields.put("id", 0);
//        fields.put("pair", 0);
//        fields.put("openTime", 0);
//        fields.put("open", 0);
//        fields.put("high", 0);
//        fields.put("low", 0);
//        fields.put("close", 0);
//        fields.put("volume", 0);
//        fields.put("closeTime", 0);
//
//
//
//        DBCursor dbCursor = mongoTemplate.getCollection("collectionName").find(query, fields);
//
//        while (dbCursor.hasNext()){
//            DBObject object = dbCursor.next();
//            logger.debug("object: {}", object);
//            //do something.
//        }
//    }

    private List<Candlestick> testCriteria(String pair){
        Query query = new Query();

        query.addCriteria(Criteria.where("pair").is(pair));
        query.with(Sort.by(Sort.Direction.ASC, "openTime"));
        return mongoTemplate.getCollection("candlestick").find(query.getQueryObject(), Candlestick.class).into(new ArrayList<>());
    }


	private List<Candlestick> testCriteriaWhitCursor(String pair){
		var list = new ArrayList<Candlestick>();
		var candlestickColl =  mongoTemplate.getCollection("candlestick");
		Query query = new Query();
		query.addCriteria(Criteria.where("pair").is(pair));
		query.with(Sort.by(Sort.Direction.ASC, "openTime"));
		candlestickColl.find(query.getQueryObject(), Candlestick.class).forEach(list::add);
		return list;
	}


	private  List<Candlestick> testWhiSpring(String pair){
		var pageRequest = PageRequest.of(0, 200);
//		var list = new ArrayList<Candlestick>();
		var list = new CandelstickList(new ArrayList<Candlestick>());
		Page<Candlestick> onePage = candlestickRepository.findByPairOrderByOpenTimeDesc(pair, Pageable.unpaged());
		while (!onePage.isLast()) {
			onePage.forEach(list::add);
			onePage = candlestickRepository.findByPairOrderByOpenTimeDesc(pair,pageRequest.next());
		}
		return list;
	}

	private  List<Candlestick> testWhiSpringStream(String pair){
		Page<Candlestick> candlesticks;
		var list = new ArrayList<Candlestick>();
		int page = 0;
		do {
			candlesticks = candlestickRepository.findByPairOrderByOpenTimeDesc(pair, PageRequest.of(page, 100));
			page++;
		} while (candlesticks.hasNext());
		return  list;
//		var list1 = new ArrayList<Candlestick>();
//		Stream<Candlestick> candlestickStream = candlestickRepository.findByPairAndSort(pair,Sort.by(Sort.Direction.ASC, "openTime"));
////		Page<Candlestick> onePage = candlestickRepository.findByPairOrderByOpenTimeDesc(pair, Pageable.unpaged());
////		while (!onePage.isLast()) {
////			onePage.forEach(list::add);
////			onePage = candlestickRepository.findByPairOrderByOpenTimeDesc(pair,pageRequest.next());
////		}
//		candlestickStream.forEachOrdered(list1::add);
//		retur/n list1;
	}


	private void pippo(String pair){
//		DBObject query = new BasicDBObject(); //setup the query criteria
//		query.put("pair", pair);
//		//query.put("ctime", (new BasicDBObject("$gte", bTime)).append("$lt", eTime));
//
//		logger.debug("query: {}", query);
//
//		DBObject fields = new BasicDBObject(); //only get the needed fields.
//		fields.put("_id", 0);
//		fields.put("uId", 1);
//		fields.put("ctime", 1);
//
//		DBCursor dbCursor = mongoTemplate.getCollection("collectionName").find(query, fields);
//
//		while (dbCursor.hasNext()){
//			DBObject object = dbCursor.next();
//			logger.debug("object: {}", object);
//			//do something.
//		}
//		var list1 = new ArrayList<Candlestick>(mongoTemplate.find(new BasicQuery("{ pair: \"mongodb\"}", "{ name: 1}"), Candlestick.class, "candelstick"));
	}
}
