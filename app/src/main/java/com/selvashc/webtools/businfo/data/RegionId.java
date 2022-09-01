package com.selvashc.webtools.businfo.data;

import android.annotation.SuppressLint;
import android.content.res.Resources;

import com.selvashc.webtools.businfo.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Singleton Pattern으로 지역ID와 이름을 저장하고 있고, MainActivity onCreate에서 최초 실행되어 값을 셋팅
 * 지역 이름과 지역 ID를 서로 변환할 수 있도록 하는 역할
 */
public class RegionId {

    private static final int GYEONGGI_ID = 0;
    private static final int SEJONG_ID = 12;
    private static final int DAEGU_ID = 22;
    private static final int INCHEON_ID = 23;
    private static final int GWANGJU_ID = 24;
    private static final int DAEJEON_ID = 25;
    private static final int ULSAN_ID = 26;
    private static final int JEJU_ID = 39;
    private static final int WONJU_ID = 32020;
    private static final int BUSAN_ID = -1;

    private static RegionId instance;

    @SuppressLint("UseSparseArrays")
    private Map<Integer, String> regionIdName = new HashMap<>();

    public int getGyeonggiId() { return GYEONGGI_ID; }
    public int getSejongId() { return SEJONG_ID; }
    public int getDaeguId() { return DAEGU_ID; }
    public int getIncheonId() { return INCHEON_ID; }
    public int getGwangjuId() { return GWANGJU_ID; }
    public int getDaejeonId() { return DAEJEON_ID; }
    public int getUlsanId() { return ULSAN_ID;}
    public int getJejuId() { return JEJU_ID; }
    public int getWonjuId() { return WONJU_ID; }
    public int getBusanId() { return BUSAN_ID; }

    public Map<Integer, String> getRegionIdName() { return regionIdName; }
    public int getRegionId(String name) {
        for (Map.Entry<Integer, String> entry : regionIdName.entrySet())
            if (entry.getValue().equals(name)) return entry.getKey();
        return -1;
    }

    private RegionId() { }

    public static RegionId getInstance(Resources r) {
        if (instance == null) {
            instance = new RegionId();
            instance.regionIdName.put(SEJONG_ID, r.getString(R.string.sejong));
            instance.regionIdName.put(DAEGU_ID, r.getString(R.string.daegu));
            instance.regionIdName.put(INCHEON_ID, r.getString(R.string.incheon));
            instance.regionIdName.put(GWANGJU_ID, r.getString(R.string.gwangju));
            instance.regionIdName.put(DAEJEON_ID, r.getString(R.string.daejeon));
            instance.regionIdName.put(ULSAN_ID, r.getString(R.string.ulsan));
            instance.regionIdName.put(JEJU_ID, r.getString(R.string.jeju));
            instance.regionIdName.put(WONJU_ID, r.getString(R.string.wonju));
            instance.regionIdName.put(BUSAN_ID, r.getString(R.string.busan));
            instance.regionIdName.put(GYEONGGI_ID, r.getString(R.string.gyeonggi));
            instance.regionIdName.put(31010, r.getString(R.string.suwon));
            instance.regionIdName.put(31020, r.getString(R.string.seongnam));
            instance.regionIdName.put(31030, r.getString(R.string.uijeongbu));
            instance.regionIdName.put(31040, r.getString(R.string.anyaong));
            instance.regionIdName.put(31050, r.getString(R.string.bucheon));
            instance.regionIdName.put(31060, r.getString(R.string.gwangmyeong));
            instance.regionIdName.put(31070, r.getString(R.string.pyeongtaek));
            instance.regionIdName.put(31080, r.getString(R.string.dongducheon));
            instance.regionIdName.put(31090, r.getString(R.string.ansan));
            instance.regionIdName.put(31100, r.getString(R.string.goyang));
            instance.regionIdName.put(31110, r.getString(R.string.gwacheon));
            instance.regionIdName.put(31120, r.getString(R.string.guri));
            instance.regionIdName.put(31130, r.getString(R.string.namyangju));
            instance.regionIdName.put(31140, r.getString(R.string.osan));
            instance.regionIdName.put(31150, r.getString(R.string.siheung));
            instance.regionIdName.put(31160, r.getString(R.string.gunpo));
            instance.regionIdName.put(31170, r.getString(R.string.uiwang));
            instance.regionIdName.put(31180, r.getString(R.string.hanam));
            instance.regionIdName.put(31190, r.getString(R.string.yongin));
            instance.regionIdName.put(31200, r.getString(R.string.paju));
            instance.regionIdName.put(31210, r.getString(R.string.icheon));
            instance.regionIdName.put(31220, r.getString(R.string.anseong));
            instance.regionIdName.put(31230, r.getString(R.string.gimpo));
            instance.regionIdName.put(31240, r.getString(R.string.hwaseong));
            instance.regionIdName.put(31250, r.getString(R.string.gwangju_gyeonggi));
            instance.regionIdName.put(31260, r.getString(R.string.yangju));
            instance.regionIdName.put(31270, r.getString(R.string.pocheon));
            instance.regionIdName.put(31320, r.getString(R.string.yeoju));
            instance.regionIdName.put(31350, r.getString(R.string.yeoncheon));
            instance.regionIdName.put(31370, r.getString(R.string.gapyeong));
            instance.regionIdName.put(31380, r.getString(R.string.yangpyeong));
        }
        return instance;
    }

    public static RegionId getInstance() {
        if (instance == null) { instance = new RegionId(); }
        return instance;
    }
}
