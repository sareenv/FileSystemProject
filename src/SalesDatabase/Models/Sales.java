package SalesDatabase.Models;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;



/**
 * Sales is a model class which contain the relevant information regarding the Sales class
 * @author Vinayak Sareen.
 * @see Serializable
 * @see Comparable
 * */

public class Sales implements Serializable, Comparable<Sales> {
    public final String country;
    public final String item_type;
    public final char order_priority;
    public final Date order_date;
    public final long order_ID;
    public final Date ship_date;
    public final int units_sold;
    public final float unit_price;
    public final float unit_cost;
    public final Double revenue;
    public final Double total_cost;
    public final Double total_profit;

    public Sales(String country, String item_type, char order_priority, Date order_date,
                 long order_ID, Date ship_date, int units_sold, float unit_price,
                 float unit_cost, Double revenue,
                 Double total_cost, Double total_profit) {
        this.country = country;
        this.item_type = item_type;
        this.order_priority = order_priority;
        this.order_date = order_date;
        this.order_ID = order_ID;
        this.ship_date = ship_date;
        this.units_sold = units_sold;
        this.unit_price = unit_price;
        this.unit_cost = unit_cost;
        this.revenue = revenue;
        this.total_cost = total_cost;
        this.total_profit = total_profit;
    }

    @Override
    public String toString() {
        String pattern = "dd/MM/yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        return
                "Country:  " + country +
                        ", Item Type: " +  item_type  +
                        ", Priority: " +  order_priority +
                        ", Order Date: " + dateFormat.format(order_date) +
                        ", Order ID: " + order_ID +
                        ", Shipping Date: " +  dateFormat.format(ship_date) +
                        ", Units Sold: " + units_sold +
                        ", Unit Price: " + unit_price +
                        ", Unit Cost: " + unit_cost +
                        ", Revenue: " + revenue +
                        ", Total Cost: " + total_cost +
                        ", Total Profit: " + total_profit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sales sales = (Sales) o;
        return order_priority == sales.order_priority && order_ID == sales.order_ID
                && units_sold == sales.units_sold
                && Float.compare(sales.unit_price, unit_price) == 0
                && Float.compare(sales.unit_cost, unit_cost) == 0
                && country.equals(sales.country)
                && item_type.equals(sales.item_type)
                && order_date.equals(sales.order_date)
                && ship_date.equals(sales.ship_date)
                && revenue.equals(sales.revenue)
                && total_cost.equals(sales.total_cost)
                && total_profit.equals(sales.total_profit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(country, item_type, order_priority,
                order_date, order_ID, ship_date, units_sold,
                unit_price, unit_cost, revenue, total_cost,
                total_profit);
    }

    @Override
    public int compareTo(Sales o) {
        if (o == null) { return 1; }
        return (int) (this.order_ID - o.order_ID);
    }
}