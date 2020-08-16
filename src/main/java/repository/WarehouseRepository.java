package repository;

import java.util.ArrayList;

import model.Item;
import model.Location;
import model.Warehouse;

public class WarehouseRepository {

    private ArrayList<Location> locations;
    private int maxY;
    private int maxX;
    private final Warehouse warehouse;
    private ArrayList<Item> items;

    public WarehouseRepository(){
//        set new objects
        warehouse = new Warehouse();
        items = new ArrayList<>();
        locations = new ArrayList<>();
    }

    public void createLocations() {
//        get locations
        calculateDistances();
        getShortestDistance();
    }

    public void calculateDistances(){
        int x = 1;
        int y = 1;

        Location depot = new Location();
        depot.setX(x);
        depot.setY(y);
        locations.add(depot);

        for(int i = 0; i < warehouse.getNumberOfVerticalAisle(); i++){
//            1 = wall left
//            2 * i = pods
//            1 * (i+1) = space
            x = 1+(3*i);
            for(int j = 0; j <= warehouse.getNumberOfHorizontalAisle(); j++){
//                1 = wall bottom
//                warehouse.getNumberOfRows()*j = pods
//                1 * (j+1) = space
                y = (warehouse.getNumberOfRows()*j)+(j*2)+1;
                for(int k = 0; k < warehouse.getNumberOfRows(); k++) {
                    int dir = k+1 > warehouse.getNumberOfRows()/2 ? 1 : 0;
                    Location locLeft = new Location();
                    locLeft.setX(x);
                    locLeft.setY(y+k);
                    locLeft.setIndex(k+1);
//                    go top or bottom for shortest direction
                    locLeft.setDirection(dir);
                    locLeft.setRowIndex(j);
                    locLeft.setWeight((int )(Math.random() * 3 + 1));
                    locLeft.setPosition(0);
                    locations.add(locLeft);

                    Location locRight = new Location();
                    locRight.setX(x+2);
                    locRight.setY(y+k);
                    locRight.setIndex(k+1);
                    locRight.setDirection(dir);
                    locRight.setRowIndex(j);
                    locRight.setPosition(1);
                    locRight.setWeight((int )(Math.random() * 3 + 1));
                    locations.add(locRight);
                }
            }
            maxX = x+3;
            maxY = y+warehouse.getNumberOfRows()+3;
        }
    }

    public void getShortestDistance(){
        for (int i = 0; i < locations.size(); i++){
            Location start = locations.get(i);
            ArrayList<Integer> distances = new ArrayList<>();

            for (int j = 0; j < locations.size(); j++){
                Location end = locations.get(j);
                int startX = start.getX() + (i != 0 ? (start.getPosition() == 0 ? -1 : 1) : 0);
                int endX = end.getX() + (j != 0 ? (end.getPosition() == 0 ? -1 : 1) : 0);

                int distance = 0;

//                is in one row?
                if(start.getRowIndex() == end.getRowIndex()){
                    if(Math.abs(start.getX()-end.getX()) == 2){
                        distance += Math.abs(start.getY()-end.getY())+Math.abs(startX-endX);
                    }else{
                        if(start.getX() != end.getX()){
                            if((start.getDirection() == end.getDirection()) && start.getDirection() == 1){
                                distance += (warehouse.getNumberOfRows()-start.getIndex())+(warehouse.getNumberOfRows()-end.getIndex())+2;
                            }else{
                                distance += start.getIndex()+end.getIndex();
                            }
                            distance += Math.abs(startX-endX);
                        }else{
                            distance += Math.abs(start.getY()-end.getY());
                        }
                    }
                }else{
                    distance += Math.abs(start.getY()-end.getY())+Math.abs(startX-endX);
                }
                distances.add(distance);
            }
            start.setDistances(distances);
        }
    }

    public void createRandomItems(){
        for (int i = 0; i < locations.size()-1; i++) {
            Item item = new Item();
            int w = (int) Math.round((Math.random() * 9) + 1);
            item.setWeight(w);
            items.add(item);
        }
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public ArrayList<Location> getLocations() {
        return locations;
    }

    public int getMaxX() {
        return maxX;
    }

    public int getMaxY() {
        return maxY;
    }
}
