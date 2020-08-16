package model;


import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "numberOfVerticalAisle",
        "numberOfHorizontalAisle",
        "numberOfRows",
        "numberOfColumns"
})

public class Warehouse {
    @JsonProperty("numberOfVerticalAisle")
    private Integer numberOfVerticalAisle;
    @JsonProperty("numberOfHorizontalAisle")
    private Integer numberOfHorizontalAisle;
    @JsonProperty("numberOfRows")
    private Integer numberOfRows;
    @JsonProperty("numberOfColumns")
    private Integer numberOfColumns;

    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();


    @JsonProperty("numberOfVerticalAisle")
    public Integer getNumberOfVerticalAisle() {
        return numberOfVerticalAisle;
    }

    @JsonProperty("numberOfVerticalAisle")
    public void setNumberOfVerticalAisle(Integer numberOfVerticalAisle) {
        this.numberOfVerticalAisle = numberOfVerticalAisle;
    }

    @JsonProperty("numberOfHorizontalAisle")
    public Integer getNumberOfHorizontalAisle() {
        return numberOfHorizontalAisle;
    }

    @JsonProperty("numberOfHorizontalAisle")
    public void setNumberOfHorizontalAisle(Integer numberOfHorizontalAisle) {
        this.numberOfHorizontalAisle = numberOfHorizontalAisle;
    }

    @JsonProperty("numberOfRows")
    public Integer getNumberOfRows() {
        return numberOfRows;
    }

    @JsonProperty("numberOfRows")
    public void setNumberOfRows(Integer numberOfRows) {
        this.numberOfRows = numberOfRows;
    }

    @JsonProperty("numberOfColumns")
    public Integer getNumberOfColumns() {
        return numberOfColumns;
    }

    @JsonProperty("numberOfColumns")
    public void setNumberOfColumns(Integer numberOfColumns) {
        this.numberOfColumns = numberOfColumns;
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
