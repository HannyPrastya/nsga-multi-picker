package helper;

import model.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class Meta {
    public static ArrayList<Integer> createChromosome(int numberOfOrders){
        ArrayList<Integer> chromosome = new ArrayList<>(numberOfOrders);
        for (int i = 0; i < numberOfOrders ; i++) {
            chromosome.add(i);
        }
        Collections.shuffle(chromosome);
        return chromosome;
    }

    public static ArrayList<Solution> createNewPopulation(int numberOfPopulation, int size){
        ArrayList<Solution> population = new ArrayList<>();
        for (int j = 0; j < numberOfPopulation; j++) {
            Solution solution = createNewSolution(size);
            population.add(solution);
        }

        return population;
    }

    public static Solution createNewSolution(int size){
        Solution solution = new Solution();
        solution.setChromosome(createChromosome(size));
        return solution;
    }

    public static void calculateFitness(Solution solution, Dataset dataset, ArrayList<Location> locations, Map<String, Double[]> memory) throws CloneNotSupportedException {
        ArrayList<Batch> batches = convertSolutionToBatches(solution, dataset);
        solution.setBatches(batches);

        int distance = 0;
        double similarity = 0;
        for (Batch batch: batches) {
            ArrayList<Integer> IDs = new ArrayList<>(batch.getIDs().keySet());
            Collections.sort(IDs);
            String ids = IDs.toString();
            if(!memory.containsKey(ids)){
                double d = 0.0;
                double s = 0.0;
                for (int i = 0; i < batch.getIDs().size(); i++) {
                    int id = IDs.get(i);
                    for (int j = i+1; j < batch.getIDs().size(); j++) {
                        int id2 = IDs.get(j);
                        d += locations.get(id).getDistances().get(id2);
//                        s += matrix.get(id).get(id2);
                    }
                    double x = ((double) batch.getIDs().get(id)/batch.getTotalItems()) * (1/ (double) batch.getIDs().size());
                    s += x;
                }
                similarity += s;

                Double[] r = new Double[1];
                r[0] = d;
                memory.put(ids, r);
                distance += r[0];
            }else{
                distance += memory.get(ids)[0];
                final Double[] s = {0.0};
                batch.getIDs().forEach(
                        (id, n) -> {
                            s[0] += (n/(double) batch.getTotalItems()) * (1/ (double) batch.getOrders().size());
                        }
                );
                similarity += s[0];
            }
        }

        solution.setDistance(distance);
        solution.setSimilarity(similarity);

//        System.out.println(solution.getObjectiveValues());
    }

    public static ArrayList<Batch> convertSolutionToBatches(Solution solution, Dataset dataset) throws CloneNotSupportedException {
        ArrayList<Batch> batches = new ArrayList<>();
        ArrayList<Order> orders = new ArrayList<>();

        for (int i = 0; i < solution.getMaxNumberOfBatches(); i++) {
            batches.add(new Batch());
        }

        for (int orderID = 0; orderID < solution.getChromosome().size(); orderID++) {
            Order order = dataset.getOrders().get(orderID);
            int batchNumber = solution.getChromosome().get(orderID);
            batches.get(batchNumber).addOrder(order);
        }

        for (Batch batch: batches) {
            while(batch.getTotalWeight() > dataset.getCapacity()){
                Order order = batch.removeAndGetOrder();
                boolean isFound = false;
                for (Batch batchSearch: batches) {
                    if(dataset.getCapacity() - batchSearch.getTotalWeight() > order.getTotalWeight()){
                        batchSearch.addOrder(order);
                        isFound = true;
                        break;
                    }
                }

                if(!isFound){
                    orders.add(order);
                }
            }
        }

        while(orders.size() > 0){
            Batch batch = batches.get(batches.size() - 1);
            Order order = orders.get(0);

            if(dataset.getCapacity() - batch.getTotalWeight() >= order.getTotalWeight()){
                batch.addOrder(order);
            }else{
                Batch newBatch = new Batch();
                newBatch.addOrder(order);
                batches.add(newBatch);
            }

            orders.remove(0);
        }
//        int t = 0;
//        for (Batch b: batches) {
//            t += b.getTotalWeight();
//        }
//        System.out.println("total after : "+t);

        return batches;
    }

    public static int getMaxNumberofBatches(Dataset ds){
        return (int) Math.ceil((double) ds.getTotalOfWeight()/ds.getCapacity());
    }

    public static ArrayList<Integer> mapLocationBasedOnOrders(ArrayList<Order> orders){
        ArrayList<Integer> locations = new ArrayList<>();
        ArrayList<Integer> numbers = new ArrayList<>();
        for (Order o: orders) {
            for (Integer item: o.getItemIDs()) {
                int key = locations.indexOf(item);
                if(key < 0){
                    locations.add(item);
                    numbers.add(1);
                }else{
                    numbers.set(key, numbers.get(key) + 1);
                }
            }
        }

        return locations;
    }
}
