package com.selvashc.webtools.businfo.view.activity;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import com.jawon.han.widget.HanApplication;
import com.jawon.han.widget.adapter.HanStringArrayAdapter;
import com.selvashc.webtools.businfo.R;
import com.selvashc.webtools.businfo.data.RegionId;
import com.selvashc.webtools.businfo.databinding.ActivityBusArriveBinding;
import com.selvashc.webtools.businfo.view.dialog.GyeongggiRegionSelectDialog;
import com.selvashc.webtools.businfo.viewmodel.BusStopInfoViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * 버스 도착 정보 화면
 *  - 버스 정류장 이름과 버스 도착 정보를 검색 (ViewModel 에 요청)
 *  - 즐겨 찾기 추가
 */
public class BusStopInfoActivity extends BaseActivity {

    // ViewModel
    private BusStopInfoViewModel busStopInfoViewModel;

    // ListView 에 사용될 List
    private List<String> busStopNameList = new ArrayList<>();
    private List<String> busArriveList = new ArrayList<>();

    // 버스 정류장 ID를 저장할 List
    private List<String> busStopIdList = new ArrayList<>();

    // ListView Adapter
    private HanStringArrayAdapter searchBusStopListViewAdapter;
    private HanStringArrayAdapter searchBusArriveListViewAdapter;

    // 지역 선택 목록에서 선택한 지역 이름
    private String regionName;

    // 버스 도착 검색은 30초마다 갱신되는데, 처음으로 검색할 때를 구별하기 위해 사용
    private boolean timerIsFirst;

    private ActivityBusArriveBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_bus_arrive);
        binding.setActivity(this);

        getHanMenuPopup().inflate(R.menu.popup_menu_bus_info);
        bindViews();
        initViewModel();
    }

    @Override
    protected void onDestroy() {
        busStopInfoViewModel.stopTimer();
        super.onDestroy();
    }

    private void bindViews() {
        // EditText 에서 엔터키를 누르면 바로 검색할 수 있도록 설정
        binding.busStopEdittext.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_UP) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                onClickedSearchBusStop();
                return true;
            }
            return false;
        });

        searchBusStopListViewAdapter = new HanStringArrayAdapter(this, android.R.layout.simple_list_item_1, busStopNameList);
        binding.searchBusStopListview.setAdapter(searchBusStopListViewAdapter);
        binding.searchBusStopListview.setOnItemClickListener((parent, view, position, id) -> onClickedSearchBusArrive());

        searchBusArriveListViewAdapter = new HanStringArrayAdapter(this, android.R.layout.simple_list_item_1, busArriveList);
        binding.searchBusArriveListview.setAdapter(searchBusArriveListViewAdapter);
        binding.searchBusArriveListview.setOnItemClickListener((parent, view, position, id) -> HanApplication.getInstance(this).getHanDevice().displayAndPlayTTS(binding.searchBusArriveListview.getSelectedItem().toString(), true));
    }

    private void initViewModel() {
        // 정류장 검색 결과 update
        busStopInfoViewModel = new BusStopInfoViewModel(getResources());
        busStopInfoViewModel.getBusStopInfoList().observe(this, busStopInfoResultList -> {
            getHanProgressDialog().dismiss();

            if (busStopInfoResultList == null) {
                HanApplication.getInstance(this).getHanDevice().displayAndPlayTTS(getString(R.string.api_error), true);
                return;
            }

            if (busStopInfoResultList.isEmpty()) {
                HanApplication.getInstance(this).getHanDevice().displayAndPlayTTS(getString(R.string.no_result), true);
            } else {
                HanApplication.getInstance(this).getHanDevice().displayAndPlayTTS(getString(R.string.complete_search), true);
                busStopNameList.clear();
                busStopIdList.clear();
                for (int i = 0; i < busStopInfoResultList.size(); i++) {
                    busStopNameList.add(busStopInfoResultList.get(i).getBusStopName());
                    busStopIdList.add(busStopInfoResultList.get(i).getBusStopId());
                }
                searchBusStopListViewAdapter.notifyDataSetChanged();
                binding.busStopResultLayout.setVisibility(View.VISIBLE);
                binding.searchBusStopListview.requestFocus();
                binding.searchBusStopListview.setSelection(0);
            }
        });

        // 버스 도착 정보 결과 update
        busStopInfoViewModel.getBusArriveTimeInfoList().observe(this, busArriveTimeInfoResultList -> {
            if (busArriveTimeInfoResultList == null) {
                HanApplication.getInstance(this).getHanDevice().displayAndPlayTTS(getString(R.string.api_error), true);
                busArriveList.clear();
                binding.busArriveResultLayout.setVisibility(View.GONE);
                getHanProgressDialog().dismiss();
                busStopInfoViewModel.stopTimer();
                return;
            }

            if (busArriveTimeInfoResultList.isEmpty()) {
                if (timerIsFirst)
                    HanApplication.getInstance(this).getHanDevice().displayAndPlayTTS(getString(R.string.no_result), true);
                else
                    HanApplication.getInstance(this).getHanDevice().displayAndPlayTTS(getString(R.string.plz_try_again), true);
                getHanProgressDialog().dismiss();
                busStopInfoViewModel.stopTimer();
            } else {
                busArriveList.clear();
                for (int i = 0; i < busArriveTimeInfoResultList.size(); i++) {
                    busArriveList.add(busArriveTimeInfoResultList.get(i).getBusName());
                }
                searchBusArriveListViewAdapter.notifyDataSetChanged();

                if (timerIsFirst) {
                    timerIsFirst = false;
                    getHanProgressDialog().dismiss();
                    HanApplication.getInstance(this).getHanDevice().displayAndPlayTTS(getString(R.string.complete_search), true);
                    binding.busArriveResultLayout.setVisibility(View.VISIBLE);
                    binding.searchBusArriveListview.requestFocus();
                    binding.searchBusArriveListview.setSelection(0);
                }
            }
        });
    }

    // 버스 정류장 검색
    public void onClickedSearchBusStop() {
        busStopInfoViewModel.stopTimer();
        binding.busStopResultLayout.setVisibility(View.GONE);
        binding.busArriveResultLayout.setVisibility(View.GONE);
        if (!isWifiOn()) return;

        if (RegionId.getInstance().getRegionId(binding.regionSelectSpinner.getSelectedItem().toString())
                == RegionId.getInstance().getRegionId(getString(R.string.gyeonggi))) {
            // 경기 지역 선택 대화상자 실행
            HanApplication.getInstance(this).getHanDevice().displayAndPlayTTS(getString(R.string.select_detail_region), true);
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View v = inflater.inflate(R.layout.layout_region_select_dlg, null);
            GyeongggiRegionSelectDialog dialog = new GyeongggiRegionSelectDialog(this, v, new ListenerSelectGyeonggiRegion());
            dialog.setContentView(v);
            dialog.setTitle(getString(R.string.gyeonggi_region_select));
            dialog.show();
        } else {
            HanApplication.getInstance(this).getHanDevice().displayAndPlayTTS(getString(R.string.start_search_bus_stop), true);
            getHanProgressDialog().show();
            regionName = binding.regionSelectSpinner.getSelectedItem().toString();
            busStopInfoViewModel.searchBusStop(regionName, binding.busStopEdittext.getText().toString());
        }
    }

    // 버스 도착 정보 검색
    public void onClickedSearchBusArrive() {
        busStopInfoViewModel.stopTimer();
        binding.busArriveResultLayout.setVisibility(View.GONE);
        if (!isWifiOn()) return;

        timerIsFirst = true;
        HanApplication.getInstance(this).getHanDevice().displayAndPlayTTS(binding.searchBusStopListview.getSelectedItem().toString().split(getString(R.string.slash))[0] + getString(R.string.space) + getString(R.string.start_search_bus_stop), true);
        getHanProgressDialog().show();

        busStopInfoViewModel.setNowPosition(binding.searchBusStopListview.getSelectedItemPosition());
        busStopInfoViewModel.searchBusArrive(regionName, busStopIdList.get(binding.searchBusStopListview.getSelectedItemPosition()));
    }

    // 즐겨 찾기 추가
    public void onClickedAddBookMark() {
        busStopInfoViewModel.addBookMark(this, regionName);
        HanApplication.getInstance(this).getHanDevice().displayAndPlayTTS(getString(R.string.complete_add_bookmark), true);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        setIsShowPopupCancel(false);

        switch (item.getItemId()) {
            case R.id.bookmark:
                finish();
                break;

            case R.id.exit:
                finish();
                break;

            default:
                setIsShowPopupCancel(true);
                break;
        }
        return true;
    }

    // 경기 지역 대화 상자에서 callback 하기 위해 사용될 Listener
    public class ListenerSelectGyeonggiRegion {
        // 경기 세부 지역 선택 완료 후 검색 시작
        public void completeSelectDetailRegion(String name) {
            regionName = name;
            getHanProgressDialog().show();
            busStopInfoViewModel.stopTimer();
            binding.busArriveResultLayout.setVisibility(View.GONE);
            HanApplication.getInstance(getApplicationContext()).getHanDevice().displayAndPlayTTS(getString(R.string.start_search_bus_stop), true);
            busStopInfoViewModel.searchBusStop(regionName, binding.busStopEdittext.getText().toString());
        }
    }
}
