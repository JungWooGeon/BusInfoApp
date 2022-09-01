package com.selvashc.webtools.businfo.asynctask;

import android.content.res.Resources;

import com.selvashc.webtools.businfo.R;
import com.selvashc.webtools.businfo.data.SubwayArriveTimeInfo;
import com.selvashc.webtools.businfo.data.SubwayId;
import com.selvashc.webtools.businfo.viewmodel.SubwayStopInfoViewModel;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * 지하철 도착 정보를 API를 통해 검색할 AsyncTask
 * 검색을 완료한 후 생성자에서 받아온 Listener 로 결과를 전달해준다.
 */
public class SubwayArriveSearchAsyncTask extends BaseApiAsyncTask {

    // 정류장 정보 결과를 저장할 변수
    private SubwayArriveTimeInfo subwayArriveTimeInfo;
    private List<SubwayArriveTimeInfo> subwayArriveTimeInfoList = new ArrayList<>();

    // 검색할 버스 정류장
    private String searchSubwayStop;
    private int searchSubwayType;

    // callback listener
    private SubwayStopInfoViewModel.ListenerSubwayStopInfo listener;

    public SubwayArriveSearchAsyncTask(Resources resources, String subwayStop, String subwayType, int regionId, SubwayStopInfoViewModel.ListenerSubwayStopInfo listenerSubwayStopInfo) {
        super(resources, regionId);
        searchSubwayStop = subwayStop.split(resources.getString(R.string.station))[0];
        searchSubwayType = SubwayId.getInstance().getSubwayNameId().get(subwayType);
        listener = listenerSubwayStopInfo;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        // API 검색 - 지역으로 나누지 않음 (수도권 외 지역 추가 시 구별 필요)
        try {
            apiSearch(getR().getString(R.string.API_URL_CAPITAL_SubwayArriveSearch), getR().getString(R.string.slash) + URLEncoder.encode(getR().getString(R.string.API_CAPITAL_SUBWAY_KEY), getR().getString(R.string.utf8))
                    + getR().getString(R.string.slash) + URLEncoder.encode(getR().getString(R.string.xml), getR().getString(R.string.utf8)) + getR().getString(R.string.slash) + URLEncoder.encode(getR().getString(R.string.realtimeStationArrival), getR().getString(R.string.utf8))
                    + getR().getString(R.string.slash) + URLEncoder.encode(getR().getString(R.string.number_0), getR().getString(R.string.utf8)) + getR().getString(R.string.slash) + URLEncoder.encode(getR().getString(R.string.number_100), getR().getString(R.string.utf8))
                    + getR().getString(R.string.slash) + URLEncoder.encode(searchSubwayStop, getR().getString(R.string.utf8)), SUBWAY_SEARCH);
        } catch (UnsupportedEncodingException e) {
            e.getCause();
        }
        return null;
    }

    @Override
    public void compareAndStore(String tagName, String text) {
        // 지역을 나누지 않음
        compareAndStoreGyeonggi(tagName, text);
    }

    @Override
    public void compareAndStoreGyeonggi(String tagName, String text) {
        // 수도권 지역 API 검색 시 tag 에 따라 text 저장
        if (tagName.equals(getR().getString(R.string.subwayId))) {
            subwayArriveTimeInfo = new SubwayArriveTimeInfo();
            subwayArriveTimeInfo.setSubwayId(Integer.parseInt(text));
        } else if (tagName.equals(getR().getString(R.string.updnLine))) {
            subwayArriveTimeInfo.setUpdownLine(text);
        } else if (tagName.equals(getR().getString(R.string.trainLineNm))) {
            subwayArriveTimeInfo.setTrainLineNumber(text);
        } else if (tagName.equals(getR().getString(R.string.barvlDt))) {
            subwayArriveTimeInfo.setArrivetTime(text);
        } else if (tagName.equals(getR().getString(R.string.arvlMsg2))) {
            subwayArriveTimeInfo.setArriveMsg(text);

            if (subwayArriveTimeInfo.getSubwayId() == searchSubwayType) {
                subwayArriveTimeInfoList.add(subwayArriveTimeInfo);
            }
        }
    }

    @Override
    public void compareAndStoreBusan(String tagName, String text) {
        // 지역을 나누지 않음
        compareAndStoreGyeonggi(tagName, text);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        // Listener 를 통해 결과 전송
        if (isError()) {
            // 실패
            listener.failedSearchSubwayArrive();
        } else {
            // 성공
            listener.completedSearchSubwayArrive(subwayArriveTimeInfoList);
        }
    }
}
