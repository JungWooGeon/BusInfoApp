package com.selvashc.webtools.businfo.data;

/**
 * 버스 정보 (id, 번호, 타입(시내, 시외, 간선 등), 운행 지역)를 저장
 */
public class BusInfo {
    private String busId;
    private String busNum;
    private String busType;
    private String region;

    public BusInfo(String busId, String busNum, String busType) {
        this.busId = busId;
        this.busNum = busNum;
        this.busType = busType;
    }

    public BusInfo(String busId, String busNum, String busType, String region) {
        this.busId = busId;
        this.busNum = busNum;
        this.busType = busType;
        this.region = region;
    }

    public void setBusNum(String num) { busNum = num; }
    public String getBusId() { return this.busId; }
    public String getBusNum() {
        return this.busNum;
    }
    public String getBusType() {
        return this.busType;
    }
    public String getRegion() { return this.region;}
}