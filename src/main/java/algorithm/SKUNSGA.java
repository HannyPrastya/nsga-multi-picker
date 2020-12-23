package algorithm;

import constant.Configuration;
import helper.Common;
import helper.ExcelExporter;
import helper.Meta;
import model.*;

import java.text.ParseException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class SKUNSGA {
    private static final int DOMINANT = 1;
    private static final int INFERIOR = 2;
    private static final int NON_DOMINATED = 3;

    private List<List<Double>> report;
    private List<List<Double>> reportF1;
    private Dataset dataset;
    private ArrayList<Location> locations;
    private List<List<Integer>> groups;
    private ArrayList<Solution> population;
    private final int numberOfPopulation = Configuration.numberOfPopulation;
    private final int numberOfGeneration = Configuration.numberOfGeneration;
    private ArrayList<Solution> offsprings;
    private int crossover = 60;
    int currentGeneration = 0;
    boolean log = false;

    private final Simulator simulator;
    private final RoutingAlgorithm router;

    public SKUNSGA() throws ParseException {
        this.simulator = new Simulator();
        this.router = new RoutingAlgorithm();
        this.simulator.setLog(false);
    }

    public void setDataset(Dataset dataset) {
        this.dataset = dataset;
    }

    public void setLocations(ArrayList<Location> locations) {
        this.locations = locations;
        this.router.setLocations(this.locations);
    }

    public Simulator getSimulator() {
        return simulator;
    }

    public RoutingAlgorithm getRouter() {
        return router;
    }

    public void start() throws CloneNotSupportedException, ParseException {
        report = new ArrayList<>();
        reportF1 = new ArrayList<>();
        this.groupingSKUs();
//        this.setClusters();
//        this.population = Meta.createNewPopulation(numberOfPopulation, dataset.getOrders().size());

        while (currentGeneration < numberOfGeneration) {
//            doOperators();
//            population.addAll(offsprings);
//            decoding();
//            routing();
//            simulate();
//            preparePopulation();
//            population = getChildrenFromCombinedPopulation();
//            report();
//            reportFrontier1();
//
//            if(log){
//                System.out.println("//after top population");
//                population.forEach(solution -> {
//                    System.out.println(solution.getObjectiveValues());
//                });
//                System.out.println("////////////////////////");
//            }
            ++currentGeneration;
        }

        System.out.println("LAST");
        population.forEach(solution -> {
            System.out.println(solution.getRank());
            System.out.println(solution.getDominatedSolutions().size());
        });

        exportExcel();
    }

    public void groupingSKUs(){
        this.groups = new ArrayList<>();
        for (int i = 0; i < this.dataset.getOrders().size(); i++) {
            Order order = this.dataset.getOrders().get(i);
            boolean flag = true;
            for (int j = 0; j < groups.size(); j++) {
                if(dataset.getOrders().get(groups.get(j).get(0)).getItemIDs().get(0).equals(order.getItemIDs().get(0))){
                    flag = false;
                    this.groups.get(j).add(i);
                    break;
                }
            }

            if(flag){
                groups.add(new ArrayList<>());
                groups.get(groups.size() - 1).add(i);
            }
        }
    }

    public void report(){
        List<Double> row = new ArrayList<>();

        Double minObjective1 = null;
        Double maxObjective1 = null;
        Double avgObjective1 = 0.0;
        Double minObjective2 = null;
        Double maxObjective2 = null;
        Double avgObjective2 = 0.0;

        for (Solution solution : population) {
            row.add(solution.getObjectiveValues().get(0));
            row.add(solution.getObjectiveValues().get(1));

            if(maxObjective1 == null){
                maxObjective1 = solution.getObjectiveValues().get(0);
            }

            if(minObjective1 == null){
                minObjective1 = solution.getObjectiveValues().get(0);
            }

            if(maxObjective2 == null){
                maxObjective2 = solution.getObjectiveValues().get(1);
            }

            if(minObjective2 == null){
                minObjective2 = solution.getObjectiveValues().get(1);
            }

            if(solution.getObjectiveValues().get(0) > maxObjective1){
                maxObjective1 = solution.getObjectiveValues().get(0);
            }

            if(solution.getObjectiveValues().get(0) < minObjective1){
                minObjective1 = solution.getObjectiveValues().get(0);
            }

            if(solution.getObjectiveValues().get(1) > maxObjective2){
                maxObjective2 = solution.getObjectiveValues().get(1);
            }

            if(solution.getObjectiveValues().get(1) < minObjective2){
                minObjective2 = solution.getObjectiveValues().get(1);
            }

            avgObjective1 += solution.getObjectiveValues().get(0);
            avgObjective2 += solution.getObjectiveValues().get(1);
        }

        row.add(minObjective1);
        row.add(maxObjective1);
        row.add(avgObjective1/population.size());
        row.add(minObjective2);
        row.add(maxObjective2);
        row.add(avgObjective2/population.size());

        report.add(row);
    }

    public void reportFrontier1(){
        List<Double> row = new ArrayList<>();
        double nOfSolution = 0.0;

        Double minObjective1 = null;
        Double maxObjective1 = null;
        Double avgObjective1 = 0.0;
        Double minObjective2 = null;
        Double maxObjective2 = null;
        Double avgObjective2 = 0.0;

        for (Solution solution : population) {
            if(solution.getRank() == 1){
                nOfSolution += 1;
                if(maxObjective1 == null){
                    maxObjective1 = solution.getObjectiveValues().get(0);
                }

                if(minObjective1 == null){
                    minObjective1 = solution.getObjectiveValues().get(0);
                }

                if(maxObjective2 == null){
                    maxObjective2 = solution.getObjectiveValues().get(1);
                }

                if(minObjective2 == null){
                    minObjective2 = solution.getObjectiveValues().get(1);
                }

                if(solution.getObjectiveValues().get(0) > maxObjective1){
                    maxObjective1 = solution.getObjectiveValues().get(0);
                }

                if(solution.getObjectiveValues().get(0) < minObjective1){
                    minObjective1 = solution.getObjectiveValues().get(0);
                }

                if(solution.getObjectiveValues().get(1) > maxObjective2){
                    maxObjective2 = solution.getObjectiveValues().get(1);
                }

                if(solution.getObjectiveValues().get(1) < minObjective2){
                    minObjective2 = solution.getObjectiveValues().get(1);
                }

                avgObjective1 += solution.getObjectiveValues().get(0);
                avgObjective2 += solution.getObjectiveValues().get(1);
            }else{
                break;
            }
        }

        row.add(minObjective1);
        row.add(maxObjective1);
        row.add(avgObjective1/nOfSolution);
        row.add(minObjective2);
        row.add(maxObjective2);
        row.add(avgObjective2/nOfSolution);

        reportF1.add(row);
    }

    public void exportExcel(){
        ExcelExporter excelExporter = new ExcelExporter();

//        OBJECTIVES
        List<String> columns = new ArrayList<>();
        columns.add("GEN");
        for (int i = 0; i < numberOfPopulation; i++) {
            columns.add("WT-"+i);
            columns.add("TARDINESS-"+i);
        }
        columns.add("MIN WAITING TIME (S)");
        columns.add("MAX WAITING TIME (S)");
        columns.add("AVG WAITING TIME (S)");
        columns.add("MIN TARDINESS (S)");
        columns.add("MAX TARDINESS (S)");
        columns.add("AVG TARDINESS (S)");

        excelExporter.setColumns(columns.toArray(new String[0]));
        excelExporter.setDoubleDataset(report);
        excelExporter.startDoubleDataset("Objectives");


//        FRONTIER 1
        columns = new ArrayList<>();
        columns.add("GEN");
        columns.add("MIN WAITING TIME (S)");
        columns.add("MAX WAITING TIME (S)");
        columns.add("AVG WAITING TIME (S)");
        columns.add("MIN TARDINESS (S)");
        columns.add("MAX TARDINESS (S)");
        columns.add("AVG TARDINESS (S)");

        excelExporter.setColumns(columns.toArray(new String[0]));
        excelExporter.setDoubleDataset(reportF1);
        excelExporter.startDoubleDataset("Frontier 1");

//        PARETO SOLUTIONS
        excelExporter.setColumns(new String[]{"SOLUTION", "WAITING TIME", "TARDINESS"});

        List<List<Double>> ds = new ArrayList<>();
        for (Solution solution : population) {
            if(solution.getRank() == 1){
                ds.add(solution.getObjectiveValues());
            }
        }
        excelExporter.setDoubleDataset(ds);
        excelExporter.startDoubleDataset("Pareto Solutions");

//        TRAFFIC
        columns = new ArrayList<>();
        columns.add("SOLUTION");
        List<HashMap<Integer, Integer>> hids = new ArrayList<>();
        for (Solution solution : population) {
            hids.add(solution.getTraffic());
        }

        excelExporter.setColumns(columns.toArray(new String[0]));
        excelExporter.setHashIntegerDataset(hids);
        excelExporter.startHashIntegerDataset("Traffic");

//        CONGESTION
        columns = new ArrayList<>();
        columns.add("SOLUTION");
        hids = new ArrayList<>();
        for (Solution solution : population) {
            hids.add(solution.getCongestion());
        }

        excelExporter.setColumns(columns.toArray(new String[0]));
        excelExporter.setHashIntegerDataset(hids);
        excelExporter.startHashIntegerDataset("Congestion");

        excelExporter.export("NSGA"+"-"+dataset.getOrders().size()+"-"+dataset.getCapacity()+"-"+simulator.getPickers().length);
    }

    private void setClusters() {
        this.dataset.getOrders().sort(Comparator.comparing(Order::getDueTimeID));

        this.dataset.getDueTimes().get(0).setStart(0);
        this.dataset.getDueTimes().get(this.dataset.getDueTimes().size() - 1).setEnd(this.dataset.getOrders().size() - 1);

        int dueTimeID = this.dataset.getDueTimes().get(0).getId();
        for (int i = 0; i < this.dataset.getOrders().size(); i++) {
            Order o = this.dataset.getOrders().get(i);
            if (dueTimeID != o.getDueTimeID()) {
                this.dataset.getDueTimes().get(o.getDueTimeID() - 1).setStart(i);
                this.dataset.getDueTimes().get(o.getDueTimeID() - 2).setEnd(i - 1);
                dueTimeID = o.getDueTimeID();
            }
        }
    }

    private void doOperators() throws CloneNotSupportedException {
        offsprings = new ArrayList<>();
        ArrayList<Integer> range = new ArrayList<>(population.size() - 1);

        for (int i = 0; i < population.size(); i++) {
            range.add(i);
        }

        int operated = 0;
        double limit = (double) crossover / (double) 100;
        while ((double) operated / (double) population.size() < limit) {
//            Crossover
            Integer key1 = range.get(Common.randInt(0, range.size() - 1));
            range.remove(key1);
            Integer key2 = range.get(Common.randInt(0, range.size() - 1));
            range.remove(key2);

            Solution offA = population.get(key1).clone();
            Solution offB = population.get(key2).clone();

            int key = Common.randInt(0, dataset.getDueTimes().size() - 1);

            for (Integer i = dataset.getDueTimes().get(key).getStart(); i <= dataset.getDueTimes().get(key).getEnd(); i++) {
                Integer temp = offA.getChromosome().get(i);
                offA.getChromosome().set(i, offB.getChromosome().get(i));
                offB.getChromosome().set(i, temp);
            }
            operated += 2;
            offsprings.add(offA);
            offsprings.add(offB);
            if(log){
                System.out.println("crossover on " + key1 + " and " + key2);
            }
        }

        while ((double) operated / (double) population.size() < (double) 1) {
//            Mutation
            Integer key = range.get(Common.randInt(0, range.size() - 1));
            range.remove(key);
            Solution off = population.get(key).clone();

            int keyDueTime = Common.randInt(0, dataset.getDueTimes().size() - 1);

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
            if(log){
                System.out.println("mutation on " + key);
            }
        }
    }

    public void decoding() throws CloneNotSupportedException {
        for (Solution sol : population) {
//            reset due time
            if(!sol.isSimulated()){
                ArrayList<DueTime> dues = new ArrayList<>();
                int idx = 0;
                DueTime dueTime = dataset.getDueTimes().get(idx).clone();
                dueTime.setOrders(new ArrayList<>());
                for (int i = 0; i < sol.getChromosome().size(); i++) {
                    int priority = sol.getChromosome().get(i);
                    Order order = dataset.getOrders().get(i).clone();
                    order.setPriority(priority);

                    dueTime.getOrders().add(order);
                    if (i >= dueTime.getEnd()) {
                        dueTime.getOrders().sort(Comparator.comparing(Order::getPriority));
//                    System.out.println(dueTime.getTime());
//                    for (Order o: dueTime.getOrders()) {
//                        System.out.println(o.getPriority());
//                    }
                        dues.add(dueTime.clone());
                        if (i != sol.getChromosome().size() - 1) {
                            ++idx;
                            dueTime = dataset.getDueTimes().get(idx);
                            dueTime.setOrders(new ArrayList<>());
                        }
                    }
                }

                sol.setDueTimes(dues);

                ArrayList<Batch> batches = new ArrayList<>();
                batches.add(new Batch());
                for (DueTime d : sol.getDueTimes()) {
                    for (Order o : d.getOrders()) {
                        boolean flag = true;
                        for (Batch b : batches) {
                            if (b.getTotalWeight() + o.getTotalWeight() <= this.dataset.getCapacity()) {
                                flag = false;
                                b.addOrder(o);
                                break;
                            }
                        }
                        if (flag) {
                            Batch batch = new Batch();
                            batch.addOrder(o);
                            batches.add(batch);
                        }
                    }
                }

                sol.setBatches(batches);
            }
        }
    }

    public void routing() {
        for (Solution sol : population) {
            if(!sol.isSimulated()) {
                switch (Configuration.routeAlgorithm){
                    case Configuration.S_SHAPE:
                        for (Batch b: sol.getBatches()) {
                            router.determineAisles(b);
                            router.routeUsingSShape(b);
                        }
                        break;
                    case Configuration.LARGEST_GAP:
                        for (Batch b: sol.getBatches()) {
                            router.determineAisles(b);
                            router.routingUsingLargestGap(b);
                        }
                        break;
                    case Configuration.COMBINED_PLUS:
                        for (Batch b: sol.getBatches()) {
                            router.determineAisles(b);
                            router.routingUsingCombined(b);
                        }
                        break;
                }
            }
        }
    }

    public void simulate() {
        for (Solution sol : population) {
            if(!sol.isSimulated()){
                this.simulator.setBatches(sol.getBatches());
                this.simulator.start();

//                GET DATA
                sol.setCongestion(simulator.getCongestion());
                sol.setTraffic(simulator.getTraffic());

                double totalWaitingTime = 0;
                double tardiness = 0;
                for (int i = 0; i < simulator.getPickers().length; i++) {
                    Picker picker = simulator.getPickers()[i];
                    for (WaitingTime t : picker.getWaitingTimes()) {
                        totalWaitingTime += t.getSeconds();
                    }
                    for (Batch batch : picker.getBatches()) {
                        for (Order order : batch.getOrders()) {
                            long tard = batch.getEnd().getTime() - this.dataset.getDueTimes().get(order.getDueTimeID() - 1).getTimeObject().getTime();
                            tardiness += tard > 0 ? ((double) tard / 1000) : 0;
                        }
                    }
                }

                if (sol.getObjectiveValues().size() == 2) {
                    sol.getObjectiveValues().set(0, totalWaitingTime);
                    sol.getObjectiveValues().set(1, tardiness);
                } else {
                    sol.setObjectiveValues(new ArrayList<>());
                    sol.getObjectiveValues().add(totalWaitingTime);
                    sol.getObjectiveValues().add(tardiness);
                }
                sol.setSimulated(true);
            }
        }
    }

    public void preparePopulation() {
        Solution[] populace = population.toArray(new Solution[population.size()]);

        fastNonDominatedSort(populace);
        crowdingDistanceAssignment(populace);

        randomizedQuickSortForRank(population, 0, population.size() - 1);
    }

//    public ArrayList<Solution> getChildrenFromCombinedPopulation() {
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

    public ArrayList<Solution> getChildrenFromCombinedPopulation() {
        int lastNonDominatedSetRank = population.get(population.size() - 1).getRank();
        ArrayList<Solution> populace = new ArrayList<>();

        sortForCrowdingDistance(population, lastNonDominatedSetRank);

        for(int i = 0; i < numberOfPopulation; i++){
            populace.add(population.get(i));
        }
        return populace;
    }

    private static void fastNonDominatedSort(final Solution[] populace) {
        for(Solution chromosome : populace) chromosome.reset();
        for(int i = 0; i < populace.length - 1; i++) {

            for(int j = i + 1; j < populace.length; j++) {
                switch(dominates(populace[i], populace[j])) {
                    case DOMINANT:
                        populace[i].setDominatedSolutions(populace[j]);
                        populace[j].incrementDominationCount(1);
                        break;
                    case INFERIOR:
                        populace[i].incrementDominationCount(1);
                        populace[j].setDominatedSolutions(populace[i]);
                        break;
                    case NON_DOMINATED: break;
                }
            }

            if(populace[i].getDominationCount() == 0) populace[i].setRank(1);
        }

        if(populace[populace.length - 1].getDominationCount() == 0) populace[populace.length - 1].setRank(1);

        for(int i = 0; i < populace.length; i++) {
            for(Solution chromosome : populace[i].getDominatedSolutions()) {
                chromosome.incrementDominationCount(-1);
                if(chromosome.getDominationCount() == 0) chromosome.setRank(populace[i].getRank() + 1);
            }
        }
    }

    private static void crowdingDistanceAssignment(final Solution[] nondominatedChromosomes) {
        int size = nondominatedChromosomes.length;
        for(int i = 0; i < 2; i++) {
            sortAgainstObjective(nondominatedChromosomes, i);

            nondominatedChromosomes[0].setCrowdingDistance(Double.MAX_VALUE);
            nondominatedChromosomes[size - 1].setCrowdingDistance(Double.MAX_VALUE);

            double maxObjectiveValue = selectMaximumObjectiveValue(nondominatedChromosomes, i);
            double minObjectiveValue = selectMinimumObjectiveValue(nondominatedChromosomes, i);

            for(int j = 1; j < size - 1; j++) if(nondominatedChromosomes[j].getCrowdingDistance() < Double.MAX_VALUE) nondominatedChromosomes[j].setCrowdingDistance(
                    nondominatedChromosomes[j].getCrowdingDistance() + (
                            (nondominatedChromosomes[j + 1].getObjectiveValues().get(i) - nondominatedChromosomes[j - 1].getObjectiveValues().get(i)) / (maxObjectiveValue - minObjectiveValue)
                    )
            );
        }
    }

    private static int dominates(final Solution chromosome1, final Solution chromosome2) {
        if(isDominant(chromosome1, chromosome2)) return DOMINANT;
        else if(isDominant(chromosome2, chromosome1)) return INFERIOR;
        else return NON_DOMINATED;
    }

    private static boolean isDominant(final Solution chromosome1, final Solution chromosome2) {
        boolean isDominant = true;
        boolean atleastOne = false;

        for(int i = 0; i < Configuration.numberOfObjetives; i++) {
            if(Configuration.objTypes[i]){
                if(chromosome1.getObjectiveValues().get(i) > chromosome2.getObjectiveValues().get(i)) {
                    isDominant = false;
                    break;
                } else if(!atleastOne && (chromosome1.getObjectiveValues().get(i) < chromosome2.getObjectiveValues().get(i))) atleastOne = true;
            }else {
                if(chromosome1.getObjectiveValues().get(i) < chromosome2.getObjectiveValues().get(i)) {
                    isDominant = false;
                    break;
                } else if(!atleastOne && (chromosome1.getObjectiveValues().get(i) > chromosome2.getObjectiveValues().get(i))) atleastOne = true;
            }
        }

        return isDominant && atleastOne;
    }


    public static void randomizedQuickSortForRank(final List<Solution> populace, final int head, final int tail) {
        if(head < tail) {
            int pivot = randomizedPartitionForRank(populace, head, tail);

            randomizedQuickSortForRank(populace, head, pivot - 1);
            randomizedQuickSortForRank(populace, pivot + 1, tail);
        }
    }

    public static void sortForCrowdingDistance(final List<Solution> populace, final int lastNonDominatedSetRank) {

        int rankStartIndex = -1;
        int rankEndIndex = -1;

        for(int i = 0; i < populace.size(); i++)
            if((rankStartIndex < 0) && (populace.get(i).getRank() == lastNonDominatedSetRank)) rankStartIndex = i;
            else if((rankStartIndex >= 0) && (populace.get(i).getRank() == lastNonDominatedSetRank)) rankEndIndex = i;

        randomizedQuickSortForCrowdingDistance(populace, rankStartIndex, rankEndIndex);
    }

    public static void sortAgainstObjective(final Solution[] populace, int objectiveIndex) {
        randomizedQuickSortAgainstObjective(populace, 0, populace.length - 1, objectiveIndex);
    }

    public static double selectMaximumObjectiveValue(final Solution[] populace, int objectiveIndex) {

        double result = populace[0].getObjectiveValues().get(objectiveIndex);

        for(Solution chromosome : populace) if(chromosome.getObjectiveValues().get(objectiveIndex) > result) result = chromosome.getObjectiveValues().get(objectiveIndex);

        return result;
    }

    public static double selectMinimumObjectiveValue(final Solution[] populace, int objectiveIndex) {

        double result = populace[0].getObjectiveValues().get(objectiveIndex);

        for(Solution chromosome : populace) if(chromosome.getObjectiveValues().get(objectiveIndex) < result) result = chromosome.getObjectiveValues().get(objectiveIndex);

        return result;
    }

    private static int randomizedPartitionForRank(final List<Solution> populace, final int head, final int tail) {

        swapForRank(populace, head, ThreadLocalRandom.current().nextInt(head, tail + 1));

        return partitionForRank(populace, head, tail);
    }

    private static void swapForRank(final List<Solution> populace, final int firstIndex, final int secondIndex) {

        Solution temporary = populace.get(firstIndex);

        populace.set(firstIndex, populace.get(secondIndex));
        populace.set(secondIndex, temporary);
    }

    private static int partitionForRank(final List<Solution> populace, final int head, final int tail) {

        int pivot = populace.get(tail).getRank();
        int pivotIndex = head;

        for(int j = head; j < tail; j++) {

            if(populace.get(j).getRank() <= pivot) {

                swapForRank(populace, pivotIndex, j);
                ++pivotIndex;
            }
        }

        swapForRank(populace, pivotIndex, tail);

        return pivotIndex;
    }

    private static void randomizedQuickSortForCrowdingDistance(final List<Solution> populace, final int head, final int tail) {

        if(head < tail) {

            int pivot = randomizedPartitionForCrowdingDistance(populace, head, tail);

            randomizedQuickSortForCrowdingDistance(populace, head, pivot - 1);
            randomizedQuickSortForCrowdingDistance(populace, pivot + 1, tail);
        }
    }

    private static int randomizedPartitionForCrowdingDistance(final List<Solution> populace, final int head, final int tail) {

        swapForCrowdingDistance(populace, head, ThreadLocalRandom.current().nextInt(head, tail + 1));

        return partitionForCrowdingDistance(populace, head, tail);
    }

    private static void swapForCrowdingDistance(final List<Solution> populace, final int firstIndex, final int secondIndex) {

        Solution temporary = populace.get(firstIndex);

        populace.set(firstIndex, populace.get(secondIndex));
        populace.set(secondIndex, temporary);
    }

    private static int partitionForCrowdingDistance(final List<Solution> populace, final int head, final int tail) {

        double pivot = populace.get(tail).getCrowdingDistance();
        int pivotIndex = head;

        for(int j = head; j < tail; j++) {

            if(populace.get(j).getCrowdingDistance() >= pivot) {

                swapForRank(populace, pivotIndex, j);
                ++pivotIndex;
            }
        }

        swapForRank(populace, pivotIndex, tail);

        return pivotIndex;
    }

    private static void randomizedQuickSortAgainstObjective(final Solution[] populace, final int head, final int tail, final int objectiveIndex) {
        if(head < tail) {

            int pivot = randomizedPartitionAgainstObjective(populace, head, tail, objectiveIndex);

            randomizedQuickSortAgainstObjective(populace, head, pivot - 1, objectiveIndex);
            randomizedQuickSortAgainstObjective(populace, pivot + 1, tail, objectiveIndex);
        }
    }

    private static int randomizedPartitionAgainstObjective(final Solution[] populace, final int head, final int tail, final int objectiveIndex) {
        swapAgainstObjective(populace, head, ThreadLocalRandom.current().nextInt(head, tail + 1));

        return partitionAgainstObjective(populace, head, tail, objectiveIndex);
    }

    private static void swapAgainstObjective(final Solution[] populace, final int firstIndex, final int secondIndex) {
        Solution temporary = populace[firstIndex];
        populace[firstIndex] = populace[secondIndex];
        populace[secondIndex] = temporary;
    }

    private static int partitionAgainstObjective(final Solution[] populace, final int head, final int tail, final int objectiveIndex) {
        double pivot = populace[tail].getObjectiveValues().get(objectiveIndex);
        int pivotIndex = head;

        for(int j = head; j < tail; j++) {

            if(populace[j].getObjectiveValues().get(objectiveIndex) <= pivot) {

                swapAgainstObjective(populace, pivotIndex, j);
                ++pivotIndex;
            }
        }

        swapAgainstObjective(populace, pivotIndex, tail);

        return pivotIndex;
    }
}
