package com.selvashc.webtools.businfo.data;

/**
 * 버스 도착 정보 (도착 시간, 버스 번호) 저장
 */
public class BusArriveTimeInfo {
    private String arriveTime;
    private String busName;

    public BusArriveTimeInfo(String arriveTime, String busName) {
        this.arriveTime = arriveTime;
        this.busName = busName;
    }

    public void setBusName(String busName) { this.busName = busName; }
    public void setArriveTime(String arriveTime) { this.arriveTime = arriveTime; }
    public String getArriveTime() { return this.arriveTime; }
    public String getBusName() { return this.busName; }
}
