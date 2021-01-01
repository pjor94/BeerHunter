package it.pjor94.beerhunter.core.trading;

import org.ta4j.core.BaseTradingRecord;
import org.ta4j.core.Order;
import org.ta4j.core.Trade;
import org.ta4j.core.TradingRecord;
import org.ta4j.core.cost.CostModel;
import org.ta4j.core.cost.ZeroCostModel;
import org.ta4j.core.num.Num;

import java.util.ArrayList;
import java.util.List;

public class BHTradingRecord implements TradingRecord {

    private static final long serialVersionUID = -4436851731855891220L;

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
    private Trade currentTrade;
    /**
     * Trading cost models
     */
    private CostModel transactionCostModel;
    private CostModel holdingCostModel;
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
    }

    /**
     * Constructor.
     *
     * @param orders the orders to be recorded (cannot be empty)
     */
    public BHTradingRecord(Order... orders) {
        this(new ZeroCostModel(), new ZeroCostModel(), orders);
    }

    /**
     * Constructor.
     *
     * @param transactionCostModel the cost model for transactions of the asset
     * @param holdingCostModel     the cost model for holding asset (e.g. borrowing)
     * @param orders               the orders to be recorded (cannot be empty)
     */
    public BHTradingRecord(CostModel transactionCostModel, CostModel holdingCostModel, Order... orders) {
        this(orders[0].getType(), transactionCostModel, holdingCostModel);
        for (Order o : orders) {
            boolean newOrderWillBeAnEntry = currentTrade.isNew();
            if (newOrderWillBeAnEntry && o.getType() != startingType) {
                // Special case for entry/exit types reversal
                // E.g.: BUY, SELL,
                // BUY, SELL,
                // SELL, BUY,
                // BUY, SELL
                currentTrade = new Trade(o.getType(), transactionCostModel, holdingCostModel);
            }
            Order newOrder = currentTrade.operate(o.getIndex(), o.getPricePerAsset(), o.getAmount());
            recordOrder(newOrder, newOrderWillBeAnEntry);
        }
    }

    @Override
    public Trade getCurrentTrade() {
        return currentTrade;
    }

    @Override
    public void operate(int index, Num price, Num amount) {
        if (currentTrade.isClosed()) {
            // Current trade closed, should not occur
            throw new IllegalStateException("Current trade should not be closed");
        }
        boolean newOrderWillBeAnEntry = currentTrade.isNew();
        Order newOrder = currentTrade.operate(index, price, amount);
        recordOrder(newOrder, newOrderWillBeAnEntry);
    }

    @Override
    public boolean enter(int index, Num price, Num amount) {
        if (currentTrade.isNew()) {
            operate(index, price, amount);
            return true;
        }
        return false;
    }

    @Override
    public boolean exit(int index, Num price, Num amount) {
        if (currentTrade.isOpened()) {
            operate(index, price, amount);
            return true;
        }
        return false;
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
        } else if (Order.OrderType.SELL.equals(order.getType())) {
            // Storing the new order in sell orders list
            sellOrders.add(order);
        }

        // Storing the trade if closed
        if (currentTrade.isClosed()) {
            trades.add(currentTrade);
            currentTrade = new Trade(startingType, transactionCostModel, holdingCostModel);
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
}
