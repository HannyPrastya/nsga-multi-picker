package algorithm;

import model.Batch;
import model.Location;
import model.Node;

import java.util.*;

public class AntColonyOptimization {

    private ArrayList<Batch> batches;
    private ArrayList<Location> locations;
    private int distance = 0;
    private int numberOfGenerations = 100;
    private int numberOfAnts = 5;
    private double probability = 0.5;
    Map<String, Integer> memory = new HashMap<String, Integer>();

    public void setBatches(ArrayList<Batch> batches) {
        this.batches = batches;
    }

    public void setLocations(ArrayList<Location> locations) {
        this.locations = locations;
    }

    public int getDistance() {
        return distance;
    }

    public int calculateDistanceACO(ArrayList<Integer> list){
        if(list.size() == 0){
            return 0;
        }

        int distance = 0;
        Map<Integer, Node> locs = new HashMap<Integer, Node>();

        locs.put(0, new Node());

        for (int idStart: list) {
            boolean isInline = false;
            int idEndTemp = 0;
            for (int idEnd: list) {
                if(locations.get(idStart).getDistances().get(idEnd) == 0 && idStart != idEnd){
                    isInline = true;
                    idEndTemp = idEnd;
                    break;
                }
            }
            if(!locs.containsKey(idEndTemp) || !isInline){
                locs.put(idStart, new Node());
            }
        }

        for (Map.Entry<Integer, Node> start: locs.entrySet()) {
            for (Map.Entry<Integer, Node> end: locs.entrySet()) {
                if(!start.getKey().equals(end.getKey())){
                    start.getValue().addData(end.getKey(), locations.get(start.getKey()).getDistances().get(end.getKey()));
                }
            }
        }

        for (int currentGeneration = 0; currentGeneration < numberOfGenerations; currentGeneration++) {
            ArrayList<ArrayList<Integer>> solutions = new ArrayList<>();
            ArrayList<Integer> distances = new ArrayList<>();
            HashMap<String, Double> ps = new HashMap<String, Double>();

            for (int i = 0; i < numberOfAnts; i++) {
                solutions.add(createSolution(locs));
            }

            int start = 0;
            for (ArrayList<Integer> path : solutions) {
                int l = 0;

                for (int j = 1; j < path.size(); j++) {
                    int end = path.get(j);
                    l += locations.get(start).getDistances().get(end);
                    start = end;
                }

                distances.add(l);
            }

            for (int i = 0; i < solutions.size(); i++) {
                ArrayList<Integer> path = solutions.get(i);
                start = 0;

                for (int j = 1; j < path.size(); j++) {
                    int end = path.get(j);
                    String key = start+"-"+end;
                    if(ps.containsKey(key)){
                        ps.put(key, ps.get(key) + (1/(double) distances.get(i)));
                    }else{
                        ps.put(key, (1/(double) distances.get(i)));
                    }

                    start = end;
                }
            }

            ps.forEach((k, p) -> {
                String[] keys =  k.split("-");
                int startNode = Integer.parseInt(keys[0]);
                int endNode = Integer.parseInt(keys[1]);
                double currentT = locs.get(startNode).getPheromones().get(endNode)*probability;
                locs.get(startNode).getPheromones().put(endNode, currentT+p);
            });

            for (int i = 0; i < distances.size(); i++) {
                Integer d = distances.get(i);
                if(d < distance || distance == 0){
                    distance = d;
                }
            }
        }

        return distance;
    }

    public ArrayList<Integer> createSolution(Map<Integer, Node> list){
        ArrayList<Integer> solution = new ArrayList<>();
        ArrayList<Integer> visitedLocations = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            if (visitedLocations.size() == 0) {
                visitedLocations.add(0);
                solution.add(0);
            }

            Integer startID = visitedLocations.get(visitedLocations.size() - 1);
            Node start = list.get(startID);

            double totalT = 0;
            ArrayList<Integer> possibleDestinations = new ArrayList<>();
            ArrayList<Double> possibleProbabilities = new ArrayList<>();
            ArrayList<Integer> ids = new ArrayList<>();
            for (Map.Entry<Integer, Node> destination : list.entrySet()) {
                if (!visitedLocations.contains(destination.getKey()) && !startID.equals(destination.getKey())) {
                    double T = start.getPheromones().get(destination.getKey()) * (1 / (double) start.getDistances().get(destination.getKey()));
                    possibleProbabilities.add(T);
                    possibleDestinations.add(destination.getKey());
                    ids.add(destination.getKey());
                    totalT += T;
                }
            }

            int selected = 0;
            Random r = new Random();
            int rand = r.nextInt(1000);
            double posStart = 0;
            for (int j = 0; j < possibleDestinations.size(); j++) {
                double posEnd = posStart + ((possibleProbabilities.get(j) / totalT) * 1000);
                if(rand >= posStart && rand < posEnd){
                    selected = j;
                    break;
                }
                posStart = posEnd;
            }
            if(ids.size() == 0){
                solution.add(0);
            }else{
                solution.add(ids.get(selected));
                visitedLocations.add(ids.get(selected));
            }
        }
        for (Map.Entry<Integer, Node> loc : list.entrySet()) {

        }
        return solution;
    }

    public int calculateDistancePerBatch(Batch batch){
        ArrayList<Integer> sortedData = new ArrayList<>(batch.getIDs().keySet());
        Collections.sort(sortedData);
        String ids = batch.getIDs().toString();
        int shortestDistance = 0;

        if(memory.containsKey(ids)){
            shortestDistance = memory.get(ids);
        }else{
            shortestDistance = calculateDistanceACO(sortedData);

            if(!memory.containsKey(ids)){
                memory.put(ids, shortestDistance);
            }
        }

        return shortestDistance;
    }

    public void calculateDistance(){
        int totalDistance = 0;
        for (Batch batch: this.batches) {
            totalDistance += calculateDistancePerBatch(batch);
        }
        distance = totalDistance;
    }
}
