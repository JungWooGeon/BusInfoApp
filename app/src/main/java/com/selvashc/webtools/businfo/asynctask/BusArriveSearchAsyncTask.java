package com.selvashc.webtools.businfo.asynctask;

import android.content.res.Resources;

import com.selvashc.webtools.businfo.R;
import com.selvashc.webtools.businfo.data.BusArriveTimeInfo;
import com.selvashc.webtools.businfo.data.RegionId;
import com.selvashc.webtools.businfo.viewmodel.BusStopInfoViewModel;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * 버스 도착 정보를 API를 통해 검색하는 AsyncTask
 * 검색을 완료한 후 생성자에서 받아온 Listener 로 결과를 전달해준다.
 */
public class BusArriveSearchAsyncTask extends BaseApiAsyncTask {

    // 버스 도착 정보 결과를 저장할 변수
    private ArrayList<BusArriveTimeInfo> busArriveResultList = new ArrayList<>();

    // 검색할 버스 정류장 ID
    private String busStopId;

    // API 정보를 가져올 때, 정보를 저장할 변수
    private int preTime = 0;
    private int arriveTime = 0;
    private String name = "";

    // callback listener
    private BusStopInfoViewModel.ListenerBusStopInfo listener;

    public BusArriveSearchAsyncTask(Resources resources, String stopId, int regionId, BusStopInfoViewModel.ListenerBusStopInfo listenerBusArrive) {
        super(resources, regionId);
        busStopId = stopId;
        listener = listenerBusArrive;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        // API 검색 (버스 도착 정보는 부산을 제외하고 모두 동일한 API를 사용함)
        try {
            if (getRegionId() == RegionId.getInstance().getBusanId()) {
                apiSearch(getR().getString(R.string.API_URL_BUSAN_BusArriveSearch), getR().getString(R.string.and) + URLEncoder.encode(getR().getString(R.string.bstopid), getR().getString(R.string.utf8)) + getR().getString(R.string.equal) + URLEncoder.encode(busStopId, getR().getString(R.string.utf8)), BUS_SEARCH);
            } else {
                apiSearch(getR().getString(R.string.API_URL_BusArriveTimerSearch), getR().getString(R.string.and) + URLEncoder.encode(getR().getString(R.string._type), getR().getString(R.string.utf8)) + getR().getString(R.string.equal) + URLEncoder.encode(getR().getString(R.string.xml), getR().getString(R.string.utf8))
                        + getR().getString(R.string.and) + URLEncoder.encode(getR().getString(R.string.cityCode), getR().getString(R.string.utf8)) + getR().getString(R.string.equal) + URLEncoder.encode(Integer.toString(getRegionId()), getR().getString(R.string.utf8))
                        + getR().getString(R.string.and) + URLEncoder.encode(getR().getString(R.string.nodeId), getR().getString(R.string.utf8)) + getR().getString(R.string.equal) + URLEncoder.encode(busStopId, getR().getString(R.string.utf8)), BUS_SEARCH);
            }
        } catch (UnsupportedEncodingException e) {
            e.getCause();
        }
        return null;
    }

    @Override
    public void compareAndStore(String tagName, String text) {
        // 경기 지역과 동일하게 저장 (같은 API 를 사용)
        compareAndStoreGyeonggi(tagName, text);
    }

    @Override
    public void compareAndStoreGyeonggi(String tagName, String text) {
        // 경기 지역 API 검색 시 tag 에 따라 text 저장
        // preTime, preName -> 두 번째로 오는 버스에 대한 정보를 무시하기 위한 예외처리 (버스번호가 같다면 더 먼저 오는 버스로 저장)
        if (tagName.equals(getR().getString(R.string.arrtime))) {
            preTime = arriveTime;
            arriveTime = Integer.parseInt(text) / 60;
            if (arriveTime == 0)
                arriveTime = 1;
        } else if (tagName.equals(getR().getString(R.string.routeno))) {
            String preName = name;
            name = text;
            if (preName.equals(name)) {
                if (preTime > arriveTime)
                    busArriveResultList.get(busArriveResultList.size()-1).setArriveTime(Integer.toString(arriveTime));
            } else {
                busArriveResultList.add(new BusArriveTimeInfo(Integer.toString(arriveTime), text));
            }
        }
    }

    @Override
    public void compareAndStoreBusan(String tagName, String text) {
        // 부산 지역 API 검색 (tag에 따라 text 저장)
        // 부산 지역은 API 검색 시 END_TAG 마지막에 '\n' 이 추가로 있어 예외처리
        if (text.equals(getR().getString(R.string.next_lign))) return;
        if (tagName.equals(getR().getString(R.string.lineno)))
            name = text;
        else if (tagName.equals(getR().getString(R.string.min1)))
            busArriveResultList.add(new BusArriveTimeInfo(text , name));
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        // Listener 를 통해 결과 전송
        if (isError()) {
            // 실패
            listener.failedSearchBusArrive();
        } else {
            // 성공
            for (int i = 0; i < busArriveResultList.size(); i++) {
                busArriveResultList.get(i).setBusName(busArriveResultList.get(i).getBusName() + getR().getString(R.string.space) + getR().getString(R.string.bus_num_is) + getR().getString(R.string.space) +
                        busArriveResultList.get(i).getArriveTime() + getR().getString(R.string.space) + getR().getString(R.string.arrive_after_minute));
            }
            listener.completedSearchBusArrive(busArriveResultList);
        }
    }
}
