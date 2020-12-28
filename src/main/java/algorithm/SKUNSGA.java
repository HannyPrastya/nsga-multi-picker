package algorithm;

import constant.Configuration;
import helper.Common;
import helper.ExcelExporter;
import helper.Meta;
import model.*;

import java.text.ParseException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class SKUNSGA extends NonSortedGeneticAlgorithm {
    private List<OrderGroup> groups;

    public SKUNSGA() throws ParseException {
    }

    public void start() throws CloneNotSupportedException, ParseException {
        report = new ArrayList<>();
        reportF1 = new ArrayList<>();
        this.groupingSKUs();
        this.population = Meta.createNewPopulation(numberOfPopulation, groups.size());

        while (currentGeneration < numberOfGeneration) {
            doOperators();
            population.addAll(offsprings);
            decoding();
            routing();
            simulate();
            preparePopulation();
            population = getChildrenFromCombinedPopulation();
            report();
            reportFrontier1();

            if(log){
                System.out.println("//after top population");
                population.forEach(solution -> {
                    System.out.println(solution.getObjectiveValues());
                });
                System.out.println("////////////////////////");
            }
            ++currentGeneration;
        }

        System.out.println("LAST");
//        population.forEach(solution -> {
//            System.out.println(solution.getRank());
//            System.out.println(solution.getDominatedSolutions().size());
//        });

        exportExcel();
    }

    public void groupingSKUs(){
        this.groups = new ArrayList<>();
        for (int i = 0; i < this.dataset.getOrders().size(); i++) {
            Order order = this.dataset.getOrders().get(i);
            boolean flag = true;
            found:
            for (OrderGroup group : groups) {
                for (Order order1 : group.getOrders()) {
                    for (Integer itemID : order1.getItemIDs()) {
                        if (itemID.equals(order.getItemIDs().get(0))) {
                            if (group.getWeight() + order1.getTotalWeight() <= dataset.getCapacity()) {
                                flag = false;
                                group.add(order);
                                break found;
                            }else {
                                break;
                            }
                        }
                    }
                }
            }

            if (flag) {
                groups.add(new OrderGroup());
                groups.get(groups.size() - 1).add(order);
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
            int x1 = Common.randInt(0, range.size() - 1);
            int key1 = range.get(x1);
            range.remove(x1);

            int x2 = Common.randInt(0, range.size() - 1);
            int key2 = range.get(x2);
            range.remove(x2);

            Solution offA = population.get(key1).clone();
            Solution offB = population.get(key2).clone();

            int start = Common.randInt(0, offA.getChromosome().size() - 2);
            int end = Common.randInt(start + 1, offA.getChromosome().size() - 1);

            for (int i = start; i <= end; i++) {
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
            int x = Common.randInt(0, range.size() - 1);
            int key = range.get(x);
            range.remove(x);
            Solution off = population.get(key).clone();

            ArrayList<Integer> list = new ArrayList<>();

            int start = Common.randInt(0, off.getChromosome().size() - 2);
            int end = Common.randInt(start + 1, off.getChromosome().size() - 1);

            for (int i = start; i <= end; i++) {
                list.add(off.getChromosome().get(i));
            }
            Collections.shuffle(list);
            int j = 0;

            for (int i = start; i <= end; i++) {
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
                ArrayList<Order> orders = new ArrayList<>();
                for (int i = 0; i < sol.getChromosome().size(); i++) {
                    int priority = sol.getChromosome().get(i);
                    groups.get(i).setPriority(priority);
                }

                groups.sort(Comparator.comparing(OrderGroup::getPriority));

                ArrayList<Batch> batches = new ArrayList<>();
                batches.add(new Batch());
                for (OrderGroup group : groups) {
                    for (int i = 0; i < group.getOrders().size(); i++) {
                        Order o = group.getOrders().get(i).clone();
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
}
