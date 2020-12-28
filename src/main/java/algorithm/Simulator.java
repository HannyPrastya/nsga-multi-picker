package algorithm;

import constant.Configuration;
import helper.Common;
import helper.ExcelExporter;
import model.Batch;
import model.Location;
import model.Picker;
import model.WaitingTime;

import java.text.ParseException;
import java.util.*;

public class Simulator {
    private ArrayList<Location> locations;
    private int numberOfPicker;
    private Picker[] pickers;
    private List<Batch> batches;
    private final Date startDate;
    private Integer[] aisles;
    private Integer numberOfAisle;
    private Integer rows;
    private Boolean log;
    private HashMap<Integer, Integer> traffic;
    private HashMap<Integer, Integer> congestion;

    public Simulator() throws ParseException {
        log = true;
        String dateStr = Configuration.startTime;
        startDate = Common.convertStringToDate(dateStr);
    }

    public void setLog(Boolean log) {
        this.log = log;
    }

    public void setNumberOfAisle(Integer numberOfAisle) {
        this.numberOfAisle = numberOfAisle;
        aisles = new Integer[this.numberOfAisle];

        for (int i = 0; i < this.numberOfAisle; i++) {
            aisles[i] = -1;
        }
    }

    public HashMap<Integer, Integer> getCongestion() {
        return congestion;
    }

    public void setRows(Integer rows) {
        this.rows = rows;
    }

    public void setLocations(ArrayList<Location> locations) {
        this.locations = locations;
    }

    public void setNumberOfPicker(int numberOfPicker) {
        this.numberOfPicker = numberOfPicker;
    }

    public void setBatches(List<Batch> batches) {
        this.batches = batches;
    }

    public Picker[] getPickers() {
        return pickers;
    }

    public HashMap<Integer, Integer> getTraffic() {
        return traffic;
    }

    public void start() {
        initPickers();
        boolean flag = true;
        while (flag){
            boolean finished = true;
            for (Picker picker : pickers) {
                if(!picker.isFinished()){
                    boolean movable = true;
                    if(picker.getState() == 3 && aisles[picker.getCurrentAisle()] >= 0){
                        movable = aisles[picker.getCurrentAisle()].equals(picker.getId());
                    }

                    if(movable){
                        picker.move();
                        if(picker.getState() == 3 || picker.getState() == 4 || (picker.getState() == 5 && picker.getRestSecond() > 0)){
                            if(aisles[picker.getCurrentAisle()] == -1){
                                aisles[picker.getCurrentAisle()] = picker.getId();
                            }
                        }else if(picker.getState() == 5 && picker.getRestSecond() <= 0){
                            aisles[picker.getCurrentAisle()] = -1;
                        }
                    }else{
                        picker.hold();
                    }
                }

                if (!picker.isFinished()) {
                    finished = false;
                }
            }

            if(finished){
                flag = false;
            }
        }
        calculateTraffic();
        calculateCongestion();
    }

    private void initPickers(){
        pickers = new Picker[numberOfPicker];
        for (int i = 0; i < pickers.length; i++) {
            pickers[i] = new Picker();
            pickers[i].setId(i);
            pickers[i].setLocations(locations);
            pickers[i].setRows(rows);
            pickers[i].getCalendar().setTime((Date) startDate.clone());
        }

        if(log){
            System.out.println("Batch size : "+batches.size());
        }

//        mapping Batch to picker
        for (int i = 0; i < batches.size(); i++) {
            int pickerIndex = i == 0 ? 0 : i % numberOfPicker;
            pickers[pickerIndex].getBatches().add(batches.get(i));
        }

        for (Picker picker : pickers) {
            if(log){
                System.out.println("First destination list : "+picker.getBatches().get(0).getRoutedIDs());
            }
        }
    }

    private void calculateTraffic(){
        traffic = new HashMap<>();

        for (Picker picker : pickers) {
            for (Map.Entry<Integer, Integer> entry : picker.getTraffic().entrySet()) {
                int groupIndex = entry.getKey();
                if(!traffic.containsKey(groupIndex)){
                    traffic.put(groupIndex, entry.getValue());
                }
                traffic.put(groupIndex, (traffic.get(groupIndex) + entry.getValue()));
            }
        }
    }

    public void calculateCongestion(){
        congestion = new HashMap<>();

        for (Picker picker : pickers) {
            for (WaitingTime time : picker.getWaitingTimes()) {
                int groupIndex = time.getAisle();
                if(!congestion.containsKey(groupIndex)){
                    congestion.put(groupIndex, time.getSeconds());
                }
                congestion.put(groupIndex, (congestion.get(groupIndex) + time.getSeconds()));
            }
        }
    }

    public void exportToExcel(){
        ExcelExporter excelExporter = new ExcelExporter();
        excelExporter.setColumns(new String[]{"ORIGIN", "DESTINATION", "AISLE ID","PICKER ID", "WAITING TIME (S)", "START TIME", "FINISH TIME"});

        for (Picker picker : pickers) {
            excelExporter.getWaitingTimes().addAll(picker.getWaitingTimes());
        }
        excelExporter.startExportWaitingTimes();

        excelExporter.setColumns(new String[]{"BATCH ID", "ORIGIN", "DESTINATION", "PICKER ID", "TASK", "TRAVELING TIME", "WAITING TIME",  "PICKING TIME", "TOTAL TIME", "START TIME", "FINISH TIME"});

        for (Picker picker : pickers) {
            excelExporter.getHistories().addAll(picker.getHistories());
        }

        excelExporter.startExportTimeline();
        excelExporter.export("Simulator");
    }
}