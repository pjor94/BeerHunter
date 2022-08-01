package it.pjor94.beerhunter.core.report;

import it.pjor94.beerhunter.core.trading.BHTradingRecord;
import lombok.SneakyThrows;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.ta4j.core.*;
import org.ta4j.core.num.Num;

import java.io.FileOutputStream;
import java.util.*;

@Service
public class ExcelReportService {
    protected final Logger log = LoggerFactory.getLogger(getClass());
    @Autowired
    private ExcelReportUtils excelReportUtils;

    @SneakyThrows
    public void createBacktestingReport(TradingRecord tradingRecord, BarSeries barSeries){
        List<OrderReport> orderReportList = new ArrayList<>();
        tradingRecord.getTrades().forEach(
                trade -> {
                    OrderReport entryOrder = new OrderReport(trade.getEntry());
                    Bar entryBar = barSeries.getBar(entryOrder.getIndex());
                    entryOrder.setBar(entryBar);
                    OrderReport exitOrder = new OrderReport(trade.getExit());
                    Bar exitBar = barSeries.getBar(exitOrder.getIndex());
                    exitOrder.setBar(exitBar);
                    orderReportList.add(entryOrder);
                    orderReportList.add(exitOrder);
                });
        // Analysis
        Workbook workbook = new XSSFWorkbook();
        // Create a Sheet
        Sheet sheet = workbook.createSheet("test");
        excelReportUtils.pojoToSheet(sheet,orderReportList);
        FileOutputStream out = new FileOutputStream("/Users/philip/Developer/beerhunter-gradle/test-reports/"+barSeries.getSeriesPeriodDescription()+".xlsx");
        workbook.write(out);
        out.close();
        workbook.close();
    }

    @SneakyThrows
    public void createReport(BHTradingRecord tradingRecord, BarSeries barSeries, Num base , Num quote,String pair,String strategy){
        Workbook workbook = new XSSFWorkbook();

        // Create a Sheet
        Sheet sheet = workbook.createSheet("test");
        Row row = null;
        Cell cell = null;
        int r = 10;
        int c = 0;
        int colCount = 0;

        //Long
        var buyTime = 1;
        var buyAmount = 2;
        var buyPrice = 3;
        var buyNetPrice = 4;
        var buyFee = 5;
        //Short
        var sellTime = 6;
        var sellAmount = 7;
        var sellPrice = 8;
        var sellNetPrice = 9;
        var sellFee = 10;
        //
        var profitCell = 11;
        var basePortfolio = 12;
        var quotePortfolio = 13;


//        int firstRow = 0;
//        int lastRow = 0;
//        int firstCol = 0;
//        int lastCol = 2;

        Font titleFont = workbook.createFont();
        titleFont.setBold(true);
        titleFont.setColor(IndexedColors.LIGHT_BLUE.getIndex());
        titleFont.setFontHeightInPoints((short) 44);
        titleFont.setItalic(false);


        CellStyle titleStyle = workbook.createCellStyle();
        //titleStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        titleStyle.setAlignment(HorizontalAlignment.JUSTIFY);
        titleStyle.setFont(titleFont);
       // titleStyle.setAlignment(HorizontalAlignment.CENTER);

        sheet.addMergedRegion(new CellRangeAddress(0, 6, 1, 15));
        row = sheet.createRow(0);
        Cell title =row.createCell(buyTime);
        title.setCellStyle(titleStyle);
        title.setCellValue("BEERHUNTER BACKTESTING");

        row = sheet.createRow(r-2);
        cell = row.createCell(buyTime);
        cell.setCellValue("PAIR");
        cell = row.createCell(buyTime + 1);
        cell.setCellValue(pair);
        row = sheet.createRow(r-1);
        cell = row.createCell(buyTime);
        cell.setCellValue("Strategy");
        cell = row.createCell(buyTime + 1);
        cell.setCellValue(strategy);

        row = sheet.createRow(r++);

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);

        CellStyle positive = workbook.createCellStyle();
        positive.setFillForegroundColor(IndexedColors.GREEN.getIndex());
        positive.setFillPattern(FillPatternType.SOLID_FOREGROUND);


        CellStyle negative = workbook.createCellStyle();
        negative.setFillForegroundColor(IndexedColors.RED.getIndex());
        negative.setFillPattern(FillPatternType.SOLID_FOREGROUND);


        CellStyle cellStyle = workbook.createCellStyle();
        CreationHelper createHelper = workbook.getCreationHelper();
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd/MM/YYYY hh:mm"));


        cell = row.createCell(buyTime);
        cell.setCellStyle(headerStyle);
        cell.setCellValue("BUY TIME");

        cell = row.createCell(buyAmount);
        cell.setCellStyle(headerStyle);
        cell.setCellValue("BUY AMOUNT");

        cell = row.createCell(buyPrice);
        cell.setCellStyle(headerStyle);
        cell.setCellValue("BUY PRICE");

        cell = row.createCell(buyNetPrice);
        cell.setCellStyle(headerStyle);
        cell.setCellValue("BUY NET PRICE");

        cell = row.createCell(buyFee);
        cell.setCellStyle(headerStyle);
        cell.setCellValue("BUY FEE");

        cell = row.createCell(sellTime);
        cell.setCellStyle(headerStyle);
        cell.setCellValue("SELL TIME");

        cell = row.createCell(sellAmount);
        cell.setCellStyle(headerStyle);
        cell.setCellValue("SELL AMOUNT");

        cell = row.createCell(sellPrice);
        cell.setCellStyle(headerStyle);
        cell.setCellValue("SELL PRICE");

        cell = row.createCell(sellNetPrice);
        cell.setCellStyle(headerStyle);
        cell.setCellValue("SELL NET PRICE");

        cell = row.createCell(sellFee);
        cell.setCellStyle(headerStyle);
        cell.setCellValue("SELL FEE");

        cell = row.createCell(profitCell);
        cell.setCellStyle(headerStyle);
        cell.setCellValue("PROFIT");



        cell = row.createCell(basePortfolio);
        cell.setCellStyle(headerStyle);
        cell.setCellValue("BASE PORTFOLIO");
        cell = row.createCell(quotePortfolio);
        cell.setCellStyle(headerStyle);
        cell.setCellValue("QUOTE PORTFOLIO");

        colCount = sellFee;
        sheet.setAutoFilter(new CellRangeAddress(row.getRowNum(), row.getRowNum(), buyTime, quotePortfolio));
//        for(Trade trade :tradingRecord.getTrades()){
//            row = sheet.createRow(r++);
//            Order entry = trade.getEntry();
//            Order exit  = trade.getExit();
//            Bar entryBar = barSeries.getBar(entry.getIndex());
//            Bar exitBar = barSeries.getBar(exit.getIndex());
//            //LONG
//            cell = row.createCell(buyTime);
//            cell.setCellStyle(cellStyle);
//            cell.setCellValue(Date.from(entryBar.getEndTime().toInstant()));
//            cell = row.createCell(buyAmount);
//            cell.setCellValue(entry.getAmount().doubleValue());
//            cell = row.createCell(buyPrice);
//            cell.setCellValue(entry.getPricePerAsset().doubleValue());
//            cell = row.createCell(buyNetPrice);
//            cell.setCellValue(entry.getNetPrice().doubleValue());
//            cell = row.createCell(buyFee);
//            cell.setCellValue(entry.getCost().doubleValue());
//            //SHORT
//            cell = row.createCell(sellTime);
//            cell.setCellStyle(cellStyle);
//            cell.setCellValue(Date.from(exitBar.getEndTime().toInstant()));
//            cell = row.createCell(sellAmount);
//            cell.setCellValue(exit.getAmount().doubleValue());
//            cell = row.createCell(sellPrice);
//            cell.setCellValue(exit.getPricePerAsset().doubleValue());
//            cell = row.createCell(sellNetPrice);
//            cell.setCellValue(exit.getNetPrice().doubleValue());
//            cell = row.createCell(sellFee);
//            cell.setCellValue(exit.getCost().doubleValue());
//            //
//            cell = row.createCell(profitCell);
//            var profit =(exit.getAmount().multipliedBy(exit.getNetPrice())).minus((entry.getAmount().multipliedBy(entry.getNetPrice())));
////            cell.getCellStyle().setFillPattern(FillPatternType.SOLID_FOREGROUND);
////            cell.getCellStyle().setFillBackgroundColor(profit.isPositive() ? IndexedColors.GREEN.getIndex() : IndexedColors.RED.getIndex());
//            cell.setCellStyle( profit.isPositive() ? positive : negative);
//            cell.setCellValue(profit.doubleValue());
//            //cell.setCellFormula("SUM(("+row.getCell(buyAmount).getAddress()+"*"+row.getCell(buyNetPrice).getAddress()+")-("+row.getCell(sellAmount).getAddress()+"*"+row.getCell(sellNetPrice).getAddress()+"))");
//            //cell.setCellFormula(strFormula);
//            base=base.plus(entry.getAmount());
//            quote=quote.minus(entry.getAmount().multipliedBy(entry.getNetPrice()));
//            base=base.minus(exit.getAmount());
//            quote=quote.plus(exit.getAmount().multipliedBy(exit.getNetPrice()));
//            cell = row.createCell(basePortfolio);
//            cell.setCellValue(base.doubleValue());
//            cell = row.createCell(quotePortfolio);
//            cell.setCellValue(quote.doubleValue());
//        }
        List<Trade> all = tradingRecord.getTrades();
        all.addAll(tradingRecord.getOpenTrades());
        all.sort(Comparator.comparing((trade)->barSeries.getBar(trade.getEntry().getIndex()).getEndTime()));
        for(Trade trade : all){
            row = sheet.createRow(r++);
            Order entry = trade.getEntry();
            Order exit  = trade.getExit();
            Bar entryBar = barSeries.getBar(entry.getIndex());
            //LONG
            cell = row.createCell(buyTime);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(Date.from(entryBar.getEndTime().toInstant()));
            cell = row.createCell(buyAmount);
            cell.setCellValue(entry.getAmount().doubleValue());
            cell = row.createCell(buyPrice);
            cell.setCellValue(entry.getPricePerAsset().doubleValue());
            cell = row.createCell(buyNetPrice);
            cell.setCellValue(entry.getNetPrice().doubleValue());
            cell = row.createCell(buyFee);
            cell.setCellValue(entry.getCost().doubleValue());
            base=base.plus(entry.getAmount());
            quote=quote.minus(entry.getAmount().multipliedBy(entry.getNetPrice()));
            //SHORT
            if(exit!= null){
                Bar exitBar = barSeries.getBar(exit.getIndex());
                cell = row.createCell(sellTime);
                cell.setCellStyle(cellStyle);
                cell.setCellValue(Date.from(exitBar.getEndTime().toInstant()));
                cell = row.createCell(sellAmount);
                cell.setCellValue(exit.getAmount().doubleValue());
                cell = row.createCell(sellPrice);
                cell.setCellValue(exit.getPricePerAsset().doubleValue());
                cell = row.createCell(sellNetPrice);
                cell.setCellValue(exit.getNetPrice().doubleValue());
                cell = row.createCell(sellFee);
                cell.setCellValue(exit.getCost().doubleValue());
                //
                cell = row.createCell(profitCell);
                var profit =(exit.getAmount().multipliedBy(exit.getNetPrice())).minus((entry.getAmount().multipliedBy(entry.getNetPrice())));
                cell.setCellStyle( profit.isPositive() ? positive : negative);
                cell.setCellValue(profit.doubleValue());
                cell.setCellValue(profit.doubleValue());
                base=base.minus(exit.getAmount());
                quote=quote.plus(exit.getAmount().multipliedBy(exit.getNetPrice()));
            }
            cell = row.createCell(basePortfolio);
            cell.setCellValue(base.doubleValue());
            cell = row.createCell(quotePortfolio);
            cell.setCellValue(quote.doubleValue());

        }
        // auto size columns
        for (int col = 0; col < colCount; col++) {
            sheet.autoSizeColumn(col);
        }

        FileOutputStream out = new FileOutputStream("/Users/philip/Developer/beerhunter-gradle/test-reports/"+barSeries.getSeriesPeriodDescription()+".xlsx");
        workbook.write(out);
        out.close();
        workbook.close();
    }
}
