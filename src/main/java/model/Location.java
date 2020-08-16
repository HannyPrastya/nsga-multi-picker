
package model;

import com.fasterxml.jackson.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "y",
        "x",
        "distances",
        "index"
})
public class Location {

    @JsonProperty("distances")
    private ArrayList<Integer> distances;
    @JsonProperty("x")
    private Integer x;
    @JsonProperty("y")
    private Integer y;
    @JsonProperty("index")
    private Integer index;
    @JsonProperty("direction")
    private Integer direction;
    @JsonProperty("rowIndex")
    private Integer rowIndex;
    @JsonProperty("weight")
    private Integer weight;
    @JsonProperty("position")
    private Integer position;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();


    @JsonProperty("distances")
    public ArrayList<Integer> getDistances() {
        return distances;
    }

    @JsonProperty("distances")
    public void setDistances(ArrayList<Integer> distances) {
        this.distances = distances;
    }

    @JsonProperty("x")
    public Integer getX() {
        return x;
    }

    @JsonProperty("x")
    public void setX(Integer x) {
        this.x = x;
    }

    @JsonProperty("y")
    public Integer getY() {
        return y;
    }

    @JsonProperty("y")
    public void setY(Integer y) {
        this.y = y;
    }

    @JsonProperty("direction")
    public Integer getDirection() {
        return direction;
    }

    @JsonProperty("direction")
    public void setDirection(Integer direction) {
        this.direction = direction;
    }

    @JsonProperty("index")
    public Integer getIndex() {
        return index;
    }

    @JsonProperty("index")
    public void setIndex(Integer index) {
        this.index = index;
    }

    @JsonProperty("rowIndex")
    public Integer getRowIndex() {
        return rowIndex;
    }

    @JsonProperty("rowIndex")
    public void setRowIndex(Integer rowIndex) {
        this.rowIndex = rowIndex;
    }

    @JsonProperty("weight")
    public Integer getWeight() {
        return weight;
    }

    @JsonProperty("weight")
    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    @JsonProperty("position")
    public Integer getPosition() {
        return position;
    }

    @JsonProperty("position")
    public void setPosition(Integer position) {
        this.position = position;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
