package model;

import java.util.HashMap;
import java.util.Map;

public class Node {
    private Map<Integer, Integer> distances = new HashMap<Integer, Integer>();
    private Map<Integer, Double> pheromones = new HashMap<Integer, Double>();

    public Map<Integer, Integer> getDistances() {
        return distances;
    }

    public Map<Integer, Double> getPheromones() {
        return pheromones;
    }

    public void addData(int key, int distance){
        distances.put(key, distance);
        pheromones.put(key, (double) 1);
    }
}