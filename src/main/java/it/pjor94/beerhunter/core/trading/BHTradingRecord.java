package it.pjor94.beerhunter.core.trading;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ta4j.core.BaseTradingRecord;
import org.ta4j.core.Order;
import org.ta4j.core.Trade;
import org.ta4j.core.TradingRecord;
import org.ta4j.core.cost.CostModel;
import org.ta4j.core.cost.ZeroCostModel;
import org.ta4j.core.num.DoubleNum;
import org.ta4j.core.num.Num;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class BHTradingRecord implements TradingRecord {
    protected final Logger log = LoggerFactory.getLogger(getClass());

    private static final long serialVersionUID = -4436851731855891220L;
    @Setter
    @Getter
    private Num quoteAssetAmount = DoubleNum.valueOf(0);
    @Setter
    @Getter
    private Num baseAssetAmount = DoubleNum.valueOf(0);
    @Setter
    private Num tradeAmount = DoubleNum.valueOf(0);

    private List<Order> orders = new ArrayList<>();
    /**
     * The recorded BUY orders
     */
    private List<Order> buyOrders = new ArrayList<>();
    /**
     * The recorded SELL orders
     */
    private List<Order> sellOrders = new ArrayList<>();
    /**
     * The recorded entry orders
     */
    private List<Order> entryOrders = new ArrayList<>();
    /**
     * The recorded exit orders
     */
    private List<Order> exitOrders = new ArrayList<>();
    /**
     * The recorded trades
     */
    private List<Trade> trades = new ArrayList<>();
    /**
     * The entry type (BUY or SELL) in the trading session
     */
    private Order.OrderType startingType;
    /**
     * The current non-closed trade (there's always one)
     */
    private List<Trade> currentTrades = new ArrayList<>();
    private Trade currentTrade;

    private Trade currentTradeToCloese;

    private boolean hasOpenTrade() {
        return currentTrades.stream().anyMatch(Trade::isOpened);
    }

    private boolean hasNewTrade() {
        return currentTrades.stream().anyMatch(Trade::isNew);
    }

    private boolean hasClosedTrade() {
        return currentTrades.stream().anyMatch(Trade::isClosed);
    }

    private Trade getNewTrade() {
        List<Trade> trades = currentTrades.stream()
                .filter(Trade::isNew).collect(Collectors.toList());
        return !trades.isEmpty() ? trades.get(0) : null;
    }

    private Trade getFirstOpenTrade() {
        List<Trade> trades = currentTrades.stream()
                .filter(Trade::isOpened).sorted(Comparator.comparing((Trade trade) -> trade.getEntry().getIndex())).collect(Collectors.toList());
        //.filter(Trade::isOpened).sorted(Comparator.comparingInt((trade) -> trade.getEntry().getIndex())).collect(Collectors.toList());
        return !trades.isEmpty() ? trades.get(0) : null;
    }

    private Trade getCloseTrade() {
        List<Trade> trades = currentTrades.stream()
                .filter(Trade::isClosed).sorted(Comparator.comparing((Trade trade) -> trade.getEntry().getIndex())).collect(Collectors.toList());
        return !trades.isEmpty() ? trades.get(0) : null;
    }

    public List<Trade> getOpenTrades() {
        return currentTrades.stream()
                .filter(Trade::isOpened).sorted(Comparator.comparing((Trade trade) -> trade.getEntry().getIndex())).collect(Collectors.toList());
    }


    /**
     * Trading cost models
     */
    private CostModel transactionCostModel;
    private CostModel holdingCostModel;

    private boolean primordiale = false;

    /**
     * Constructor.
     */
    public BHTradingRecord() {
        this(Order.OrderType.BUY);
    }

    /**
     * Constructor.
     */
    public BHTradingRecord(Order.OrderType orderType) {
        this(orderType, new ZeroCostModel(), new ZeroCostModel());
    }

    /**
     * Constructor.
     *
     * @param entryOrderType       the {@link Order.OrderType order type} of entries
     *                             in the trading session
     * @param transactionCostModel the cost model for transactions of the asset
     * @param holdingCostModel     the cost model for holding asset (e.g. borrowing)
     */
    public BHTradingRecord(Order.OrderType entryOrderType, CostModel transactionCostModel,
                           CostModel holdingCostModel) {
        if (entryOrderType == null) {
            throw new IllegalArgumentException("Starting type must not be null");
        }
        this.startingType = entryOrderType;
        this.transactionCostModel = transactionCostModel;
        this.holdingCostModel = holdingCostModel;
        currentTrade = new Trade(entryOrderType, transactionCostModel, holdingCostModel);
        currentTrades.add(new Trade(entryOrderType, transactionCostModel, holdingCostModel));
    }


    public List<Trade> getCurrentTrades() {
        return currentTrades;
    }

    @Override
    public Trade getCurrentTrade() {
        return null;
    }

    @Override
    public void operate(int index, Num price, Num amount) {
//        if (currentTrade.isClosed()) {
//            // Current trade closed, should not occur
//            throw new IllegalStateException("Current trade should not be closed");
//        }
//        boolean newOrderWillBeAnEntry = currentTrade.isNew();
//        Order newOrder = currentTrade.operate(index, price, amount);
//        recordOrder(newOrder, newOrderWillBeAnEntry);
    }

    @Override
    public boolean enter(int index, Num price, Num amount) {
        return enter(index, price);
    }

    @Override
    public boolean exit(int index, Num price, Num amount) {
        return exit(index, price);
    }

    private void operate(Trade trade, int index, Num price, Num amount) {
        Order newOrder = trade.operate(index, price, amount);
        // currentTrades.set(currenteTradeIndex,trade);
        recordOrder(newOrder, trade.isNew());
    }


    public boolean enter(int index, Num price) {
        if(primordiale){
            if (canEnter(index, price)) {
               if(currentTrade.isNew()){
                   operate(currentTrade, index, price, tradeAmount);
                   return true;
               }
            }
        }else {
            if (canEnter(index, price)) {
                Trade trade = hasNewTrade() ? getNewTrade() : new Trade(Order.OrderType.BUY, transactionCostModel, holdingCostModel);
                if (!hasNewTrade()) {
                    currentTrades.add(trade);
                }
                if (trade.isNew()) {
                    operate(trade, index, price, tradeAmount);
                    return true;
                }
            }
        }
        return false;
    }

    private int noprofit = 10;

    public boolean exit(int index, Num price) {
        var hasDone = false;
        if (primordiale) {
            if (canExit(index, price)) {
                if (currentTrade.isOpened() && price.isGreaterThan(currentTrade.getEntry().getNetPrice())) {
                    operate(currentTrade, index, price, tradeAmount);
                    hasDone = true;
                }
            }
        } else {
            if (canExit(index, price)) {
                if (hasOpenTrade()) {
                    var profittable = getOpenTrades().stream().filter((trade) -> price.isGreaterThan(trade.getEntry().getNetPrice())).collect(Collectors.toList());
                    for (Trade trade : profittable) {
                        if (canExit(index, price)) {
                            if (price.isGreaterThan(trade.getEntry().getNetPrice())) {
                                currentTradeToCloese = trade;
                                operate(trade, index, price, tradeAmount);
                                hasDone = true;
                            }
                        }
                    }
//                    if (profittable.isEmpty()) {
//                        if (canExit(index, price)) {
//                            currentTradeToCloese = getOpenTrades()
//                                    .stream().sorted(Comparator.comparing((Trade t) -> t.getEntry().getNetPrice()))
//                                    .collect(Collectors.toList()).stream().findFirst().get();
//                            operate(currentTradeToCloese, index, price, tradeAmount);
//                            hasDone = true;
//                        }
//                    }
                }
            }
        }
        return hasDone;
    }

    @Override
    public List<Trade> getTrades() {
        return trades;
    }

    @Override
    public Order getLastOrder() {
        if (!orders.isEmpty()) {
            return orders.get(orders.size() - 1);
        }
        return null;
    }

    @Override
    public Order getLastOrder(Order.OrderType orderType) {
        if (Order.OrderType.BUY.equals(orderType) && !buyOrders.isEmpty()) {
            return buyOrders.get(buyOrders.size() - 1);
        } else if (Order.OrderType.SELL.equals(orderType) && !sellOrders.isEmpty()) {
            return sellOrders.get(sellOrders.size() - 1);
        }
        return null;
    }

    @Override
    public Order getLastEntry() {
        if (!entryOrders.isEmpty()) {
            return entryOrders.get(entryOrders.size() - 1);
        }
        return null;
    }

    @Override
    public Order getLastExit() {
        if (!exitOrders.isEmpty()) {
            return exitOrders.get(exitOrders.size() - 1);
        }
        return null;
    }

    /**
     * Records an order and the corresponding trade (if closed).
     *
     * @param order   the order to be recorded
     * @param isEntry true if the order is an entry, false otherwise (exit)
     */
    private void recordOrder(Order order, boolean isEntry) {
        if (order == null) {
            throw new IllegalArgumentException("Order should not be null");
        }

        // Storing the new order in entries/exits lists
        if (isEntry) {
            entryOrders.add(order);
        } else {
            exitOrders.add(order);
        }

        // Storing the new order in orders list
        orders.add(order);
        if (Order.OrderType.BUY.equals(order.getType())) {
            // Storing the new order in buy orders list
            buyOrders.add(order);
            log.info("------------------------------------");
            log.info(String.valueOf(Order.OrderType.BUY));
            log.info("Amount   " + order.getAmount());
            log.info("Base Before   " + baseAssetAmount);
            baseAssetAmount = baseAssetAmount.plus(order.getAmount());
            log.info("Base After   " + baseAssetAmount);
            log.info("Quote Before  " + quoteAssetAmount);
            quoteAssetAmount = quoteAssetAmount.minus(order.getAmount().multipliedBy(order.getNetPrice()));
            log.info("Quote After" + quoteAssetAmount);
        } else if (Order.OrderType.SELL.equals(order.getType())) {
            // Storing the new order in sell orders list
            sellOrders.add(order);
            log.info("------------------------------------");
            log.info(String.valueOf(Order.OrderType.SELL));
            log.info("Amount   " + order.getAmount());
            log.info("Base Before  " + baseAssetAmount);
            baseAssetAmount = baseAssetAmount.minus(order.getAmount());
            log.info("Base After  " + baseAssetAmount);
            log.info("Quote Before  " + quoteAssetAmount);
            quoteAssetAmount = quoteAssetAmount.plus(order.getAmount().multipliedBy(order.getNetPrice()));
            log.info("Quote After  " + quoteAssetAmount);
        }

        // Storing the trade if closed
        if(primordiale){
            if (currentTrade.isClosed()) {
                trades.add(currentTrade);
                //currentTrades.remove(currentTradeToCloese);
                //currentTrades.add(new Trade(startingType, transactionCostModel, holdingCostModel));
                currentTrade = new Trade(startingType, transactionCostModel, holdingCostModel);
            }
        }else {
            if (hasClosedTrade()) {
                trades.add(currentTradeToCloese);
                currentTrades.remove(currentTradeToCloese);
                //currentTrades.add(new Trade(startingType, transactionCostModel, holdingCostModel));
                //currentTrade = new Trade(startingType, transactionCostModel, holdingCostModel);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("BaseTradingRecord:\n");
        for (Order order : orders) {
            sb.append(order.toString()).append("\n");
        }
        return sb.toString();
    }


    private boolean canEnter(int index, Num price) {
        return quoteAssetAmount.isGreaterThanOrEqual(tradeAmount.multipliedBy(price));
    }

    private boolean canExit(int index, Num price) {
        return baseAssetAmount.isGreaterThanOrEqual(tradeAmount);
    }
}
