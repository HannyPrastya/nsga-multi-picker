package helper;

import model.History;
import model.WaitingTime;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelExporter {
    private String[] columns;
    private List<List<Integer>> integerDataset;
    private List<List<Double>> doubleDataset;
    private List<HashMap<Integer, Integer>> hashIntegerDataset;
    private List<WaitingTime> waitingTimes;
    private List<History> histories;
    private Workbook workbook;
    private CreationHelper helper;

    public ExcelExporter(){
        workbook = new XSSFWorkbook();
        helper = workbook.getCreationHelper();
        waitingTimes = new ArrayList<>();
        histories =  new ArrayList<>();
    }

    public void setColumns(String[] columns) {
        this.columns = columns;
    }

    public List<WaitingTime> getWaitingTimes() {
        return waitingTimes;
    }

    public List<History> getHistories() {
        return histories;
    }

    public void setDoubleDataset(List<List<Double>> doubleDataset) {
        this.doubleDataset = doubleDataset;
    }

    public void setHashIntegerDataset(List<HashMap<Integer, Integer>> hashIntegerDataset) {
        this.hashIntegerDataset = hashIntegerDataset;
    }

    public void startExportWaitingTimes(){
        Sheet sheet = workbook.createSheet("Waiting Times");

        Row headerRow = sheet.createRow(0);
        for(int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
        }

        for (int i = 0; i < waitingTimes.size(); i++) {
            WaitingTime wt = waitingTimes.get(i);
            Row row = sheet.createRow(i + 1);

            row.createCell(0).setCellValue(wt.getOrigin());
            row.createCell(1).setCellValue(wt.getDestination());
            row.createCell(2).setCellValue(wt.getAisle());
            row.createCell(3).setCellValue(wt.getPickerID());
            row.createCell(4).setCellValue(wt.getSeconds());
            row.createCell(5).setCellValue(Common.convertTimeToString(wt.getStartTime()));
            row.createCell(6).setCellValue(Common.convertTimeToString(wt.getEndTime()));
        }

        for(int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    public void startExportTimeline(){
        Sheet sheet = workbook.createSheet("Timeline");

        Row headerRow = sheet.createRow(0);
        for(int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
        }

        for (int i = 0; i < histories.size(); i++) {
            History history = histories.get(i);
            Row row = sheet.createRow(i + 1);

            if(history.getTask().equals("LOADING")){
            }else if(history.getTask().equals("UNLOADING")){
            }else if(history.getTask().equals("BACK TO DEPOT")){
                row.createCell(1).setCellValue(history.getOrigin());
                row.createCell(2).setCellValue(history.getDestination());
            }else{
                row.createCell(0).setCellValue(history.getBatchIndex());
                row.createCell(1).setCellValue(history.getOrigin());
                row.createCell(2).setCellValue(history.getDestination());
            }
            row.createCell(3).setCellValue(history.getPickerID());
            row.createCell(4).setCellValue(history.getTask());
            row.createCell(5).setCellValue(history.getTravelingTime());
            row.createCell(6).setCellValue(history.getWaitingTime());
            row.createCell(7).setCellValue(history.getPickingTime());
            row.createCell(8).setCellValue(history.getTotalTime()+history.getTravelingTime()+history.getPickingTime()+history.getWaitingTime());
            row.createCell(9).setCellValue(Common.convertTimeToString(history.getStartTime()));
            row.createCell(10).setCellValue(Common.convertTimeToString(history.getEndTime()));
        }

        for(int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    public void startDoubleDataset(String name){
        Sheet sheet = workbook.createSheet(name);

        Row headerRow = sheet.createRow(0);
        for(int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
        }

        for (int i = 0; i < doubleDataset.size(); i++) {
            List<Double> r = doubleDataset.get(i);
            Row row = sheet.createRow(i + 1);

            row.createCell(0).setCellValue(i+1);
            for (int j = 0; j < r.size(); j++) {
                row.createCell(j+1).setCellValue(r.get(j));
            }
        }

        for(int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    public void startHashIntegerDataset(String name){
        Sheet sheet = workbook.createSheet(name);

        Row headerRow = sheet.createRow(0);
        for(int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
        }

        for (int i = 0; i < hashIntegerDataset.size(); i++) {
            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(i+1);
            HashMap<Integer, Integer> data = hashIntegerDataset.get(i);

            for(Map.Entry<Integer,Integer> entry : data.entrySet()) {
                row.createCell(entry.getKey()+1).setCellValue(entry.getValue());
            }
        }

        for(int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    public void export(String filename){
        try {
            FileOutputStream fileOut = new FileOutputStream("src/main/resources/"+filename+".xlsx");
            workbook.write(fileOut);
            fileOut.close();
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
