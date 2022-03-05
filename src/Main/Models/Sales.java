package Main.Models;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class Sales implements Serializable, Comparable<Sales> {

    private final String country;
    private final String item_type;
    private final char order_priority;
    private final Date order_date;
    public final long order_ID;
    private final Date ship_date;
    private final int units_sold;
    private final float unit_price;
    private final float unit_cost;
    private final Double revenue;
    private final Double total_cost;
    private final Double total_profit;

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
        return "Sales{" +
                "country='" + country + '\'' +
                ", item_type='" + item_type + '\'' +
                ", order_priority=" + order_priority +
                ", order_date=" + order_date +
                ", order_ID=" + order_ID +
                ", ship_date=" + ship_date +
                ", units_sold=" + units_sold +
                ", unit_price=" + unit_price +
                ", unit_cost=" + unit_cost +
                ", revenue=" + revenue +
                ", total_cost=" + total_cost +
                ", total_profit=" + total_profit +
                '}';
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
        return (int) (this.order_ID - o.order_ID);
    }
}