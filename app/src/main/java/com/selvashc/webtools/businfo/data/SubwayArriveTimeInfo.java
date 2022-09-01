package com.selvashc.webtools.businfo.data;

public class SubwayArriveTimeInfo {
    private int subwayId;
    private String updownLine;
    private String trainLineNumber;
    private String arrivetTime;
    private String arriveMsg;

    public int getSubwayId() {
        return subwayId;
    }

    public void setSubwayId(int subwayId) {
        this.subwayId = subwayId;
    }

    public String getUpdownLine() {
        return updownLine;
    }

    public void setUpdownLine(String updownLine) {
        this.updownLine = updownLine;
    }

    public String getTrainLineNumber() {
        return trainLineNumber;
    }

    public void setTrainLineNumber(String trainLineNumber) {
        this.trainLineNumber = trainLineNumber;
    }

    public String getArrivetTime() {
        return arrivetTime;
    }

    public void setArrivetTime(String arrivetTime) {
        this.arrivetTime = arrivetTime;
    }

    public String getArriveMsg() {
        return arriveMsg;
    }

    public void setArriveMsg(String arriveMsg) {
        this.arriveMsg = arriveMsg;
    }
}
