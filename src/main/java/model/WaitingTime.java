package model;

import java.util.Date;

public class WaitingTime {
    private Integer pickerID;
    private Integer destination;
    private Integer aisle;
    private Integer seconds;
    private Integer origin;
    private Date startTime;
    private Date endTime;

    public WaitingTime(){
        seconds = 0;
    }

    public void setOrigin(Integer origin) {
        this.origin = origin;
    }

    public Integer getOrigin() {
        return origin;
    }

    public void setPickerID(Integer pickerID) {
        this.pickerID = pickerID;
    }

    public Integer getPickerID() {
        return pickerID;
    }

    public void setAisle(Integer aisle) {
        this.aisle = aisle;
    }

    public Integer getAisle() {
        return aisle;
    }

    public void setDestination(Integer destination) {
        this.destination = destination;
    }

    public Integer getDestination() {
        return destination;
    }

    public void setSeconds(Integer seconds) {
        this.seconds = seconds;
    }

    public Integer getSeconds() {
        return seconds;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void addSecond(){
        seconds += 1;
    }
}
