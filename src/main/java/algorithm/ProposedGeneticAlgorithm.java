package algorithm;

import constant.Configuration;
import helper.Common;
import helper.Meta;
import model.*;

import java.text.ParseException;
import java.util.*;

public class ProposedGeneticAlgorithm {
    private Dataset dataset;
    private ArrayList<Location> locations;
    private ArrayList<Solution> population;
    private final int numberOfPopulation = 10;
    private ArrayList<Solution> offsprings;
    private int crossover = 60;
    private Solution elite;
    private int currentGeneration = 0;
    private Boolean log = false;

    public void setDataset(Dataset dataset) {
        this.dataset = dataset;
    }

    public void setLocations(ArrayList<Location> locations) {
        this.locations = locations;
    }

    public void start() throws CloneNotSupportedException, ParseException {
        this.setClusters();

        int numberOfGeneration = 1000;
        while (currentGeneration < numberOfGeneration){
            this.population = Meta.createNewPopulation(numberOfPopulation, dataset.getOrders().size());
            doOperators();
            decoding();
//            routing();
            calculateTardiness();
            mapElite();
            prepareNextGeneration();

            System.out.println(elite.getObjectiveValues().get(0));

            ++currentGeneration;
        }
    }

    private void prepareNextGeneration(){
        population = Meta.createNewPopulation(numberOfPopulation, dataset.getOrders().size());
    }

    private void setClusters(){
        this.dataset.getOrders().sort(Comparator.comparing(Order::getDueTimeID));

        this.dataset.getDueTimes().get(0).setStart(0);
        this.dataset.getDueTimes().get(this.dataset.getDueTimes().size() - 1).setEnd(this.dataset.getOrders().size()-1);

        int dueTimeID = this.dataset.getDueTimes().get(0).getId();
        int clusterCapacity = 0;
        for (int i = 0; i < this.dataset.getOrders().size(); i++) {
            Order o = this.dataset.getOrders().get(i);
            if(dueTimeID != o.getDueTimeID()){
                this.dataset.getDueTimes().get(o.getDueTimeID() - 1).setStart(i);
                this.dataset.getDueTimes().get(o.getDueTimeID() - 2).setEnd(i-1);
                this.dataset.getDueTimes().get(o.getDueTimeID() - 2).setCapacity(clusterCapacity);
                dueTimeID = o.getDueTimeID();
                clusterCapacity = o.getTotalWeight();
            }else{
                clusterCapacity += o.getTotalWeight();
            }
        }

        this.dataset.getDueTimes().get(dueTimeID - 1).setCapacity(clusterCapacity);

        if(log){
            System.out.println("Total weights : "+this.dataset.getTotalOfWeight());
            System.out.println("Capacity : "+this.dataset.getCapacity());
        }
        for (DueTime d: this.dataset.getDueTimes()) {
            d.setDivider((int) Math.ceil((double) d.getCapacity() / (double) this.dataset.getCapacity()));
            if(log){
                System.out.println(d.getStart()+" - "+d.getEnd()+" - weight : "+d.getCapacity()+" - total batches = "+d.getDivider());
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
            if(log){
                System.out.println("crossover on "+key1+" and "+key2);
            }
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
            if(log){
                System.out.println("mutation on "+key);
            }
        }
    }

    public void decoding() throws CloneNotSupportedException {
        for (Solution sol: population) {
//            reset due time
            ArrayList<DueTime> dues = new ArrayList<>();
            int idx = 0;
            DueTime dueTime = this.dataset.getDueTimes().get(idx).clone();
            dueTime.setOrders(new ArrayList<>());
            for (int i = 0; i < sol.getChromosome().size(); i++) {
                int priority = sol.getChromosome().get(i);
                Order order = this.dataset.getOrders().get(i).clone();
                order.setPriority(priority);

                dueTime.getOrders().add(order);
                if(i >= dueTime.getEnd()){
                    dueTime.getOrders().sort(Comparator.comparing(Order::getPriority));
//                    System.out.println(dueTime.getTime());
//                    for (Order o: dueTime.getOrders()) {
//                        System.out.println(o.getPriority());
//                    }
                    dues.add(dueTime);
                    if(i != sol.getChromosome().size() - 1){
                        ++idx;
                        dueTime = dataset.getDueTimes().get(idx).clone();
                        dueTime.setOrders(new ArrayList<>());
                    }
                }
            }

            sol.setDueTimes(dues);

            ArrayList<Batch> batches = new ArrayList<>();
            batches.add(new Batch());
            for (DueTime d: sol.getDueTimes()) {
                for (Order o: d.getOrders()) {
                    boolean flag = true;
                    for (Batch b: batches) {
                        if(b.getTotalWeight() + o.getTotalWeight() <= this.dataset.getCapacity()){
                            flag = false;
                            b.addOrder(o);
                            break;
                        }
                    }
                    if(flag){
                        Batch batch = new Batch();
                        batch.addOrder(o);
                        batches.add(batch);
                    }
                }
            }

            sol.setBatches(batches);
        }
    }

    public void routing(){
//        U shape routing
        for (Solution sol: this.population) {
            for (Batch b: sol.getBatches()) {
                for (Map.Entry<Integer, Integer> i: b.getIDs().entrySet()) {
                }
            }
        }
    }

    public void calculateTardiness() throws ParseException {
        int[] pickingTimes = {25, 30, 35};
        int loadingTime = 10;
        int unloadingTime = 10;

        String dateStr = Configuration.startTime;
        Date date = Common.convertStringToDate(dateStr);
        for (Solution sol: this.population) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int start = 0;
            int tardiness = 0;
            ArrayList<Double> objectiveValues = new ArrayList<>();
            for (Batch b: sol.getBatches()) {
                for (Map.Entry<Integer, Integer> end: b.getIDs().entrySet()) {
                    int travelTime = locations.get(start).getDistances().get(end.getKey());
                    int idx = new Random().nextInt(pickingTimes.length);
                    int pickingTime = pickingTimes[idx] * end.getValue();
                    calendar.add(Calendar.SECOND, pickingTime + travelTime + loadingTime + unloadingTime);
                }
                b.setEnd(calendar.getTime());
            }
            if(log){
                System.out.println(calendar.getTime());
            }

            for (Batch b: sol.getBatches()) {
                for (Order o: b.getOrders()) {
                    long diff = b.getEnd().getTime() - this.dataset.getDueTimes().get(o.getDueTimeID() - 1).getTimeObject().getTime();
                    long diffSeconds = diff / 1000;
//                    System.out.println(b.getEnd()+" - "+this.dataset.getDueTimes().get(o.getDueTimeID() - 1).getTimeObject());
//                    System.out.println(diffSeconds);
                    if(diffSeconds > 0){
                        tardiness += diffSeconds;
                    }
                }
            }
            objectiveValues.add((double) tardiness);
            sol.setObjectiveValues(objectiveValues);
        }
    }

    public void mapElite(){
        population.sort(Comparator.comparingDouble((Solution a) -> a.getObjectiveValues().get(0)));
        if(elite == null){
            elite = population.get(0);
        }else if (population.get(0).getObjectiveValues().get(0) < elite.getObjectiveValues().get(0)){
            elite = population.get(0);
        }
    }

    public Solution getElite() {
        return elite;
    }
}
