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
        "itemIDs",
        "totalWeight",
        "dueTimeID"
})
public class Order implements  Cloneable{

    private Integer priority;

    @JsonProperty("itemIDs")
    private List<Integer> itemIDs = null;
    @JsonProperty("totalWeight")
    private int totalWeight;
    @JsonProperty("dueTimeID")
    private int dueTimeID;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Integer getPriority() {
        return priority;
    }

    @JsonProperty("dueTimeID")
    public int getDueTimeID(){ return dueTimeID; }

    @JsonProperty("dueTimeID")
    public void setDueTimeID(int dueTimeID){
        this.dueTimeID = dueTimeID;
    }

    @JsonProperty("totalWeight")
    public int getTotalWeight() {
        return totalWeight;
    }

    @JsonProperty("totalWeight")
    public void setTotalWeight(int totalWeight) {
        this.totalWeight = totalWeight;
    }

    @JsonProperty("itemIDs")
    public List<Integer> getItemIDs() {
        return itemIDs;
    }

    @JsonProperty("itemIDs")
    public void setItemIDs(List<Integer> itemIDs) {
        this.itemIDs = itemIDs;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public Order clone() throws CloneNotSupportedException {
        Order cloned = (Order) super.clone();

        return cloned;
    }
}
