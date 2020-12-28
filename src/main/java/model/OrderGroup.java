package model;

import java.util.ArrayList;
import java.util.List;

public class OrderGroup {

    private List<Order> orders = new ArrayList<>();
    private Integer weight = 0;
    private Integer priority = 0;

    public List<Order> getOrders() {
        return orders;
    }

    public void add(Order order){
        orders.add(order);
        weight += order.getTotalWeight();
    }

    public Integer getWeight() {
        return weight;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Integer getPriority() {
        return priority;
    }
}
