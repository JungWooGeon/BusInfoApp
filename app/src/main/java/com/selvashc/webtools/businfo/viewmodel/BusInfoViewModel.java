package com.selvashc.webtools.businfo.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.res.Resources;

import com.selvashc.webtools.businfo.R;
import com.selvashc.webtools.businfo.data.BusInfo;
import com.selvashc.webtools.businfo.data.RegionId;
import com.selvashc.webtools.businfo.model.BusInfoModel;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

/**
 * View 의 이벤트에 따라 BusInfoModel 이 데이터를 반환/저장하도록 통신하는 ViewModel
 *  - 버스 번호 정보, 버스 경로 정보 검색 (model 에 요청)
 *  - 즐겨 찾기 추가 - 마지막에 검색한 목록을 즐겨 찾기에 추가
 */
public class BusInfoViewModel {

    // Model
    private BusInfoModel model;

    // LiveData
    private MutableLiveData<List<BusInfo>> busInfoList = new MutableLiveData<>();
    private MutableLiveData<String> busRouteResultList = new MutableLiveData<>();

    // 즐겨 찾기 추가를 위해 데이터를 저장할 변수
    private int nowPosition = -1;
    private int regionId;

    public BusInfoViewModel(Resources r) {
        model = new BusInfoModel(r, new ListenerBusInfo());
    }

    public MutableLiveData<List<BusInfo>> getBusInfoList() { return busInfoList; }
    public MutableLiveData<String> getBusRouteResultList() { return busRouteResultList; }

    public void setNowPosition(int position) { nowPosition = position; }

    // 버스 번호 정보 검색
    public void searchBusInfo(String regionName, String word) {
        regionId = RegionId.getInstance().getRegionId(regionName);
        model.searchBusInfo(regionId, word);
    }

    // 버스 경로 검색
    public void searchBusRoute(String regionName, String busRouteId) { model.searchBusRoute(RegionId.getInstance().getRegionId(regionName), busRouteId); }

    // 즐겨 찾기 추가
    public void addBookMark(Context context) {
        String addBookMarkString;

        if (regionId == RegionId.getInstance().getDaejeonId() || regionId == RegionId.getInstance().getIncheonId()
                || regionId == RegionId.getInstance().getSejongId() || regionId == RegionId.getInstance().getGwangjuId()
                || regionId == RegionId.getInstance().getDaeguId() || regionId == RegionId.getInstance().getUlsanId()
                || regionId == RegionId.getInstance().getJejuId() || regionId == RegionId.getInstance().getWonjuId()) {
            addBookMarkString = context.getString(R.string.bus_node_info_no_space) + context.getString(R.string.slash) + RegionId.getInstance().getRegionIdName().get(regionId) + context.getString(R.string.slash) + busInfoList.getValue().get(nowPosition).getBusNum()
                    + context.getString(R.string.greater_than_sign) + busInfoList.getValue().get(nowPosition).getBusId() + context.getString(R.string.next_lign);
        } else if (regionId == RegionId.getInstance().getBusanId()) {
            addBookMarkString = context.getString(R.string.bus_node_info_no_space) + context.getString(R.string.slash) + context.getString(R.string.busan) + context.getString(R.string.slash) + busInfoList.getValue().get(nowPosition).getBusNum() + context.getString(R.string.greater_than_sign) + busInfoList.getValue().get(nowPosition).getBusId() + context.getString(R.string.next_lign);
        } else {
            addBookMarkString = context.getString(R.string.bus_node_info_no_space) + context.getString(R.string.slash) + context.getString(R.string.gyeonggi) + context.getString(R.string.slash) + busInfoList.getValue().get(nowPosition).getBusNum() + context.getString(R.string.greater_than_sign) + busInfoList.getValue().get(nowPosition).getBusId() + context.getString(R.string.next_lign);
        }

        try (OutputStreamWriter oStreamWriter = new OutputStreamWriter(context.openFileOutput(context.getString(R.string.file_name_bookmark), Context.MODE_APPEND))) {
            oStreamWriter.write(addBookMarkString);
        } catch (IOException e) {
            e.getCause();
        }
    }

    // AsyncTask 로부터 CallBack 을 받아 사용할 Listener
    public class ListenerBusInfo {
        public void completedSearchBusName(List<BusInfo> busList) { busInfoList.setValue(busList); }
        public void failedSearchBusName() { busInfoList.setValue(null); }
        public void completedSearchBusRoute(String resultString) { busRouteResultList.setValue(resultString); }
        public void failedSearchBusRoute() { busRouteResultList.setValue(null); }
    }
}
