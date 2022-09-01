package com.selvashc.webtools.businfo.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.res.Resources;

import com.selvashc.webtools.businfo.R;
import com.selvashc.webtools.businfo.data.SubwayArriveTimeInfo;
import com.selvashc.webtools.businfo.model.SubwayStopInfoModel;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * View 의 이벤트에 따라 SubwayStopInfoModel 이 데이터를 반환/저장하도록 통신하는 ViewModel
 *  - 지하철 정류장 정보, 지하철 도착 정보 검색 (model 에 요청)
 *  - 즐겨 찾기 추가 - 마지막에 검색한 목록을 즐겨 찾기에 추가
 */
public class SubwayStopInfoViewModel {

    // Model
    private SubwayStopInfoModel model;

    // LiveData
    private MutableLiveData<List<String>> subwayStopInfoList = new MutableLiveData<>();
    private MutableLiveData<List<SubwayArriveTimeInfo>> subwayArriveInfoList = new MutableLiveData<>();

    // 즐겨 찾기 추가를 위해 데이터를 저장할 변수
    private int nowPosition = -1;
    private String nowStopName = "";

    private Resources r;

    public SubwayStopInfoViewModel(Resources resources) {
        r = resources;
        model = new SubwayStopInfoModel(resources, new SubwayStopInfoViewModel.ListenerSubwayStopInfo());
    }

    public MutableLiveData<List<String>> getSubwayStopInfoList() { return subwayStopInfoList; }
    public MutableLiveData<List<SubwayArriveTimeInfo>> getSubwayArriveInfoList() { return subwayArriveInfoList; }

    // view 에서 timer 를 종료하라는 요청에 따라 model 에 stopTimer 를 요청
    public void stopTimer() { model.stopTimer(); }

    // 지하철 정류장 정보 검색
    public void searchSubwayStop(String searchBusStop) {
        nowStopName = searchBusStop;
        model.searchSubwayStop(searchBusStop);
    }

    // 지하철 도착 정보 검색
    public void searchSubwayArrive(int position) {
        nowPosition = position;
        model.searchSubwayArrive(nowStopName, subwayStopInfoList.getValue().get(position).split(r.getString(R.string.space))[1]);
    }
    public void searchSubwayArrive(String stopName, String stopType) {
        model.searchSubwayArrive(stopName, stopType);
    }

    // 즐겨 찾기 추가
    public void addBookMark(Context context, String regionName) {
        try (OutputStreamWriter oStreamWriter = new OutputStreamWriter(context.openFileOutput(context.getString(R.string.file_name_bookmark), Context.MODE_APPEND))) {
            oStreamWriter.write(context.getString(R.string.subway_arrive_info_no_space) + context.getString(R.string.slash) + regionName + context.getString(R.string.slash) + subwayStopInfoList.getValue().get(nowPosition) + context.getString(R.string.next_lign));
        } catch (IOException e) {
            e.getCause();
        }
    }

    // AsyncTask로부터 CallBack을 받아 사용할 Listener
    public class ListenerSubwayStopInfo {
        public void completedSearchSubwayStop(List<String> infoList) {
            List<String> result = new ArrayList<>();
            for (int i = 0; i < infoList.size(); i++) {
                result.add(nowStopName + r.getString(R.string.space) + infoList.get(i));
            }
            subwayStopInfoList.setValue(result);
        }
        public void failedSearchSubwayStop() { subwayStopInfoList.setValue(null); }
        public void completedSearchSubwayArrive(List<SubwayArriveTimeInfo> timeInfo) { subwayArriveInfoList.setValue(timeInfo); }
        public void failedSearchSubwayArrive() {
            subwayArriveInfoList.setValue(null);
            stopTimer();
        }
    }
}
