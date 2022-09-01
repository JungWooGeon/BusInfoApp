package com.selvashc.webtools.businfo.asynctask;

import android.content.res.Resources;

import com.selvashc.webtools.businfo.R;
import com.selvashc.webtools.businfo.data.BusStopInfo;
import com.selvashc.webtools.businfo.data.RegionId;
import com.selvashc.webtools.businfo.viewmodel.BusStopInfoViewModel;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * 버스 정류장 정보를 API를 통해 검색할 AsyncTask
 * 검색을 완료한 후 생성자에서 받아온 Listener 로 결과를 전달해준다.
 */
public class BusStopSearchAsyncTask extends BaseApiAsyncTask {

    // 정류장 정보 결과를 저장할 변수
    private BusStopInfo busStopInfo = new BusStopInfo();
    private List<BusStopInfo> busStopInfoList = new ArrayList<>();

    // 검색할 버스 정류장
    private String searchBusStop;

    // API 정보를 가져올 때, 정보를 저장할 변수
    private String busStopName;

    // callback listener
    private BusStopInfoViewModel.ListenerBusStopInfo listener;

    public BusStopSearchAsyncTask(Resources resources, String busStop, int regionId, BusStopInfoViewModel.ListenerBusStopInfo listenerBusStop) {
        super(resources, regionId);
        searchBusStop = busStop;
        listener = listenerBusStop;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        // API 검색 (버스 도착 정보는 부산을 제외하고 모두 동일한 API를 사용함)
        if (getRegionId() == RegionId.getInstance().getBusanId()) {
            try {
                apiSearch(getR().getString(R.string.API_URL_BUSAN_BusStopSearch), getR().getString(R.string.and) + URLEncoder.encode(getR().getString(R.string.pageNo), getR().getString(R.string.utf8)) + getR().getString(R.string.equal) + URLEncoder.encode(getR().getString(R.string.number_1), getR().getString(R.string.utf8))
                        + getR().getString(R.string.and) + URLEncoder.encode(getR().getString(R.string.numOfRows), getR().getString(R.string.utf8)) + getR().getString(R.string.equal) + URLEncoder.encode(getR().getString(R.string.number_1000), getR().getString(R.string.utf8))
                        + getR().getString(R.string.and) + URLEncoder.encode(getR().getString(R.string.bstopnm), getR().getString(R.string.utf8)) + getR().getString(R.string.equal) + URLEncoder.encode(searchBusStop, getR().getString(R.string.utf8)), BUS_SEARCH);
            } catch (UnsupportedEncodingException e) {
                e.getCause();
            }
        } else {
            // 검색어 뒤에 "." 을 붙이지 않을 경우 검색어가 포함되어도 "."이 있다면 검색되지 않아
            // 같이 검색되게 하기 위하여 "."을 붙이고도 검색을 진행
            busStopSearch(searchBusStop);
            busStopSearch(searchBusStop + getR().getString(R.string.dot));
        }
        return null;
    }

    private void busStopSearch(String name) {
        // 부산 지역을 제외하고 API를 검색할 때 사용
        try {
            apiSearch(getR().getString(R.string.API_URL_BusStopSearch), getR().getString(R.string.and) + URLEncoder.encode(getR().getString(R.string.pageNo), getR().getString(R.string.utf8)) + getR().getString(R.string.equal) + URLEncoder.encode(getR().getString(R.string.number_1), getR().getString(R.string.utf8))
                    + getR().getString(R.string.and) + URLEncoder.encode(getR().getString(R.string.numOfRows), getR().getString(R.string.utf8)) + getR().getString(R.string.equal) + URLEncoder.encode(getR().getString(R.string.number_1000), getR().getString(R.string.utf8))
                    + getR().getString(R.string.and) + URLEncoder.encode(getR().getString(R.string._type), getR().getString(R.string.utf8)) + getR().getString(R.string.equal) + URLEncoder.encode(getR().getString(R.string.xml), getR().getString(R.string.utf8))
                    + getR().getString(R.string.and) + URLEncoder.encode(getR().getString(R.string.cityCode), getR().getString(R.string.utf8)) + getR().getString(R.string.equal) + URLEncoder.encode(Integer.toString(getRegionId()), getR().getString(R.string.utf8))
                    + getR().getString(R.string.and) + URLEncoder.encode(getR().getString(R.string.nodeNm), getR().getString(R.string.utf8)) + getR().getString(R.string.equal) + URLEncoder.encode(name, getR().getString(R.string.utf8)), BUS_SEARCH);
        } catch (UnsupportedEncodingException e) {
            e.getCause();
        }
    }

    @Override
    public void compareAndStore(String tagName, String text) {
        // 경기 지역과 동일하게 저장 (같은 API 를 사용)
        compareAndStoreGyeonggi(tagName, text);
    }

    @Override
    public void compareAndStoreGyeonggi(String tagName, String text) {
        // 경기 지역 API 검색 시 tag 에 따라 text 저장
        if (tagName.equals(getR().getString(R.string.nodeid))) {
            busStopInfo = new BusStopInfo();
            busStopInfo.setBusStopId(text);
        } else if (tagName.equals(getR().getString(R.string.nodenm))) {
            busStopName = text;
            // 제주는 정류소 번호가 없음
            if (getRegionId() == RegionId.getInstance().getJejuId()) {
                busStopInfo.setBusStopName(busStopName);
                busStopInfoList.add(busStopInfo);
            }
        } else if (tagName.equals(getR().getString(R.string.nodeno))) {
            busStopInfo.setBusStopName(busStopName + getR().getString(R.string.slash) + getR().getString(R.string.stop_numer) + getR().getString(R.string.space) + text);
            busStopInfoList.add(busStopInfo);
        }
    }

    @Override
    public void compareAndStoreBusan(String tagName, String text) {
        // 부산 지역 API 검색 (tag 에 따라 text 저장)
        // 부산 지역은 API 검색 시 END_TAG 마지막에 '\n' 이 추가로 있어 예외처리
        if (text.equals(getR().getString(R.string.next_lign))) return;
        if (tagName.equals(getR().getString(R.string.bstopid))) {
            busStopInfo = new BusStopInfo();
            busStopInfo.setBusStopId(text);
        } else if (tagName.equals(getR().getString(R.string.bstopnm))) {
            busStopName = text;
        } else if (tagName.equals(getR().getString(R.string.arsno))) {
            busStopInfo.setBusStopName(busStopName + getR().getString(R.string.slash) + getR().getString(R.string.stop_numer) + getR().getString(R.string.space) + text);
            busStopInfoList.add(busStopInfo);
        }
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        // Listener 를 통해 결과 전송
        if (isError()) {
            // 실패
            listener.failedSearchBusStop();
        } else {
            // 성공
            listener.completedSearchBusStop(busStopInfoList);
        }
    }
}
