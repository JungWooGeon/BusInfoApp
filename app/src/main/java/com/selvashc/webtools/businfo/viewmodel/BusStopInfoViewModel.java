package com.selvashc.webtools.businfo.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.res.Resources;

import com.selvashc.webtools.businfo.R;
import com.selvashc.webtools.businfo.data.BusArriveTimeInfo;
import com.selvashc.webtools.businfo.data.BusStopInfo;
import com.selvashc.webtools.businfo.data.RegionId;
import com.selvashc.webtools.businfo.model.BusStopInfoModel;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

/**
 * View 의 이벤트에 따라 BusStopInfoModel 이 데이터를 반환/저장하도록 통신하는 ViewModel
 *  - 버스 정류장 정보, 버스 도착 정보 검색 (model 에 요청)
 *  - 즐겨 찾기 추가 - 마지막에 검색한 목록을 즐겨 찾기에 추가
 */
public class BusStopInfoViewModel {

    // Model
    private BusStopInfoModel model;

    // LiveData
    private final MutableLiveData<List<BusStopInfo>> busStopInfoList = new MutableLiveData<>();
    private final MutableLiveData<List<BusArriveTimeInfo>> busArriveTimeInfoList = new MutableLiveData<>();

    // 즐겨 찾기 추가를 위해 데이터를 저장할 변수
    private int nowPosition = -1;

    public BusStopInfoViewModel(Resources resources) {
        model = new BusStopInfoModel(resources, new ListenerBusStopInfo());
    }

    public MutableLiveData<List<BusStopInfo>> getBusStopInfoList() { return busStopInfoList; }
    public MutableLiveData<List<BusArriveTimeInfo>> getBusArriveTimeInfoList() { return busArriveTimeInfoList; }

    public void setNowPosition(int position) { nowPosition = position; }

    // view 에서 timer 를 종료하라는 요청에 따라 model 에 stopTimer 를 요청
    public void stopTimer() { model.stopTimer(); }

    // 버스 정류장 정보 검색
    public void searchBusStop(String regionName, String searchBusStop) {
        model.searchBusStop(RegionId.getInstance().getRegionId(regionName), searchBusStop);
    }

    // 버스 도착 정보 검색
    public void searchBusArrive(String regionName, String busStopId) {
        model.searchBusArrive(RegionId.getInstance().getRegionId(regionName), busStopId);
    }

    // 즐겨 찾기 추가
    public void addBookMark(Context context, String regionName) {
        try (OutputStreamWriter oStreamWriter = new OutputStreamWriter(context.openFileOutput(context.getString(R.string.file_name_bookmark), Context.MODE_APPEND))) {
            oStreamWriter.write(context.getString(R.string.bus_arrive_info_no_space) + context.getString(R.string.slash) + regionName + context.getString(R.string.slash) + busStopInfoList.getValue().get(nowPosition).getBusStopName() + context.getString(R.string.greater_than_sign) +
                    busStopInfoList.getValue().get(nowPosition).getBusStopId() + context.getString(R.string.next_lign));
        } catch (IOException e) {
            e.getCause();
        }
    }

    // AsyncTask로부터 CallBack을 받아 사용할 Listener
    public class ListenerBusStopInfo {
        public void completedSearchBusStop(List<BusStopInfo> infoList) { busStopInfoList.setValue(infoList); }
        public void failedSearchBusStop() { busStopInfoList.setValue(null); }
        public void completedSearchBusArrive(List<BusArriveTimeInfo> timeInfo) { busArriveTimeInfoList.setValue(timeInfo); }
        public void failedSearchBusArrive() {
            busArriveTimeInfoList.setValue(null);
            stopTimer();
        }
    }
}
