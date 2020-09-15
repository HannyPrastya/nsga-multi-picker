package model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "numberOfOrders",
        "numberOfItems",
        "totalOfWeight",
        "orders",
        "items",
        "capacity",
        "dueTimes",
})
public class Dataset {

    @JsonProperty("numberOfPickers")
    private Integer numberOfPickers;
    @JsonProperty("numberOfOrders")
    private Integer numberOfOrders;
    @JsonProperty("numberOfItemPerOrder")
    private Integer numberOfItemPerOrder;
    @JsonProperty("totalOfWeight")
    private Integer totalOfWeight;
    @JsonProperty("orders")
    private List<Order> orders = null;
    @JsonProperty("items")
    private List<Item> items = null;
    @JsonProperty("capacity")
    private int capacity = 0;
    @JsonProperty("dueTimes")
    private List<DueTime> dueTimes = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("numberOfPickers")
    public Integer getNumberOfPickers() {
        return numberOfPickers;
    }

    @JsonProperty("numberOfPickers")
    public void setNumberOfPickers(Integer numberOfPickers) {
        this.numberOfPickers = numberOfPickers;
    }

    @JsonProperty("numberOfOrders")
    public Integer getNumberOfOrders() {
        return numberOfOrders;
    }

    @JsonProperty("numberOfOrders")
    public void setNumberOfOrders(Integer numberOfOrders) {
        this.numberOfOrders = numberOfOrders;
    }

    @JsonProperty("numberOfItemPerOrder")
    public Integer getNumberOfItemPerOrder() {
        return numberOfItemPerOrder;
    }

    @JsonProperty("numberOfItemPerOrder")
    public void setNumberOfItemPerOrder(Integer numberOfItemPerOrder) {
        this.numberOfItemPerOrder = numberOfItemPerOrder;
    }

    @JsonProperty("totalOfWeight")
    public Integer getTotalOfWeight() {
        return totalOfWeight;
    }

    @JsonProperty("totalOfWeight")
    public void setTotalOfWeight(Integer totalOfWeight) {
        this.totalOfWeight = totalOfWeight;
    }

    @JsonProperty("orders")
    public List<Order> getOrders() {
        return orders;
    }

    @JsonProperty("orders")
    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    @JsonProperty("items")
    public List<Item> getItems() {
        return items;
    }

    @JsonProperty("items")
    public void setItems(List<Item> items) {
        this.items = items;
    }

    @JsonProperty("capacity")
    public int getCapacity() {
        return capacity;
    }

    @JsonProperty("capacity")
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    @JsonProperty("dueTimes")
    public List<DueTime> getDueTimes() {
        return dueTimes;
    }

    @JsonProperty("dueTimes")
    public void setDueTimes(List<DueTime> dueTimes) {
        this.dueTimes = dueTimes;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

//    public int getMaxNumberofBatches(){
//        return (int) Math.ceil(getTotalOfWeight()/getCapacity());
//    }
}
