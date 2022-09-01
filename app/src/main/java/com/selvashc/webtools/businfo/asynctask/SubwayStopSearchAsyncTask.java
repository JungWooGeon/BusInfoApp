package com.selvashc.webtools.businfo.asynctask;

import android.content.res.Resources;

import com.selvashc.webtools.businfo.R;
import com.selvashc.webtools.businfo.viewmodel.SubwayStopInfoViewModel;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * 지하철 정류장 정보를 API를 통해 검색할 AsyncTask
 * 검색을 완료한 후 생성자에서 받아온 Listener 로 결과를 전달해준다.
 */
public class SubwayStopSearchAsyncTask extends BaseApiAsyncTask {

    private List<String> busTypeLIst = new ArrayList<>();

    // 검색할 버스 정류장
    private String searchSubwayStop;

    // callback listener
    private SubwayStopInfoViewModel.ListenerSubwayStopInfo listener;

    public SubwayStopSearchAsyncTask(Resources resources, String subwayStop, int regionId, SubwayStopInfoViewModel.ListenerSubwayStopInfo listenerSubwayStopInfo) {
        super(resources, regionId);
        searchSubwayStop = subwayStop;
        listener = listenerSubwayStopInfo;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        // API 검색 - 지역으로 나누지 않음 (수도권 외 지역 추가 시 구별 필요)
        try {
            apiSearch(getR().getString(R.string.API_URL_CAPITAL_SubwayStopSearch), getR().getString(R.string.slash) + URLEncoder.encode(getR().getString(R.string.API_CAPITAL_SUBWAY_KEY), getR().getString(R.string.utf8))
                    + getR().getString(R.string.slash) + URLEncoder.encode(getR().getString(R.string.xml), getR().getString(R.string.utf8)) + getR().getString(R.string.slash) + URLEncoder.encode(getR().getString(R.string.SearchInfoBySubwayNameService), getR().getString(R.string.utf8))
                    + getR().getString(R.string.slash) + URLEncoder.encode(getR().getString(R.string.number_0), getR().getString(R.string.utf8)) + getR().getString(R.string.slash) + URLEncoder.encode(getR().getString(R.string.number_100), getR().getString(R.string.utf8))
                    + getR().getString(R.string.slash) + URLEncoder.encode(searchSubwayStop, getR().getString(R.string.utf8)), SUBWAY_SEARCH);
        } catch (UnsupportedEncodingException e) {
            e.getCause();
        }
        return null;
    }

    @Override
    public void compareAndStore(String tagName, String text) {
        // 지역 구분 x
        compareAndStoreGyeonggi(tagName, text);
    }

    @Override
    public void compareAndStoreGyeonggi(String tagName, String text) {
        if (text.equals(getR().getString(R.string.next_lign))) return;
        if (tagName.equals(getR().getString(R.string.LINE_NUM))) {
            busTypeLIst.add(text.trim());
        }
    }

    @Override
    public void compareAndStoreBusan(String tagName, String text) {
        // 지역 구분 x
        compareAndStoreGyeonggi(tagName, text);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        // Listener 를 통해 결과 전송
        if (isError()) {
            // 실패
            listener.failedSearchSubwayStop();
        } else {
            // 성공
            listener.completedSearchSubwayStop(busTypeLIst);
        }
    }
}
