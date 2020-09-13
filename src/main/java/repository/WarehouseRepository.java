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
        createMap();

//        for (Location location : locations) {
//            System.out.println(location.getX()+","+location.getY());
//        }

        calculateDistances();

//        for (int i = 0; i < locations.size(); i++) {
//            Location loc = locations.get(i);
//            for (int j = 0; j < loc.getDistances().size(); j++) {
//                System.out.println("From point "+i+"("+loc.getX()+","+loc.getY()+") - "+loc.getRowIndex()+"/"+loc.getDirection()+","+loc.getIndex()+" to "+j+"("+locations.get(j).getX()+","+locations.get(j).getY()+")- "+locations.get(j).getRowIndex()+"/"+locations.get(j).getDirection()+","+locations.get(j).getIndex()+" : "+loc.getDistances().get(j));
//            }
//        }
    }

    public void createMap(){
        int x = 0;
        int y = 0;

        Location depot = new Location();
        depot.setX(x);
        depot.setY(y);
        depot.setRowIndex(-1);
        depot.setDirection(-1);
        depot.setGroupIndex(-1);
        locations.add(depot);

        int groupIndex = 0;
        boolean flip = false;
        int flipper = ((warehouse.getNumberOfRows()*warehouse.getNumberOfHorizontalAisle())+(warehouse.getNumberOfHorizontalAisle()*2)+1)+warehouse.getNumberOfRows();

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
                    int dir = (double) (k+1) > (double) warehouse.getNumberOfRows()/ (double) 2 ? 1 : 0;
//                    dir
//                    0. shorter from bottom
//                    1. shorter from top
//                    position
//                    0. left
//                    1. right
                    int fy = y+k;
                    int rowIndex = j;
                    int index = k + 1;
                    if(flip){
                        fy = Math.abs(fy - flipper);
                        rowIndex = Math.abs(j - warehouse.getNumberOfHorizontalAisle());
                        index = Math.abs(index - (warehouse.getNumberOfRows() + 1));
                        dir = Math.abs(dir - 1);
                    }

                    int shortest = dir == 1 ? index - (warehouse.getNumberOfRows() - 1) : index;
                    Location locLeft = new Location();
                    locLeft.setX(x);
                    locLeft.setY(fy);
                    locLeft.setIndex(index);
                    locLeft.setDirection(dir);
                    locLeft.setRowIndex(rowIndex);
                    locLeft.setOpposite(warehouse.getNumberOfRows() - shortest);
                    locLeft.setWeight((int )(Math.random() * 3 + 1));
                    locLeft.setPosition(0);
                    locLeft.setShortest(shortest);
                    locLeft.setGroupIndex(groupIndex);
                    locations.add(locLeft);

                    Location locRight = new Location();
                    locRight.setX(x+2);
                    locRight.setY(fy);
                    locRight.setIndex(index);
                    locRight.setDirection(dir);
                    locRight.setRowIndex(rowIndex);
                    locRight.setOpposite(warehouse.getNumberOfRows() - shortest);
                    locRight.setWeight((int )(Math.random() * 3 + 1));
                    locRight.setPosition(1);
                    locRight.setShortest(shortest);
                    locRight.setGroupIndex(groupIndex);
                    locations.add(locRight);
                }

                groupIndex += 1;
            }
            maxX = x+3;
            maxY = y+warehouse.getNumberOfRows()+3;
            flip = !flip;
        }
    }

    public void calculateDistances(){
        for (int i = 0; i < locations.size(); i++){
            Location start = locations.get(i);
            ArrayList<Integer> distances = new ArrayList<>();

            for (int j = 0; j < locations.size(); j++) {
                Location end = locations.get(j);

                int startX = start.getX();
                int startY = start.getY();
                int endX = end.getX();
                int endY = end.getY();

                int distance = 0;

                if (i != 0) {
//                    left
                    if (start.getPosition() == 0) {
                        startX += 1;
                    } else {
                        startX -= 1;
                    }
                }

                if (j != 0) {
//                    left
                    if (end.getPosition() == 0) {
                        endX += 1;
                    } else {
                        endX -= 1;
                    }
                }

                if (start.getRowIndex().equals(end.getRowIndex())) {
                    if(startX == endX){
                        distance += Math.abs(startY-endY)+Math.abs(startX-endX);
                    }else{
//                        if(start.getX() != end.getX()){

                        distance += Math.abs(startX - endX);
                        if((start.getDirection().equals(end.getDirection()))){
                            if(start.getDirection() == 1){
                                distance += (warehouse.getNumberOfRows() - (start.getIndex() - 1)) + (warehouse.getNumberOfRows() - (end.getIndex() - 1));
                            }else{
                                distance += start.getIndex() + end.getIndex();
                            }
                        }else{
                            distance += start.getIndex() + end.getIndex();
                        }
                    }
                }else{
                    distance += Math.abs(startY-endY)+Math.abs(startX-endX);
                }
                distances.add(distance);
            }
            start.setDistances(distances);
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
