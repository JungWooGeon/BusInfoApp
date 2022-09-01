package com.selvashc.webtools.businfo.model;

import android.content.res.Resources;

import com.selvashc.webtools.businfo.asynctask.SubwayArriveSearchAsyncTask;
import com.selvashc.webtools.businfo.asynctask.SubwayStopSearchAsyncTask;
import com.selvashc.webtools.businfo.viewmodel.SubwayStopInfoViewModel;

import java.util.Timer;
import java.util.TimerTask;

/**
 *  지하철 정류장, 도착 정보를 검색한 후 ViewModel 에 값을 셋팅(반환)
 *   - ViewModel 에서 요청하는 검색을 AsyncTask 를 사용하여 진행
 *   - 검색이 완료되면 listener 를 통해 ViewModel 에 값을 전달
 */
public class SubwayStopInfoModel {

    private Resources r;

    // Timer - 30초 마다 반복하여 도착 정보를 갱신
    private Timer timer;

    private SubwayStopInfoViewModel.ListenerSubwayStopInfo listener;

    public SubwayStopInfoModel(Resources resources, SubwayStopInfoViewModel.ListenerSubwayStopInfo listenerSubwayStopInfo) {
        r = resources;
        listener = listenerSubwayStopInfo;
    }

    public void searchSubwayStop(String subwayStop) {
        SubwayStopSearchAsyncTask subwayStopSearchAsyncTask = new SubwayStopSearchAsyncTask(r, subwayStop, 0, listener);
        subwayStopSearchAsyncTask.execute();
    }

    public void searchSubwayArrive(String subwayStop, String subwayType) {
        final int TIMER_DELAYED = 0;
        final int TIMER_PERIOD = 30000;

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                SubwayArriveSearchAsyncTask subwayArriveSearchAsyncTask = new SubwayArriveSearchAsyncTask(r, subwayStop, subwayType, 0, listener);
                subwayArriveSearchAsyncTask.execute();
            }
        }, TIMER_DELAYED, TIMER_PERIOD);
    }

    public void stopTimer() {
        if (timer != null)
            timer.cancel();
    }
}
