package com.selvashc.webtools.businfo.asynctask;

import android.content.res.Resources;

import com.selvashc.webtools.businfo.R;
import com.selvashc.webtools.businfo.data.RegionId;
import com.selvashc.webtools.businfo.viewmodel.BusInfoViewModel;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * 버스 경로 정보를 API를 통해 검색할 AsyncTask
 * 검색을 완료한 후 생성자에서 받아온 Listener 로 결과를 전달해준다.
 */
public class BusRouteSearchAsyncTask extends BaseApiAsyncTask {

    // 버스 노선 결과를 저장할 변수
    private StringBuilder busNodeList = new StringBuilder();

    // 검색할 버스 ID
    private String busRouteId;

    // callback listener
    private BusInfoViewModel.ListenerBusInfo listener;

    public BusRouteSearchAsyncTask(Resources resources, String bId, int regionId, BusInfoViewModel.ListenerBusInfo listenerBusInfo) {
        super(resources, regionId);
        busRouteId = bId;
        listener = listenerBusInfo;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        // API 검색 (지역에 따라 다른 API 사용)
        try {
            if (getRegionId() == RegionId.getInstance().getDaejeonId() || getRegionId() == RegionId.getInstance().getIncheonId()
                    || getRegionId() == RegionId.getInstance().getDaeguId() || getRegionId() == RegionId.getInstance().getGwangjuId()
                    || getRegionId() == RegionId.getInstance().getSejongId() || getRegionId() == RegionId.getInstance().getUlsanId()
                    || getRegionId() == RegionId.getInstance().getJejuId() || getRegionId() == RegionId.getInstance().getWonjuId()) {
                apiSearch(getR().getString(R.string.API_URL_BusRouteSearch), getR().getString(R.string.and) + URLEncoder.encode(getR().getString(R.string.pageNo), getR().getString(R.string.utf8)) + getR().getString(R.string.equal) + URLEncoder.encode(getR().getString(R.string.number_1), getR().getString(R.string.utf8))
                        + getR().getString(R.string.and) + URLEncoder.encode(getR().getString(R.string.numOfRows), getR().getString(R.string.utf8)) + getR().getString(R.string.equal) + URLEncoder.encode(getR().getString(R.string.number_1000), getR().getString(R.string.utf8))
                        + getR().getString(R.string.and) + URLEncoder.encode(getR().getString(R.string._type), getR().getString(R.string.utf8)) + getR().getString(R.string.equal) + URLEncoder.encode(getR().getString(R.string.xml), getR().getString(R.string.utf8))
                        + getR().getString(R.string.and) + URLEncoder.encode(getR().getString(R.string.cityCode), getR().getString(R.string.utf8)) + getR().getString(R.string.equal) + URLEncoder.encode(Integer.toString(getRegionId()), getR().getString(R.string.utf8))
                        + getR().getString(R.string.and) + URLEncoder.encode(getR().getString(R.string.routeId), getR().getString(R.string.utf8)) + getR().getString(R.string.equal) + URLEncoder.encode(busRouteId, getR().getString(R.string.utf8)), BUS_SEARCH);
            } else if(getRegionId() == RegionId.getInstance().getBusanId()) {
                apiSearch(getR().getString(R.string.API_URL_BUSAN_BusRouteSearch), getR().getString(R.string.and) + URLEncoder.encode(getR().getString(R.string.lineid), getR().getString(R.string.utf8)) + getR().getString(R.string.equal) + URLEncoder.encode(busRouteId, getR().getString(R.string.utf8)), BUS_SEARCH);
            } else {
                apiSearch(getR().getString(R.string.API_URL_GYEONGGI_BusNodeSearch), getR().getString(R.string.and) + URLEncoder.encode(getR().getString(R.string.routeId), getR().getString(R.string.utf8)) + getR().getString(R.string.equal) + URLEncoder.encode(busRouteId, getR().getString(R.string.utf8)), BUS_SEARCH);
            }
        } catch (UnsupportedEncodingException e) {
            e.getCause();
        }
        return null;
    }

    @Override
    public void compareAndStore(String tagName, String text) {
        // 경기, 부산 지역을 제외하고 API 검색 시 tag 에 따라 text 저장
        if (tagName.equals(getR().getString(R.string.nodenm))) {
            if (busNodeList.toString().equals("")) {
                busNodeList.append(text);
            } else {
                busNodeList.append(getR().getString(R.string.space));
                busNodeList.append(getR().getString(R.string.hyphen));
                busNodeList.append(getR().getString(R.string.space));
                busNodeList.append(text);
            }
        }
    }

    @Override
    public void compareAndStoreGyeonggi(String tagName, String text) {
        // 경기 지역 API 검색 시 tag 에 따라 text 저장
        if (tagName.equals(getR().getString(R.string.stationName))) {
            if (busNodeList.toString().equals("")) {
                busNodeList.append(text);
            } else {
                busNodeList.append(getR().getString(R.string.space));
                busNodeList.append(getR().getString(R.string.hyphen));
                busNodeList.append(getR().getString(R.string.space));
                busNodeList.append(text);
            }
        }
    }

    @Override
    public void compareAndStoreBusan(String tagName, String text) {
        // 부산 지역 API 검색 시 tag 에 따라 text 저장
        // 부산 지역은 API 검색 시 END_TAG 마지막에 '\n' 이 추가로 있어 예외처리
        if (text.equals(getR().getString(R.string.next_lign))) return;
        if (tagName.equals(getR().getString(R.string.bstopnm))) {
            if (busNodeList.toString().equals("")) {
                busNodeList.append(text);
            } else {
                busNodeList.append(getR().getString(R.string.space));
                busNodeList.append(getR().getString(R.string.hyphen));
                busNodeList.append(getR().getString(R.string.space));
                busNodeList.append(text);
            }
        }
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        // Listener 를 통해 결과 전송
        if (isError()) {
            // 실패
            listener.failedSearchBusRoute();
        } else {
            // 성공
            listener.completedSearchBusRoute(busNodeList.toString());
        }
    }
}