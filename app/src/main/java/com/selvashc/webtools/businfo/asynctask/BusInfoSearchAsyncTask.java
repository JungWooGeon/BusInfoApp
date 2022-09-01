package com.selvashc.webtools.businfo.asynctask;

import android.content.res.Resources;

import com.selvashc.webtools.businfo.R;
import com.selvashc.webtools.businfo.data.BusInfo;
import com.selvashc.webtools.businfo.data.RegionId;
import com.selvashc.webtools.businfo.viewmodel.BusInfoViewModel;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * 버스 노선 번호 목록을 API 를 통해 검색하는 AsyncTask
 * 검색을 완료한 후 생성자에서 받아온 Listener 로 결과를 전달해준다.
 */
public class BusInfoSearchAsyncTask extends BaseApiAsyncTask {

    // 버스 정보 결과를 저장할 변수
    private final ArrayList<BusInfo> busInfo = new ArrayList<>();

    // 검색할 단어
    private String word;

    // API 정보를 가져올 때, 정보를 저장할 변수
    private String regionName = "";
    private String routeId = "";
    private String routeName = "";

    // callback listener
    private BusInfoViewModel.ListenerBusInfo listener;

    public BusInfoSearchAsyncTask(Resources resources, String w, int regionId, BusInfoViewModel.ListenerBusInfo listenerBusInfo) {
        super(resources, regionId);
        word = w;
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
                apiSearch(getR().getString(R.string.API_URL_BusSearch), getR().getString(R.string.and) + URLEncoder.encode(getR().getString(R.string.pageNo), getR().getString(R.string.utf8)) + getR().getString(R.string.equal) + URLEncoder.encode(getR().getString(R.string.number_1), getR().getString(R.string.utf8))
                        + getR().getString(R.string.and) + URLEncoder.encode(getR().getString(R.string.numOfRows), getR().getString(R.string.utf8)) + getR().getString(R.string.equal) + URLEncoder.encode(getR().getString(R.string.number_1000), getR().getString(R.string.utf8))
                        + getR().getString(R.string.and) + URLEncoder.encode(getR().getString(R.string._type), getR().getString(R.string.utf8)) + getR().getString(R.string.equal) + URLEncoder.encode(getR().getString(R.string.xml), getR().getString(R.string.utf8))
                        + getR().getString(R.string.and) + URLEncoder.encode(getR().getString(R.string.cityCode), getR().getString(R.string.utf8)) + getR().getString(R.string.equal) + URLEncoder.encode(Integer.toString(getRegionId()), getR().getString(R.string.utf8))
                        + getR().getString(R.string.and) + URLEncoder.encode(getR().getString(R.string.routeNo), getR().getString(R.string.utf8)) + getR().getString(R.string.equal) + URLEncoder.encode(word, getR().getString(R.string.utf8)), BUS_SEARCH);
            } else if (getRegionId() == RegionId.getInstance().getBusanId()) {
                apiSearch(getR().getString(R.string.API_URL_BUSAN_BusSearch), getR().getString(R.string.and) + URLEncoder.encode(getR().getString(R.string.lineno), getR().getString(R.string.utf8)) + getR().getString(R.string.equal) + URLEncoder.encode(word, getR().getString(R.string.utf8)), BUS_SEARCH);
            } else {
                apiSearch(getR().getString(R.string.API_URL_GYEONGGI_BusInfo), getR().getString(R.string.and) + URLEncoder.encode(getR().getString(R.string.keyword), getR().getString(R.string.utf8)) + getR().getString(R.string.equal) + URLEncoder.encode(word, getR().getString(R.string.utf8)), BUS_SEARCH);
            }
        } catch (UnsupportedEncodingException e) {
            e.getCause();
        }
        return null;
    }

    @Override
    public void compareAndStore(String tagName, String text) {
        // 경기, 부산 지역을 제외하고 API 검색 시 tag 에 따라 text 저장
        if (tagName.equals(getR().getString(R.string.routeid))) {
            routeId = text;
        } else if (tagName.equals(getR().getString(R.string.routeno))) {
            routeName = text;
        } else if (tagName.equals(getR().getString(R.string.routetp))) {
            busInfo.add(new BusInfo(routeId, routeName, text));
        }
    }

    @Override
    public void compareAndStoreGyeonggi(String tagName, String text) {
        // 경기 지역 API 검색 시 tag 에 따라 text 저장
        if (tagName.equals(getR().getString(R.string.regionName))) {
            regionName = text;
        } else if (tagName.equals(getR().getString(R.string.routeId))) {
            routeId = text;
        } else if (tagName.equals(getR().getString(R.string.routeName))) {
            routeName = text;
        } else if (tagName.equals(getR().getString(R.string.routeTypeName))) {
            busInfo.add(new BusInfo(routeId, routeName, text, regionName));
        }
    }

    @Override
    public void compareAndStoreBusan(String tagName, String text) {
        // 부산 지역 API 검색 시 tag 에 따라 text 저장
        // 부산 지역은 API 검색 시 END_TAG 마지막에 '\n' 이 추가로 있어 예외처리
        if (text.equals(getR().getString(R.string.next_lign))) return;
        if (tagName.equals(getR().getString(R.string.lineid))) {
            routeId = text;
        } else if (tagName.equals(getR().getString(R.string.buslinenum))) {
            routeName = text;
        } else if (tagName.equals(getR().getString(R.string.bustype))) {
            busInfo.add(new BusInfo(routeId, routeName, text));
        }
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        // Listener 를 통해 결과 전송
        if (isError()) {
            // 실패
            listener.failedSearchBusName();
        } else {
            // 성공
            if (getRegionId() == RegionId.getInstance().getDaejeonId() || getRegionId() == RegionId.getInstance().getIncheonId()
                    || getRegionId() == RegionId.getInstance().getDaeguId() || getRegionId() == RegionId.getInstance().getGwangjuId()
                    || getRegionId() == RegionId.getInstance().getSejongId() || getRegionId() == RegionId.getInstance().getUlsanId()
                    || getRegionId() == RegionId.getInstance().getJejuId() || getRegionId() == RegionId.getInstance().getWonjuId()
                    || getRegionId() == RegionId.getInstance().getBusanId()) {
                for (int i = 0; i < busInfo.size(); i++) {
                    busInfo.get(i).setBusNum(busInfo.get(i).getBusNum() + getR().getString(R.string.space) + busInfo.get(i).getBusType());
                }
            } else {
                for (int i = 0; i < busInfo.size(); i++) {
                    busInfo.get(i).setBusNum(busInfo.get(i).getBusNum() + getR().getString(R.string.space) + busInfo.get(i).getRegion() + getR().getString(R.string.space) + busInfo.get(i).getBusType());
                }
            }
            listener.completedSearchBusName(busInfo);
        }
    }
}