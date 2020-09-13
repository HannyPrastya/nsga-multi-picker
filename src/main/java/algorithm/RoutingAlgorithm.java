package algorithm;

import model.Batch;
import model.Location;

import java.util.*;

public class RoutingAlgorithm {

    private Integer totalAisle;
    private Integer numberOfVerticalAisle;
    private Integer numberOfHorizontalAisle;
    private ArrayList<Location> locations;

    public void setLocations(ArrayList<Location> locations) {
        this.locations = locations;
    }

    public void setTotalAisle(Integer totalAisle) {
        this.totalAisle = totalAisle;
    }

    public void setNumberOfHorizontalAisle(Integer numberOfHorizontalAisle) {
        this.numberOfHorizontalAisle = numberOfHorizontalAisle;
    }

    public void setNumberOfVerticalAisle(Integer numberOfVerticalAisle) {
        this.numberOfVerticalAisle = numberOfVerticalAisle;
    }

    public void determineAisles(Batch batch){
        batch.setAisles(new HashMap<>());
        for(Map.Entry<Integer,Integer> loc : batch.getIDs().entrySet()) {
            Integer groupID = locations.get(loc.getKey()).getGroupIndex();
            if (!batch.getAisles().containsKey(groupID)) {
                batch.getAisles().put(groupID, new ArrayList<>());
            }
            batch.getAisles().get(groupID).add(loc.getKey());
        }
    }

    public void routeUsingSShape(Batch batch){
//        get left
        ArrayList<Integer> routedAisles = new ArrayList<>();
        ArrayList<Integer> routedIDs = new ArrayList<>();

//        left number
        int left =  -1;
        int leftKey = -1;

//        pick most left
        for (Integer aisle : batch.getAisles().keySet()) {
            if(left == -1){
                leftKey = aisle;
                left = aisle == 0 ? 0 : (leftKey / (numberOfHorizontalAisle + 1)) == 0 ? 0 : (leftKey / (numberOfHorizontalAisle + 1));
            }
            if (aisle / (numberOfHorizontalAisle + 1) == left) {
                routedAisles.add(aisle);
                routedIDs.addAll(batch.getAisles().get(aisle));
//                routedAisles.put(aisle, batch.getAisles().get(aisle));
            } else {
                break;
            }
        }

//        top to bottom
        boolean toRight = true;
        boolean toTop = true;
        for (int i = numberOfHorizontalAisle; i >= 0; i--) {
            if(toRight){
                for (int j = left + 1; j < numberOfVerticalAisle; j++) {
                    int groupID = i + (j * (numberOfHorizontalAisle + 1));
//                    routedAisles.put(groupID, batch.getAisles().get(groupID));
                    if(batch.getAisles().containsKey(groupID)){
                        routedAisles.add(groupID);
                        if(!toTop){
                            Collections.reverse(batch.getAisles().get(groupID));
                        }
                        routedIDs.addAll(batch.getAisles().get(groupID));
                        if(!toTop) {
                            Collections.sort(batch.getAisles().get(groupID));
                        }
                        toTop = !toTop;
                    }
                    toRight = false;
                }
            }else{
                for (int j = numberOfVerticalAisle - 1; j >= left + 1; j--) {
                    int groupID = i + (j * (numberOfHorizontalAisle + 1));
                    if(batch.getAisles().containsKey(groupID)){
                        routedAisles.add(groupID);
                        if(!toTop){
                            Collections.reverse(batch.getAisles().get(groupID));
                        }
                        routedIDs.addAll(batch.getAisles().get(groupID));
                        if(!toTop) {
                            Collections.sort(batch.getAisles().get(groupID));
                        }
                        toTop = !toTop;
                    }
//                    routedAisles.put(groupID, batch.getAisles().get(groupID));
                    toRight = true;
                }
            }
        }

        batch.setRoutedIDs(routedIDs);
    }

    public void routingUsingLargestGap(Batch batch){
        ArrayList<Integer> routedIDs = new ArrayList<>();

//        left number
        int left =  -1;
        int leftKey = -1;

//        pick most left
        for (Integer aisle : batch.getAisles().keySet()) {
            if(left == -1){
                leftKey = aisle;
                left = aisle == 0 ? 0 : (leftKey / (numberOfHorizontalAisle + 1)) == 0 ? 0 : (leftKey / (numberOfHorizontalAisle + 1));
            }
            if (aisle / (numberOfHorizontalAisle + 1) == left) {
                routedIDs.addAll(batch.getAisles().get(aisle));
            } else {
                break;
            }
        }

//        top to bottom
        for (int i = numberOfHorizontalAisle; i >= 0; i--) {
            for (int j = left + 1; j < numberOfVerticalAisle; j++) {
                int groupID = i + (j * (numberOfHorizontalAisle + 1));
                if(batch.getAisles().containsKey(groupID)){
                    Collections.reverse(batch.getAisles().get(groupID));
                    for (Integer id : batch.getAisles().get(groupID)) {
                        if(locations.get(id).getDirection() == 1){
                            routedIDs.add(id);
                        }
                    }
                    Collections.sort(batch.getAisles().get(groupID));
                }
            }
            for (int j = numberOfVerticalAisle - 1; j >= left + 1; j--) {
                int groupID = i + (j * (numberOfHorizontalAisle + 1));
                if(batch.getAisles().containsKey(groupID)){
                    for (Integer id : batch.getAisles().get(groupID)) {
                        if(locations.get(id).getDirection() == 0){
                            routedIDs.add(id);
                        }
                    }
                }
            }
        }

        batch.setRoutedIDs(routedIDs);
    }

    public void routingUsingCombined(Batch batch){
        ArrayList<Integer> routedIDs = new ArrayList<>();
        Set<Integer> groups = batch.getAisles().keySet();

//        pick most left aisles
//        x = left
//        y = top
        int x = 0;
        int y = 0;
        int mostLeft = -1;
        int distance = 9999999;
        for (int i = 0; i <= numberOfHorizontalAisle; i++) {
            for (int j = 0; j < numberOfVerticalAisle; j++) {
                int groupID = i + (j * (numberOfHorizontalAisle + 1));
                if(batch.getAisles().containsKey(groupID)){
                    int dx = locations.get(0).getDistances().get(batch.getAisles().get(groupID).get(0));
                    if(distance > dx){
                        mostLeft = groupID;
                        x = j;
                        y = i;
                        distance = dx;
                    }
                    break;
                }
            }
        }
        routedIDs.addAll(batch.getAisles().get(mostLeft));
        groups.remove(mostLeft);
        distance = 9999999;
        y += 1;
//        pos = false => bottom
//        pos = true => top
        boolean pos = false;
        for (int i = y; i <= numberOfHorizontalAisle; i++) {
            for (int j = x; j >= 0; j--) {
                int groupID = i + (j * (numberOfHorizontalAisle + 1));
                if(batch.getAisles().containsKey(groupID)){
//                    check full or sub
                    ArrayList<Integer> nDir = new ArrayList<>();
                    for (Integer loc : batch.getAisles().get(groupID)) {
                        Integer dir = locations.get(loc).getDirection();
                        if(!nDir.contains(dir)){
                            nDir.add(dir);
                        }
                    }
                    if(nDir.size() == 2){
//                        full
                        if(pos){
//                            top
                            Collections.reverse(batch.getAisles().get(groupID));
                            routedIDs.addAll(batch.getAisles().get(groupID));
                            Collections.sort(batch.getAisles().get(groupID));
                        }else{
//                            bottom
                            routedIDs.addAll(batch.getAisles().get(groupID));
                        }
                        pos = !pos;
                    }else{
//                        sub
                        Integer dir = locations.get(batch.getAisles().get(groupID).get(0)).getDirection();
                        if(pos){
//                            top
                            Collections.reverse(batch.getAisles().get(groupID));
                            routedIDs.addAll(batch.getAisles().get(groupID));
                            Collections.sort(batch.getAisles().get(groupID));
                        }else{
//                            bottom
                            routedIDs.addAll(batch.getAisles().get(groupID));
                        }
                        if((dir == 0 && pos) || (dir == 1 && !pos)){
                            pos = !pos;
                        }
                    }
                    groups.remove(groupID);
                    x = j;
                }
            }
        }

        pos = locations.get(routedIDs.get(routedIDs.size() - 1)).getDirection() == 1;
        boolean toRight = true;
        for (int i = numberOfHorizontalAisle; i >= 0; i--) {
//            check most right
            int distanceRight = 999999;
            int distanceLeft = 999999;
            int mostRight = -1;
            mostLeft = 9999;
            for (int j = numberOfVerticalAisle - 1; j >= x + 1; j--) {
                int groupID = i + (j * (numberOfHorizontalAisle + 1));
                if(groups.contains(groupID)){
                    if(j > mostRight){
                        distanceRight = Math.abs(x - j);
                        mostRight = j;
                    }

                    if(j < mostLeft){
                        distanceLeft = Math.abs(x - j);
                        mostLeft = j;
                    }
                }
            }

            toRight = distanceLeft < distanceRight;

            if(toRight){
                for (int j = mostLeft; j <= mostRight; j++) {
                    int groupID = i + (j * (numberOfHorizontalAisle + 1));
                    if(groups.contains(groupID)){
                        ArrayList<Integer> nDir = new ArrayList<>();
                        for (Integer loc : batch.getAisles().get(groupID)) {
                            Integer dir = locations.get(loc).getDirection();
                            if(!nDir.contains(dir)){
                                nDir.add(dir);
                            }
                        }
                        if(nDir.size() == 2){
//                        full
                            if(pos){
//                            top
                                Collections.reverse(batch.getAisles().get(groupID));
                                routedIDs.addAll(batch.getAisles().get(groupID));
                                Collections.sort(batch.getAisles().get(groupID));
                            }else{
//                            bottom
                                routedIDs.addAll(batch.getAisles().get(groupID));
                            }
                            pos = !pos;
                        }else{
//                        sub
                            Integer dir = locations.get(batch.getAisles().get(groupID).get(0)).getDirection();
                            if(pos){
//                            top
                                Collections.reverse(batch.getAisles().get(groupID));
                                routedIDs.addAll(batch.getAisles().get(groupID));
                                Collections.sort(batch.getAisles().get(groupID));
                            }else{
//                            bottom
                                routedIDs.addAll(batch.getAisles().get(groupID));
                            }
                            if((dir == 0 && pos) || (dir == 1 && !pos)){
                                pos = !pos;
                            }
                        }
                        groups.remove(groupID);
                    }
                }
            }else{
                for (int j = mostRight; j >= mostLeft; j--) {
                    int groupID = i + (j * (numberOfHorizontalAisle + 1));
                    if(groups.contains(groupID)){
                        ArrayList<Integer> nDir = new ArrayList<>();
                        for (Integer loc : batch.getAisles().get(groupID)) {
                            Integer dir = locations.get(loc).getDirection();
                            if(!nDir.contains(dir)){
                                nDir.add(dir);
                            }
                        }
                        if(nDir.size() == 2){
//                        full
                            if(pos){
//                            top
                                Collections.reverse(batch.getAisles().get(groupID));
                                routedIDs.addAll(batch.getAisles().get(groupID));
                                Collections.sort(batch.getAisles().get(groupID));
                            }else{
//                            bottom
                                routedIDs.addAll(batch.getAisles().get(groupID));
                            }
                            pos = !pos;
                        }else{
//                        sub
                            Integer dir = locations.get(batch.getAisles().get(groupID).get(0)).getDirection();
                            if(pos){
//                            top
                                Collections.reverse(batch.getAisles().get(groupID));
                                routedIDs.addAll(batch.getAisles().get(groupID));
                                Collections.sort(batch.getAisles().get(groupID));
                            }else{
//                            bottom
                                routedIDs.addAll(batch.getAisles().get(groupID));
                            }
                            if((dir == 0 && pos) || (dir == 1 && !pos)){
                                pos = !pos;
                            }
                        }
                        groups.remove(groupID);
                    }
                }
            }

        }

        batch.setRoutedIDs(routedIDs);
    }
}
