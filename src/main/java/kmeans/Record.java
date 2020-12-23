package kmeans;

import java.util.Map;

public class Record {
    private final String description;
    private final Map<String, Double> features;

    public Record(String description, Map<String, Double> features) {
        this.description = description;
        this.features = features;
    }

    public Map<String, Double> getFeatures() {
        return features;
    }

    public String getDescription() {
        return description;
    }
}
