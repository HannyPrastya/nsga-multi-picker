package model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "chromosome",
        "distance",
        "space",
        "batches"
})

public class Solution implements Cloneable{
    private int rank;
    private int dominationCount;
    private ArrayList<Integer> chromosome;
    private ArrayList<Solution> dominatedSolutions = new ArrayList<>();
    private ArrayList<Batch> batches;
    private double crowdingDistance = 0;
    private ArrayList<Double> objectiveValues = new ArrayList<>();
    private int maxNumberOfBatches = 0;
    private ArrayList<DueTime> dueTimes;

    public Solution(){
        objectiveValues.add(0.0);
        objectiveValues.add(0.0);
    }

    public void setMaxNumberOfBatches(int maxNumberOfBatches) {
        this.maxNumberOfBatches = maxNumberOfBatches;
    }

    public int getMaxNumberOfBatches() {
        return maxNumberOfBatches;
    }

    public ArrayList<Integer> getChromosome() {
        return chromosome;
    }

    public void setChromosome(ArrayList<Integer> chromosome) {
        this.chromosome = chromosome;
    }

    public double getDistance() {
        return objectiveValues.get(0);
    }

    public void setDistance(double distance) {
        this.objectiveValues.set(0, distance);
    }

    public double getSimilarity() {
        return objectiveValues.get(0);
    }

    public void setSimilarity(double similarity) {
        objectiveValues.set(1, similarity);
    }

    public ArrayList<Batch> getBatches() {
        return batches;
    }

    public void setBatches(ArrayList<Batch> batches) {
        this.batches = batches;
    }

    public int getDominationCount() {
        return dominationCount;
    }

    public void setDominationCount(int dominationCount) {
        this.dominationCount = dominationCount;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getRank() {
        return rank;
    }

    public double getCrowdingDistance() {
        return crowdingDistance;
    }

    public void setCrowdingDistance(double crowdingDistance) {
        this.crowdingDistance = crowdingDistance;
    }

    public void incrementDominationCount(int increment){
        this.dominationCount = dominationCount+increment;
    }

    public void setDominatedSolutions(Solution solution){
        this.dominatedSolutions.add(solution);
    }

    public ArrayList<Solution> getDominatedSolutions() {
        return dominatedSolutions;
    }

    public ArrayList<Double> getObjectiveValues() {
        return objectiveValues;
    }

    public void setObjectiveValues(ArrayList<Double> objectiveValues) {
        this.objectiveValues = objectiveValues;
    }

    public void setDueTimes(ArrayList<DueTime> dueTimes) {
        this.dueTimes = dueTimes;
    }

    public ArrayList<DueTime> getDueTimes() {
        return dueTimes;
    }

    public void printGens(){
        String del = "";
        for (Integer i : getChromosome()) {
            System.out.print(del+i);
            del = ", ";
        }
        System.out.println("");
    }

    public Solution clone() throws CloneNotSupportedException {
        Solution cloned = (Solution) super.clone();
        cloned.setChromosome((ArrayList<Integer>) chromosome.clone());

        return cloned;
    }

    public void reset(){

    }
}
