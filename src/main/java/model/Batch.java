package model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "orders",
        "locations",
        "totalWeight",
        "IDs"
})

public class Batch implements Cloneable{
    @JsonProperty("IDs")
    private TreeMap<Integer, Integer> IDs = new TreeMap<Integer, Integer>();

    @JsonProperty("IDs")
    public TreeMap<Integer, Integer> getIDs() {
        return IDs;
    }

    @JsonProperty("IDs")
    public void setIDs(TreeMap<Integer, Integer> IDs) {
        this.IDs = IDs;
    }

    @JsonProperty("orders")
    private ArrayList<Order> orders = new ArrayList<>();

    @JsonProperty("orders")
    public ArrayList<Order> getOrders() {
        return orders;
    }

    @JsonProperty("orders")
    public void setOrders(ArrayList<Order> orders) {
        this.orders = orders;
    }

    @JsonProperty("locations")
    private ArrayList<Integer> locations;

    @JsonProperty("locations")
    public ArrayList<Integer> getLocations() {
        return locations;
    }

    @JsonProperty("locationss")
    public void setLocations(ArrayList<Integer> locations) {
        this.locations = locations;
    }

    @JsonProperty("totalWeight")
    private int totalWeight = 0;

    @JsonProperty("totalWeight")
    public int getTotalWeight() {
        return totalWeight;
    }

    @JsonProperty("totalWeight")
    public void setTotalWeight(int totalWeight) {
        this.totalWeight = totalWeight;
    }

    @JsonProperty("totalItems")
    private int totalItems = 0;

    @JsonProperty("totalItems")
    public int getTotalItems() {
        return totalItems;
    }

    @JsonProperty("totalItems")
    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    private Date end;

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    //    public void refreshItems(){
//        setIDs(new HashSet<>());
//        totalWeight = 0;
//        for (Order order: getOrders()) {
//            IDs.addAll(order.getItemIDs());
//            totalWeight += order.getTotalWeight();
//        }
//    }

    public void addOrder(Order order){
        orders.add(order);
        totalWeight += order.getTotalWeight();
        totalItems += order.getItemIDs().size();
        for (Integer itemID : order.getItemIDs()) {
            if(IDs.containsKey(itemID)){
                IDs.put(itemID, IDs.get(itemID) + 1);
            }else{
                IDs.put(itemID, 1);
            }
        }
    }

    public Order removeAndGetOrder(){
        Order order = getOrders().get(0);
        for (Integer itemID : order.getItemIDs()) {
            if(IDs.get(itemID) > 1){
                IDs.put(itemID, IDs.get(itemID) - 1);
            }else{
                IDs.remove(itemID);
            }
        }
        getOrders().remove(0);
        totalWeight -= order.getTotalWeight();
        totalItems -= order.getItemIDs().size();

        return order;
    }

    public Batch clone() throws CloneNotSupportedException {
        Batch cloned = (Batch) super.clone();
//        cloned.set((ArrayList<Integer>) chromosome.clone());

        return cloned;
    }
}
