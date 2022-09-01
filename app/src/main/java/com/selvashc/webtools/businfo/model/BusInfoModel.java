package com.selvashc.webtools.businfo.model;

import android.content.res.Resources;

import com.selvashc.webtools.businfo.asynctask.BusInfoSearchAsyncTask;
import com.selvashc.webtools.businfo.asynctask.BusRouteSearchAsyncTask;
import com.selvashc.webtools.businfo.viewmodel.BusInfoViewModel;

/**
 *  버스 번호, 경로에 대한 정보를 검색한 후 ViewModel 에 값을 셋팅(반환)
 *   - ViewModel 에서 요청하는 검색을 AsyncTask 를 사용하여 진행
 *   - 검색이 완료되면 listener 를 통해 ViewModel 에 값을 전달
 */
public class BusInfoModel {

    private Resources r;

    private BusInfoViewModel.ListenerBusInfo listener;

    public BusInfoModel(Resources resources, BusInfoViewModel.ListenerBusInfo listenerBusInfo) {
        r = resources;
        listener= listenerBusInfo;
    }

    // 버스 번호 정보 검색
    public void searchBusInfo(int regionId, String word) {
        BusInfoSearchAsyncTask busInfoSearchAsyncTask = new BusInfoSearchAsyncTask(r ,word, regionId, listener);
        busInfoSearchAsyncTask.execute();
    }

    // 버스 경로 정보 검색
    public void searchBusRoute(int regionId, String busRouteId) {
        BusRouteSearchAsyncTask busRouteSearchAsyncTask = new BusRouteSearchAsyncTask(r, busRouteId, regionId, listener);
        busRouteSearchAsyncTask.execute();
    }
}
