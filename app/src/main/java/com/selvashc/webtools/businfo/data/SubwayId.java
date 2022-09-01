package com.selvashc.webtools.businfo.data;

import android.content.res.Resources;

import com.selvashc.webtools.businfo.R;

import java.util.HashMap;
import java.util.Map;

public class SubwayId {

    private static SubwayId instance;

    private Map<String, Integer> subwayNameId = new HashMap<>();

    public Map<String, Integer> getSubwayNameId() { return subwayNameId; }

    public static SubwayId getInstance(Resources r) {
        if (instance == null) {
            instance = new SubwayId();
            // 인천1호선, 인천2호선, 신림선, 의정부, 에버라인, 경강선, 서해선, 김포골드 제외
            instance.subwayNameId.put(r.getString(R.string.Line1), 1001);
            instance.subwayNameId.put(r.getString(R.string.Line2), 1002);
            instance.subwayNameId.put(r.getString(R.string.Line3), 1003);
            instance.subwayNameId.put(r.getString(R.string.Line4), 1004);
            instance.subwayNameId.put(r.getString(R.string.Line5), 1005);
            instance.subwayNameId.put(r.getString(R.string.Line6), 1006);
            instance.subwayNameId.put(r.getString(R.string.Line7), 1007);
            instance.subwayNameId.put(r.getString(R.string.Line8), 1008);
            instance.subwayNameId.put(r.getString(R.string.Line9), 1009);
            instance.subwayNameId.put(r.getString(R.string.LineShinbundang), 1077);
            instance.subwayNameId.put(r.getString(R.string.LineGyeonguiJungang), 1063);
            instance.subwayNameId.put(r.getString(R.string.LineGyeongchun), 1067);
            instance.subwayNameId.put(r.getString(R.string.LineSuinbundang), 1075);
            instance.subwayNameId.put(r.getString(R.string.LineAirport), 1065);
            instance.subwayNameId.put(r.getString(R.string.LineWooiShinseol), 1092);
        }
        return instance;
    }

    public static SubwayId getInstance() {
        if (instance == null) { instance = new SubwayId(); }
        return instance;
    }
}
