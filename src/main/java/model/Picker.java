package model;

import helper.Common;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class Picker {
//    0. idle at depot
//    1. loading
//    2. traveling to entrance
//    3. to pick location
//    4. picking
//    5. out
//    6. back to depot
//    7. unloading
//    8. unloading
//    9. waiting
    private Integer state;
    private Integer currentItemIndex;
    private Integer currentBatchIndex;
    private Integer restSecond;
    private Integer origin;
    private Integer destination;
    private ArrayList<Batch> batches;
    private Calendar calendar;
    private Integer id;
    private Boolean finish;
    private Integer loadingTime;
    private Integer unloadingTime;
    private final int[] pickingTimes = new int[]{25, 30, 35};
    private Integer pickingTime;
    private ArrayList<Location> locations;
    private Integer rows;
    private Boolean log;
    private Integer velocity;
    private ArrayList<WaitingTime> waitingTimes;
    private ArrayList<History> histories;
    private Batch lastBatch;
    private HashMap<Integer, Integer> traffic;

//    0. S-Shape
//    1. Largest Gap
//    2. Combined +
    private Integer RoutingType;

    public Picker(){
        log = false;
        initPicker();
    }

    public void setLocations(ArrayList<Location> locations) {
        this.locations = locations;
    }

    public void setRows(Integer rows) {
        this.rows = rows;
    }

    public HashMap<Integer, Integer> getTraffic() {
        return traffic;
    }

    public void initPicker(){
        velocity = 3;
        origin = 0;
        destination = 0;
        state = 0;
        finish = false;
        currentBatchIndex = 0;
        currentItemIndex = 0;
        restSecond = 0;
        batches = new ArrayList<>();
        calendar = Calendar.getInstance();
        waitingTimes = new ArrayList<>();
        histories = new ArrayList<>();
        loadingTime = 300;
        unloadingTime = 300;
        traffic = new HashMap<>();
    }

    public ArrayList<Batch> getBatches() {
        return batches;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public ArrayList<WaitingTime> getWaitingTimes() {
        return waitingTimes;
    }

    public ArrayList<History> getHistories() {
        return histories;
    }

    public Integer getState() {
        return state;
    }

    public Integer getRestSecond() {
        return restSecond;
    }

    public boolean isFinished(){
        return finish && state == 0;
    }

    public Integer getDestination(){
        return (Integer) batches.get(currentBatchIndex).getRoutedIDs().get(currentItemIndex);
    }

    public Batch getCurrentBatch(){
        return batches.get(currentBatchIndex-1);
    }

    public void decideNextMove(){
//        from depot
        if(state == 0){
            destination = getDestination();
//        next item exists
        }else if(currentItemIndex < batches.get(currentBatchIndex).getRoutedIDs().size() - 1){
            currentItemIndex += 1;
            origin = destination;
            destination = getDestination();
//        next batch exists
        }else if(currentBatchIndex < batches.size() - 1){
            currentItemIndex = 0;
            currentBatchIndex += 1;
            origin = destination;
            destination = 0;
            if(log){
                System.out.println("NEXT BATCH : "+batches.get(currentBatchIndex).getRoutedIDs());
            }
//        there is no anything left
        }else {
            origin = destination;
            destination = 0;
            currentBatchIndex += 1;
            finish = true;
        }
    }

    public Integer getStart(){
        Location start = locations.get(origin);
        Location end = locations.get(destination);

        if(origin != 0){
            if(start.getRowIndex() < end.getRowIndex()){
                if(start.getDirection() == 1){
                    return start.getShortest();
                }else if(start.getDirection() == 0){
                    return start.getOpposite();
                }
            }else if(start.getRowIndex() > end.getRowIndex()){
                if(start.getDirection() == 0){
                    return start.getShortest();
                }else if(start.getDirection() == 1){
                    return start.getOpposite();
                }
            }else{
                return start.getShortest();
            }
        }
        return 0;
    }

    public Integer getEnd(){
        Location start = locations.get(origin);
        Location end = locations.get(destination);
        if(destination != 0){
            if(start.getRowIndex() < end.getRowIndex()){
                if(end.getDirection() == 1){
                    return end.getOpposite();
                }else if(end.getDirection() == 0){
                    return end.getShortest();
                }
            }else if(start.getRowIndex() > end.getRowIndex()){
                if(end.getDirection() == 1){
                    return end.getShortest();
                }else if(start.getDirection() == 0){
                    return end.getOpposite();
                }
            }else{
//                    SAME
                return end.getOpposite();
            }
        }
        return 0;
    }

    public void checkLocation(){
        if(locations.get(origin).getDistances().get(destination) == 0){
            state = 4;
            restSecond = pickingTimes[Common.randInt(0, pickingTimes.length - 1)] * batches.get(currentBatchIndex).getIDs().get(destination);
            createNewHistory("picking opposite side");
            histories.get(histories.size() - 1).setPickingTime(restSecond);
            if(log){
                System.out.println("ANOTHER PICK - 4 : "+restSecond);
            }
        }else if(locations.get(origin).getGroupIndex().equals(locations.get(destination).getGroupIndex())){
            state = 3;
            restSecond = locations.get(origin).getDistances().get(destination);
            createNewHistory("picking same aisle");
            if(log){
                System.out.println("TO PICK SAME AISLE - 3 : "+restSecond);
            }
        }else{
            state = 5;
            restSecond = getStart() * velocity;

            if(destination == 0){
                createNewHistory("back to depot");
            }else{
                createNewHistory("picking another aisle");
            }

            if(log){
                System.out.println("GO OUT - 5 : "+restSecond);
            }
        }
    }

    public void move(){
        if(finish && state == 0){
            return;
        }
        if(restSecond <= 0){
            if(state == 0){
                decideNextMove();
                state = 1;
                restSecond = loadingTime;
                createNewHistory("loading");
                if(log){
                    System.out.println("FROM DEPOT"+" TO "+destination+" WITH LOADING TIME : "+restSecond);
                }
            }else if(state == 1){
                state = 2;
                restSecond = locations.get(origin).getDistances().get(destination);

                restSecond -= getStart();
                restSecond -= getEnd();
                createNewHistory("picking another aisle");
                restSecond = restSecond * velocity;
                watchTraffic();
                if(log){
                    System.out.println("TRAVELING - 2 : "+restSecond+" WITH TOTAL : "+locations.get(origin).getDistances().get(destination));
                }
            }else if(state == 2){
                state = 3;

                restSecond = getEnd();
                restSecond = restSecond * velocity;

                if(log){
                    System.out.println("TO PICK - 3 : "+restSecond);
                }
            }else if(state == 3){
                state = 4;
                restSecond = pickingTimes[Common.randInt(0, pickingTimes.length - 1)] * batches.get(currentBatchIndex).getIDs().get(destination);
                histories.get(histories.size() - 1).setPickingTime(restSecond);

                if(log){
                    System.out.println("PICKING - 4 : "+restSecond);
                }
            }else if(state == 4){
                decideNextMove();
                checkLocation();
                if(state != 4){
                    restSecond = restSecond * velocity;
                }
            } else if(state == 5){
                if(destination == 0){
                    state = 6;
                    if(log){
                        System.out.println("TRAVELING BACK TO DEPOT - 6 : "+restSecond+" WITH TOTAL : "+locations.get(origin).getDistances().get(destination));
                    }
                }else{
                    state = 2;
                    watchTraffic();
                    if(log){
                        watchTraffic();
                        System.out.println("TRAVELING - 2 : "+restSecond+" WITH TOTAL : "+locations.get(origin).getDistances().get(destination));
                    }
                }
                restSecond = locations.get(origin).getDistances().get(destination);

                restSecond -= getStart();
                restSecond -= getEnd();
                restSecond = restSecond * velocity;
            }else if(state == 6){
                state = 7;
                restSecond = unloadingTime;
                createNewHistory("unloading");
                histories.get(histories.size() - 1).setTotalTime(restSecond);
                if(log){
                    System.out.println("UNLOADING - 7 : "+restSecond);
                }
            }else if(state == 7){
                origin = 0;
                getCurrentBatch().setEnd((Date) calendar.getTime().clone());
                if(finish){
                   state = 0;
                   destination = null;
                    if(log) {
                        System.out.println("MAKE SURE IDLE");
                    }
                } else {
                    state = 1;
                    restSecond = loadingTime;
                    destination = getDestination();
                    createNewHistory("loading");
                    if(log){
                        System.out.println("FROM DEPOT "+" TO "+destination+" WITH LOADING TIME : "+restSecond);
                    }
                }
            }
        }
        History latest = histories.get(histories.size() - 1);

        if(state != 4 && state != 1 && state != 7 && state != 0){
            latest.setTravelingTime(latest.getTravelingTime() + 1);
        }

//        System.out.println(restSecond);
        if(state != 0){
            restSecond -= 1;
            calendar.add(Calendar.SECOND, 1);
        }
        latest.setEndTime((Date) calendar.getTime().clone());
    }

    public Integer getCurrentAisle(){
        if(state == 3 || state == 4){
            return locations.get(destination).getGroupIndex();
        }else if(state == 5){
            return locations.get(origin).getGroupIndex();
        }else{
            return -1;
        }
    }

    public void hold(){
        calendar.add(Calendar.SECOND, 1);
        if(waitingTimes.size() == 0){
            createNewWaitingTime();
        }else{
            if (!waitingTimes.get(waitingTimes.size() - 1).getAisle().equals(getCurrentAisle())) {
                createNewWaitingTime();
            }
        }
        History latest = histories.get(histories.size() - 1);

        latest.setWaitingTime(latest.getWaitingTime() + 1);

        waitingTimes.get(waitingTimes.size() - 1).addSecond();
        waitingTimes.get(waitingTimes.size() - 1).setEndTime((Date) calendar.getTime().clone());
    }

    public void watchTraffic(){
        int groupIndex = locations.get(destination).getGroupIndex();
        if(!traffic.containsKey(groupIndex)){
            traffic.put(groupIndex, 1);
        }
        traffic.put(groupIndex, (traffic.get(groupIndex) + 1));
    }

    public void createNewWaitingTime(){
        WaitingTime waitingTime = new WaitingTime();
        waitingTime.setAisle(getCurrentAisle());
        waitingTime.setPickerID(id);
        waitingTime.setDestination(destination);
        waitingTime.setOrigin(origin);
        waitingTime.setStartTime((Date) calendar.getTime().clone());
        waitingTimes.add(waitingTime);
    }

    public void createNewHistory(String type){
        History h = new History();
        h.setPickerID(id);
        h.setOrigin(origin);
        h.setDestination(destination);
        h.setBatchIndex(currentBatchIndex);
        h.setStartTime((Date) calendar.getTime().clone());

        if(type.equals("loading") || type.equals("unloading")){
            h.setTotalTime(restSecond);
        }
        h.setTask(type.toUpperCase());

        histories.add(h);
    }
}
