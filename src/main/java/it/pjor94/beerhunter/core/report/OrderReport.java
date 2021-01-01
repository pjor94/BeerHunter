package it.pjor94.beerhunter.core.report;

import lombok.Setter;
import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Order;
import org.ta4j.core.cost.CostModel;
import org.ta4j.core.num.Num;

import java.time.ZonedDateTime;

public class OrderReport extends Order {
    @Setter
    private Bar bar;


    @ExcelColumn(name="TIME")
    public ZonedDateTime getEndTime() {
        return bar.getEndTime();
    }

    @Override
    @ExcelColumn(name="TYPE")
    public OrderType getType() {
        return super.getType();
    }

    @Override
    @ExcelColumn(name="COST")
    public Num getCost() {
        return super.getCost();
    }

    @Override
    public int getIndex() {
        return super.getIndex();
    }

    @Override
    @ExcelColumn(name="PRICE")
    public Num getPricePerAsset() {
        return super.getPricePerAsset();
    }

    @Override
    @ExcelColumn(name="NET PRICE")
    public Num getNetPrice() {
        return super.getNetPrice();
    }

    @Override
    @ExcelColumn(name="AMOUNT")
    public Num getAmount() {
        return super.getAmount();
    }

    public OrderReport(Order order){
        super(order.getIndex(), order.getType(), order.getPricePerAsset(), order.getAmount(), order.getCostModel());
    }

    protected OrderReport(int index, BarSeries series, OrderType type) {
        super(index, series, type);
    }

    protected OrderReport(int index, BarSeries series, OrderType type, Num amount) {
        super(index, series, type, amount);
    }

    protected OrderReport(int index, BarSeries series, OrderType type, Num amount, CostModel transactionCostModel) {
        super(index, series, type, amount, transactionCostModel);
    }

    protected OrderReport(int index, OrderType type, Num pricePerAsset) {
        super(index, type, pricePerAsset);
    }

    protected OrderReport(int index, OrderType type, Num pricePerAsset, Num amount) {
        super(index, type, pricePerAsset, amount);
    }

    protected OrderReport(int index, OrderType type, Num pricePerAsset, Num amount, CostModel transactionCostModel) {
        super(index, type, pricePerAsset, amount, transactionCostModel);
    }
}
