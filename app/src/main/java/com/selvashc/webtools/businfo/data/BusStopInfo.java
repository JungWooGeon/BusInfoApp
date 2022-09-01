package com.selvashc.webtools.businfo.data;

/**
 * 버스 정류장 정보 (이름, ID) 를 저장
 */
public class BusStopInfo {
    private String busStopName;
    private String busStopId;

    public String getBusStopName() { return busStopName; }
    public String getBusStopId() { return busStopId; }
    public void setBusStopName(String busStopName) { this.busStopName = busStopName; }
    public void setBusStopId(String busStopId) { this.busStopId = busStopId; }
}
