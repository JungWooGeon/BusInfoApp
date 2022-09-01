package com.selvashc.webtools.businfo.model;

import android.content.res.Resources;

import com.selvashc.webtools.businfo.asynctask.BusArriveSearchAsyncTask;
import com.selvashc.webtools.businfo.asynctask.BusStopSearchAsyncTask;
import com.selvashc.webtools.businfo.viewmodel.BusStopInfoViewModel;

import java.util.Timer;
import java.util.TimerTask;

/**
 *  버스 정류장, 도착 정보를 검색한 후 ViewModel 에 값을 셋팅(반환)
 *   - ViewModel 에서 요청하는 검색을 AsyncTask 를 사용하여 진행
 *   - 검색이 완료되면 listener 를 통해 ViewModel 에 값을 전달
 */
public class BusStopInfoModel {

    private Resources r;

    // Timer - 30초 마다 반복하여 도착 정보를 갱신
    private Timer timer;

    private BusStopInfoViewModel.ListenerBusStopInfo listener;

    public BusStopInfoModel (Resources resources, BusStopInfoViewModel.ListenerBusStopInfo listenerBusStopInfo) {
        r = resources;
        listener = listenerBusStopInfo;
    }

    // 버스 정류장 정보 검색
    public void searchBusStop(int regionId, String busStop) {
        BusStopSearchAsyncTask busStopSearchAsyncTask = new BusStopSearchAsyncTask(r, busStop, regionId, listener);
        busStopSearchAsyncTask.execute();
    }

    // 버스 도착 정보 검색
    public void searchBusArrive(int regionId, String stopId) {
        final int TIMER_DELAYED = 0;
        final int TIMER_PERIOD = 30000;

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                BusArriveSearchAsyncTask busArriveSearchAsyncTask = new BusArriveSearchAsyncTask(r, stopId, regionId, listener);
                busArriveSearchAsyncTask.execute();
            }
        }, TIMER_DELAYED, TIMER_PERIOD);
    }

    public void stopTimer() {
        if (timer != null)
            timer.cancel();
    }
}
