package algorithm;

import helper.Common;
import helper.Meta;
import model.*;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

public class NonSortedGeneticAlgorithm {
    private static final int DOMINANT = 1;
    private static final int INFERIOR = 2;
    private static final int NON_DOMINATED = 3;

    private Dataset dataset;
    private ArrayList<Location> locations;
    private ArrayList<Solution> population;
    private final int numberOfPopulation = 10;
    private ArrayList<Solution> offsprings;
    private int crossover = 60;
    Map<String, Integer> memory = new HashMap<String, Integer>();
    Map<String, Double[]> memoryChromosome = new HashMap<String, Double[]>();
    Map<String, Double[]> fitnessMemory = new HashMap<String, Double[]>();
    private int shortestDistance = 999999999;
    int currentGeneration = 0;

    public void setDataset(Dataset dataset) {
        this.dataset = dataset;
    }

    public void setLocations(ArrayList<Location> locations) {
        this.locations = locations;
    }

    public void start() throws CloneNotSupportedException {
        this.setClusters();
        this.population = Meta.createNewPopulation(numberOfPopulation, dataset.getOrders().size());
        doOperators();

        int numberOfGeneration = 100;
        while (currentGeneration < numberOfGeneration){
            decoding();
            routing();
//            ArrayList<Solution> newPopulation = MetaHelpers.createNewPopulation(numberOfPopulation, dataset.getOrders().size(),MetaHelpers.getMaxNumberofBatches(dataset));
//            population.addAll(newPopulation);
//            population.addAll(offsprings);
//            calculateFitness();
//            preparePopulation();
//
////            population.forEach(solution -> {
////                System.out.println(solution.getObjectiveValues());
////            });
//            calculateDistances();
////            System.out.println(currentGeneration+". "+shortestDistance+" - "+population.size());
//
//            population = getChildFromCombinedPopulation();
//            doOperators();

            ++currentGeneration;
        }
//        System.out.println(shortestDistance);
    }

    private void setClusters(){
        this.dataset.getOrders().sort(Comparator.comparing(Order::getDueTimeID));

        this.dataset.getDueTimes().get(0).setStart(0);
        this.dataset.getDueTimes().get(this.dataset.getDueTimes().size() - 1).setEnd(this.dataset.getOrders().size()-1);

        int dueTimeID = this.dataset.getDueTimes().get(0).getId();
        for (int i = 0; i < this.dataset.getOrders().size(); i++) {
            Order o = this.dataset.getOrders().get(i);
            if(dueTimeID != o.getDueTimeID()){
                this.dataset.getDueTimes().get(o.getDueTimeID() - 1).setStart(i);
                this.dataset.getDueTimes().get(o.getDueTimeID() - 2).setEnd(i-1);
                dueTimeID = o.getDueTimeID();
            }
        }

        for (DueTime d: this.dataset.getDueTimes()) {
            System.out.println(d.getStart()+" - "+d.getEnd());
        }
    }

    private void calculateDistances(){
//        int rank1 = 0;
//        int mustBeCalc = 0;
        for (Solution sol: population) {
            if(sol.getRank() == 1){
                String ids = sol.getChromosome().toString();
                int sd;
                if(!memory.containsKey(ids)){
                    
                    memory.put(ids, shortestDistance);
                    sd = shortestDistance;
//                    ++mustBeCalc;
                }else{
                    sd = memory.get(ids);
                }
                if(sd < this.shortestDistance){
                    this.shortestDistance = sd;
                }
//                ++rank1;
            }
        }

//        System.out.println(rank1+" - "+mustBeCalc);
    }

    private void doOperators() throws CloneNotSupportedException {
        offsprings = new ArrayList<>();
        ArrayList<Integer> range = new ArrayList<>(population.size() - 1);

        for (int i = 0; i < population.size(); i++) {
            range.add(i);
        }

        int operated = 0;
        double limit = (double) crossover/(double) 100;
        while((double) operated/(double) population.size() < limit){
//            Crossover
            Integer key1 = range.get(Common.randInt(0, range.size()-1));
            range.remove(key1);
            Integer key2 = range.get(Common.randInt(0, range.size()-1));
            range.remove(key2);

            Solution offA = population.get(key1).clone();
            Solution offB = population.get(key2).clone();

            int key = Common.randInt(0, dataset.getDueTimes().size()-1);

            for (Integer i = dataset.getDueTimes().get(key).getStart(); i <= dataset.getDueTimes().get(key).getEnd(); i++) {
                Integer temp = offA.getChromosome().get(i);
                offA.getChromosome().set(i, offB.getChromosome().get(i));
                offB.getChromosome().set(i, temp);
            }
            operated += 2;
            offsprings.add(offA);
            offsprings.add(offB);
            System.out.println("crossover on "+key1+" and "+key2);
        }

        while((double) operated/(double) population.size() < (double) 1){
//            Mutation
            Integer key = range.get(Common.randInt(0, range.size()-1));
            range.remove(key);
            Solution off = population.get(key).clone();

            int keyDueTime = Common.randInt(0, dataset.getDueTimes().size()-1);

            ArrayList<Integer> list = new ArrayList<>();
            for (Integer i = dataset.getDueTimes().get(keyDueTime).getStart(); i <= dataset.getDueTimes().get(keyDueTime).getEnd(); i++) {
                list.add(off.getChromosome().get(i));
            }
            Collections.shuffle(list);
            int j = 0;
            for (Integer i = dataset.getDueTimes().get(keyDueTime).getStart(); i <= dataset.getDueTimes().get(keyDueTime).getEnd(); i++) {
                off.getChromosome().set(i, list.get(j));
                ++j;
            }

            ++operated;
            offsprings.add(off);
            System.out.println("mutation on "+key);
        }
    }

    public void decoding() throws CloneNotSupportedException {
        for (Solution sol: population) {
//            reset due time
            ArrayList<DueTime> dues = new ArrayList<>();
            int idx = 0;
            DueTime dueTime = dataset.getDueTimes().get(idx).clone();
            dueTime.setOrders(new ArrayList<>());
            for (int i = 0; i < sol.getChromosome().size(); i++) {
                int priority = sol.getChromosome().get(i);
                Order order = dataset.getOrders().get(i).clone();
                order.setPriority(priority);

                dueTime.getOrders().add(order);
                if(i >= dueTime.getEnd()){
                    dueTime.getOrders().sort(Comparator.comparing(Order::getPriority));
//                    System.out.println(dueTime.getTime());
//                    for (Order o: dueTime.getOrders()) {
//                        System.out.println(o.getPriority());
//                    }
                    dues.add(dueTime.clone());
                    if(i != sol.getChromosome().size() - 1){
                        ++idx;
                        dueTime = dataset.getDueTimes().get(idx);
                        dueTime.setOrders(new ArrayList<>());
                    }
                }
            }

            sol.setDueTimes(dues);

            for (DueTime d: sol.getDueTimes()) {
                for (Order o: d.getOrders()) {

                }
            }
        }
    }

    public void routing(){

    }

//    public void calculateFitness() throws CloneNotSupportedException {
//        for (Solution solution : population) {
//            calculateFitnessPerSolution(solution);
//        }
//
//        for(Solution solution : offsprings){
//            calculateFitnessPerSolution(solution);
//        }
//    }

//    public void calculateFitnessPerSolution(Solution solution) throws CloneNotSupportedException {
//        if(memoryChromosome.containsKey(solution.getChromosome().toString())){
//            ArrayList<Batch> batches = Meta.convertSolutionToBatches(solution, dataset);
//            solution.setBatches(batches);
//            solution.setDistance(memoryChromosome.get(solution.getChromosome().toString())[0]);
//            solution.setSimilarity(memoryChromosome.get(solution.getChromosome().toString())[1]);
//        }else{
//            Meta.calculateFitness(solution, this.dataset, this.locations, this.fitnessMemory);
//
//            Double[] r = new Double[2];
//            r[0] = solution.getObjectiveValues().get(0);
//            r[1] = solution.getObjectiveValues().get(1);
//            memoryChromosome.put(solution.getChromosome().toString(), r);
//        }
//    }
//
//    public ArrayList<Solution> preparePopulation() {
//        Solution[] populace = population.toArray(new Solution[population.size()]);
//
//        fastNonDominatedSort(populace);
//        crowdingDistanceAssignment(populace);
//
//        randomizedQuickSortForRank(population, 0, populace.length - 1);
//        return population;
//    }
//
//    public ArrayList<Solution> getChildFromCombinedPopulation() {
//        int lastNonDominatedSetRank = population.get(population.size() - 1).getRank();
//        ArrayList<Solution> populace = new ArrayList<>();
//
//        sortForCrowdingDistance(population, lastNonDominatedSetRank);
//
//        for(int i = 0; i < numberOfPopulation; i++){
//            populace.add(population.get(i));
//        }
//
//        return populace;
//    }
//
//    private static void fastNonDominatedSort(final Solution[] populace) {
//        for(Solution chromosome : populace) chromosome.reset();
//        for(int i = 0; i < populace.length - 1; i++) {
//
//            for(int j = i + 1; j < populace.length; j++) {
//                switch(dominates(populace[i], populace[j])) {
//                    case DOMINANT:
//                        populace[i].setDominatedSolutions(populace[j]);
//                        populace[j].incrementDominationCount(1);
//                        break;
//                    case INFERIOR:
//                        populace[i].incrementDominationCount(1);
//                        populace[j].setDominatedSolutions(populace[i]);
//                        break;
//                    case NON_DOMINATED: break;
//                }
//            }
//
//            if(populace[i].getDominationCount() == 0) populace[i].setRank(1);
//        }
//
//        if(populace[populace.length - 1].getDominationCount() == 0) populace[populace.length - 1].setRank(1);
//
//        for(int i = 0; i < populace.length; i++) {
//            for(Solution chromosome : populace[i].getDominatedSolutions()) {
//                chromosome.incrementDominationCount(-1);
//                if(chromosome.getDominationCount() == 0) chromosome.setRank(populace[i].getRank() + 1);
//            }
//        }
//    }
//
//    private static void crowdingDistanceAssignment(final Solution[] nondominatedChromosomes) {
//        int size = nondominatedChromosomes.length;
//        for(int i = 0; i < 2; i++) {
//            sortAgainstObjective(nondominatedChromosomes, i);
//
//            nondominatedChromosomes[0].setCrowdingDistance(Double.MAX_VALUE);
//            nondominatedChromosomes[size - 1].setCrowdingDistance(Double.MAX_VALUE);
//
//            double maxObjectiveValue = selectMaximumObjectiveValue(nondominatedChromosomes, i);
//            double minObjectiveValue = selectMinimumObjectiveValue(nondominatedChromosomes, i);
//
//            for(int j = 1; j < size - 1; j++) if(nondominatedChromosomes[j].getCrowdingDistance() < Double.MAX_VALUE) nondominatedChromosomes[j].setCrowdingDistance(
//                    nondominatedChromosomes[j].getCrowdingDistance() + (
//                            (nondominatedChromosomes[j + 1].getObjectiveValues().get(i) - nondominatedChromosomes[j - 1].getObjectiveValues().get(i)) / (maxObjectiveValue - minObjectiveValue)
//                    )
//            );
//        }
//    }
//
//    private static int dominates(final Solution chromosome1, final Solution chromosome2) {
//        if(isDominant(chromosome1, chromosome2)) return DOMINANT;
//        else if(isDominant(chromosome2, chromosome1)) return INFERIOR;
//        else return NON_DOMINATED;
//    }
//
//    private static boolean isDominant(final Solution chromosome1, final Solution chromosome2) {
//        boolean isDominant = true;
//        boolean atleastOneIsLarger = false;
//
////        for(int i = 0; i < Configuration.numberOfObjetives; i++) {
////            if(Configuration.objTypes[i]){
////                if(chromosome1.getObjectiveValues().get(i) > chromosome2.getObjectiveValues().get(i)) {
////                    isDominant = false;
////                    break;
////                } else if(!atleastOneIsLarger && (chromosome1.getObjectiveValues().get(i) < chromosome2.getObjectiveValues().get(i))) atleastOneIsLarger = true;
////            }else {
////                if(chromosome1.getObjectiveValues().get(i) < chromosome2.getObjectiveValues().get(i)) {
////                    isDominant = false;
////                    break;
////                } else if(!atleastOneIsLarger && (chromosome1.getObjectiveValues().get(i) > chromosome2.getObjectiveValues().get(i))) atleastOneIsLarger = true;
////            }
////        }
//
//        return isDominant && atleastOneIsLarger;
//    }
//
//    public static void randomizedQuickSortForRank(final List<Solution> populace, final int head, final int tail) {
//
//        if(head < tail) {
//
//            int pivot = randomizedPartitionForRank(populace, head, tail);
//
//            randomizedQuickSortForRank(populace, head, pivot - 1);
//            randomizedQuickSortForRank(populace, pivot + 1, tail);
//        }
//    }
//
//    public static void sortForCrowdingDistance(final List<Solution> populace, final int lastNonDominatedSetRank) {
//
//        int rankStartIndex = -1;
//        int rankEndIndex = -1;
//
//        for(int i = 0; i < populace.size(); i++)
//            if((rankStartIndex < 0) && (populace.get(i).getRank() == lastNonDominatedSetRank)) rankStartIndex = i;
//            else if((rankStartIndex >= 0) && (populace.get(i).getRank() == lastNonDominatedSetRank)) rankEndIndex = i;
//
//        randomizedQuickSortForCrowdingDistance(populace, rankStartIndex, rankEndIndex);
//    }
//
//    public static void sortAgainstObjective(final Solution[] populace, int objectiveIndex) {
//        randomizedQuickSortAgainstObjective(populace, 0, populace.length - 1, objectiveIndex);
//    }
//
//    public static double selectMaximumObjectiveValue(final Solution[] populace, int objectiveIndex) {
//
//        double result = populace[0].getObjectiveValues().get(objectiveIndex);
//
//        for(Solution chromosome : populace) if(chromosome.getObjectiveValues().get(objectiveIndex) > result) result = chromosome.getObjectiveValues().get(objectiveIndex);
//
//        return result;
//    }
//
//    public static double selectMinimumObjectiveValue(final Solution[] populace, int objectiveIndex) {
//
//        double result = populace[0].getObjectiveValues().get(objectiveIndex);
//
//        for(Solution chromosome : populace) if(chromosome.getObjectiveValues().get(objectiveIndex) < result) result = chromosome.getObjectiveValues().get(objectiveIndex);
//
//        return result;
//    }
//
//    private static int randomizedPartitionForRank(final List<Solution> populace, final int head, final int tail) {
//
//        swapForRank(populace, head, ThreadLocalRandom.current().nextInt(head, tail + 1));
//
//        return partitionForRank(populace, head, tail);
//    }
//
//    private static void swapForRank(final List<Solution> populace, final int firstIndex, final int secondIndex) {
//
//        Solution temporary = populace.get(firstIndex);
//
//        populace.set(firstIndex, populace.get(secondIndex));
//        populace.set(secondIndex, temporary);
//    }
//
//    private static int partitionForRank(final List<Solution> populace, final int head, final int tail) {
//
//        int pivot = populace.get(tail).getRank();
//        int pivotIndex = head;
//
//        for(int j = head; j < tail; j++) {
//
//            if(populace.get(j).getRank() <= pivot) {
//
//                swapForRank(populace, pivotIndex, j);
//                ++pivotIndex;
//            }
//        }
//
//        swapForRank(populace, pivotIndex, tail);
//
//        return pivotIndex;
//    }
//
//    private static void randomizedQuickSortForCrowdingDistance(final List<Solution> populace, final int head, final int tail) {
//
//        if(head < tail) {
//
//            int pivot = randomizedPartitionForCrowdingDistance(populace, head, tail);
//
//            randomizedQuickSortForCrowdingDistance(populace, head, pivot - 1);
//            randomizedQuickSortForCrowdingDistance(populace, pivot + 1, tail);
//        }
//    }
//
//    private static int randomizedPartitionForCrowdingDistance(final List<Solution> populace, final int head, final int tail) {
//
//        swapForCrowdingDistance(populace, head, ThreadLocalRandom.current().nextInt(head, tail + 1));
//
//        return partitionForCrowdingDistance(populace, head, tail);
//    }
//
//    private static void swapForCrowdingDistance(final List<Solution> populace, final int firstIndex, final int secondIndex) {
//
//        Solution temporary = populace.get(firstIndex);
//
//        populace.set(firstIndex, populace.get(secondIndex));
//        populace.set(secondIndex, temporary);
//    }
//
//    private static int partitionForCrowdingDistance(final List<Solution> populace, final int head, final int tail) {
//
//        double pivot = populace.get(tail).getCrowdingDistance();
//        int pivotIndex = head;
//
//        for(int j = head; j < tail; j++) {
//
//            if(populace.get(j).getCrowdingDistance() >= pivot) {
//
//                swapForRank(populace, pivotIndex, j);
//                ++pivotIndex;
//            }
//        }
//
//        swapForRank(populace, pivotIndex, tail);
//
//        return pivotIndex;
//    }
//
//    private static void randomizedQuickSortAgainstObjective(final Solution[] populace, final int head, final int tail, final int objectiveIndex) {
//
//        if(head < tail) {
//
//            int pivot = randomizedPartitionAgainstObjective(populace, head, tail, objectiveIndex);
//
//            randomizedQuickSortAgainstObjective(populace, head, pivot - 1, objectiveIndex);
//            randomizedQuickSortAgainstObjective(populace, pivot + 1, tail, objectiveIndex);
//        }
//    }
//
//    private static int randomizedPartitionAgainstObjective(final Solution[] populace, final int head, final int tail, final int objectiveIndex) {
//
//        swapAgainstObjective(populace, head, ThreadLocalRandom.current().nextInt(head, tail + 1));
//
//        return partitionAgainstObjective(populace, head, tail, objectiveIndex);
//    }
//
//    private static void swapAgainstObjective(final Solution[] populace, final int firstIndex, final int secondIndex) {
//
//        Solution temporary = populace[firstIndex];
//        populace[firstIndex] = populace[secondIndex];
//        populace[secondIndex] = temporary;
//    }
//
//    private static int partitionAgainstObjective(final Solution[] populace, final int head, final int tail, final int objectiveIndex) {
//
//        double pivot = populace[tail].getObjectiveValues().get(objectiveIndex);
//        int pivotIndex = head;
//
//        for(int j = head; j < tail; j++) {
//
//            if(populace[j].getObjectiveValues().get(objectiveIndex) <= pivot) {
//
//                swapAgainstObjective(populace, pivotIndex, j);
//                ++pivotIndex;
//            }
//        }
//
//        swapAgainstObjective(populace, pivotIndex, tail);
//
//        return pivotIndex;
//    }
}
