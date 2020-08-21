package model;

import java.util.Date;

public class History {
    private Integer pickerID;
    private Integer batchIndex;
    private Integer origin;
    private Integer destination;
    private String task;
    private Integer waitingTime;
    private Integer travelingTime;
    private Integer pickingTime;
    private Integer totalTime;
    private Date startTime;
    private Date endTime;

    public History(){
        waitingTime = 0;
        travelingTime = 0;
        pickingTime = 0;
        totalTime = 0;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public void setOrigin(Integer origin) {
        this.origin = origin;
    }

    public void setDestination(Integer destination) {
        this.destination = destination;
    }

    public void setPickerID(Integer pickerID) {
        this.pickerID = pickerID;
    }

    public void setBatchIndex(Integer batchIndex) {
        this.batchIndex = batchIndex;
    }

    public void setPickingTime(Integer pickingTime) {
        this.pickingTime = pickingTime;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public void setTravelingTime(Integer travelingTime) {
        this.travelingTime = travelingTime;
    }

    public void setWaitingTime(Integer waitingTime) {
        this.waitingTime = waitingTime;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Integer getOrigin() {
        return origin;
    }

    public Integer getDestination() {
        return destination;
    }

    public Integer getPickerID() {
        return pickerID;
    }

    public Date getEndTime() {
        return endTime;
    }

    public Integer getBatchIndex() {
        return batchIndex;
    }

    public Integer getPickingTime() {
        return pickingTime;
    }

    public String getTask() {
        return task;
    }

    public Integer getTravelingTime() {
        return travelingTime;
    }

    public Integer getWaitingTime() {
        return waitingTime;
    }

    public void setTotalTime(Integer totalTime) {
        this.totalTime = totalTime;
    }

    public Integer getTotalTime() {
        return totalTime;
    }
}
