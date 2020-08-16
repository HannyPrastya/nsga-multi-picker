package repository;

import java.util.ArrayList;
import java.util.List;

import helper.Common;
import model.Dataset;
import model.Item;
import model.Order;

public class DatasetRepository {
    private Dataset dataset = new Dataset();
    private ArrayList<Order> orders = new ArrayList<>();
    private int totalWeight;

    public void setDataset(Dataset d){
        dataset = d;
    }

    public Dataset getDataset() {
        return dataset;
    }

    public void createRandomOrders(){
        totalWeight = 0;
        for (int i = 0; i < dataset.getNumberOfOrders() ; i++) {
            Order order = new Order();
            ArrayList<Integer> itemIDs = new ArrayList<>();
            int weight = 0;

            int n = Common.randInt(2, dataset.getNumberOfItemPerOrder());

            for (int j = 0; j < n; j++){
                int id = Common.randInt(1, dataset.getItems().size()-1);
                itemIDs.add(id);
                weight += dataset.getItems().get(id).getWeight();
            }
            order.setTotalWeight(weight);
            order.setItemIDs(itemIDs);

            int dueTimeID = Common.randInt(1, dataset.getDueTimes().size());
            order.setDueTimeID(dueTimeID);

            totalWeight += weight;
            orders.add(order);
        }
        dataset.setOrders(orders);
        dataset.setTotalOfWeight(totalWeight);
    }


}
