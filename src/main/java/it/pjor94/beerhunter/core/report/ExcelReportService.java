package it.pjor94.beerhunter.core.report;

import lombok.SneakyThrows;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.TradingRecord;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExcelReportService {
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
}
