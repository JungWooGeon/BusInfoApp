package com.selvashc.webtools.businfo.view.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;

import com.jawon.han.widget.HanApplication;
import com.jawon.han.widget.adapter.HanStringArrayAdapter;
import com.selvashc.webtools.businfo.R;
import com.selvashc.webtools.businfo.databinding.ActivitySubwayArriveBinding;
import com.selvashc.webtools.businfo.viewmodel.SubwayStopInfoViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * 지하철 도착 정보 화면
 *  - 지하철 정류장 이름과 지하철 도착 정보를 검색 (ViewModel 에 요청)
 *  - 즐겨 찾기 추가
 */
public class SubwayStopInfoActivity extends BaseActivity {

    // ViewModel
    SubwayStopInfoViewModel subwayStopInfoViewModel;

    // ListView 에 사용될 List
    private List<String> subwayStopNameList = new ArrayList<>();
    private List<String> subwayArriveList = new ArrayList<>();

    // ListView Adapter
    private HanStringArrayAdapter searchBusStopListViewAdapter;
    private HanStringArrayAdapter searchBusArriveListViewAdapter;

    // 지하철 도착 검색은 30초마다 갱신되는데, 처음으로 검색할 때를 구별하기 위해 사용
    private boolean timerIsFirst;

    private ActivitySubwayArriveBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_subway_arrive);
        binding.setActivity(this);

        getHanMenuPopup().inflate(R.menu.popup_menu_bus_info);
        bindViews();
        initViewModel();
    }

    @Override
    protected void onDestroy() {
        subwayStopInfoViewModel.stopTimer();
        super.onDestroy();
    }

    private void bindViews() {
        List<String> regionSelectList = new ArrayList<>();
        regionSelectList.add(getString(R.string.capital));

        binding.regionSelectSpinner.setAdapter(new HanStringArrayAdapter(this, android.R.layout.simple_spinner_item, regionSelectList));

        // EditText 에서 엔터키를 누르면 바로 검색할 수 있도록 설정
        binding.subwayStopEdittext.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_UP) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                onClickedSearchSubwayStop();
                return true;
            }
            return false;
        });

        searchBusStopListViewAdapter = new HanStringArrayAdapter(this, android.R.layout.simple_list_item_1, subwayStopNameList);
        binding.searchSubwayStopListview.setAdapter(searchBusStopListViewAdapter);
        binding.searchSubwayStopListview.setOnItemClickListener((parent, view, position, id) -> onClickedSearchSubwayArrive());

        searchBusArriveListViewAdapter = new HanStringArrayAdapter(this, android.R.layout.simple_list_item_1, subwayArriveList);
        binding.searchSubwayArriveListview.setAdapter(searchBusArriveListViewAdapter);
        binding.searchSubwayArriveListview.setOnItemClickListener((parent, view, position, id)
                -> HanApplication.getInstance(this).getHanDevice().displayAndPlayTTS(binding.searchSubwayArriveListview.getSelectedItem().toString(), true));
    }

    private void initViewModel() {
        // 정류장 검색 결과 update
        subwayStopInfoViewModel = new SubwayStopInfoViewModel(getResources());
        subwayStopInfoViewModel.getSubwayStopInfoList().observe(this, subwayStopInfoResultList -> {
            getHanProgressDialog().dismiss();

            if (subwayStopInfoResultList == null) {
                HanApplication.getInstance(this).getHanDevice().displayAndPlayTTS(getString(R.string.api_error), true);
                return;
            }

            if (subwayStopInfoResultList.isEmpty()) {
                HanApplication.getInstance(this).getHanDevice().displayAndPlayTTS(getString(R.string.no_result), true);
            } else {
                HanApplication.getInstance(this).getHanDevice().displayAndPlayTTS(getString(R.string.complete_search), true);
                subwayStopNameList.clear();
                subwayStopNameList.addAll(subwayStopInfoResultList);
                searchBusStopListViewAdapter.notifyDataSetChanged();
                binding.subwayStopResultLayout.setVisibility(View.VISIBLE);
                binding.searchSubwayStopListview.requestFocus();
                binding.searchSubwayStopListview.setSelection(0);
            }
        });

        // 지하철 도착 정보 결과 update
        subwayStopInfoViewModel.getSubwayArriveInfoList().observe(this, subwayArriveTimeInfoResultList -> {
            if (subwayArriveTimeInfoResultList == null) {
                HanApplication.getInstance(this).getHanDevice().displayAndPlayTTS(getString(R.string.api_error), true);
                subwayArriveList.clear();
                binding.subwayArriveResultLayout.setVisibility(View.GONE);
                getHanProgressDialog().dismiss();
                subwayStopInfoViewModel.stopTimer();
                return;
            }

            if (subwayArriveTimeInfoResultList.isEmpty()) {
                if (timerIsFirst)
                    HanApplication.getInstance(this).getHanDevice().displayAndPlayTTS(getString(R.string.no_result), true);
                else
                    HanApplication.getInstance(this).getHanDevice().displayAndPlayTTS(getString(R.string.plz_try_again), true);
                getHanProgressDialog().dismiss();
                subwayStopInfoViewModel.stopTimer();
            } else {
                subwayArriveList.clear();

                for (int i = 0; i < subwayArriveTimeInfoResultList.size(); i++) {
                    subwayArriveList.add(subwayArriveTimeInfoResultList.get(i).getUpdownLine() + getString(R.string.space) + subwayArriveTimeInfoResultList.get(i).getTrainLineNumber() +
                            getString(R.string.space) + subwayArriveTimeInfoResultList.get(i).getArriveMsg());
                }
                searchBusArriveListViewAdapter.notifyDataSetChanged();

                if (timerIsFirst) {
                    timerIsFirst = false;
                    getHanProgressDialog().dismiss();
                    HanApplication.getInstance(this).getHanDevice().displayAndPlayTTS(getString(R.string.complete_search), true);
                    binding.subwayArriveResultLayout.setVisibility(View.VISIBLE);
                    binding.searchSubwayArriveListview.requestFocus();
                    binding.searchSubwayArriveListview.setSelection(0);
                }
            }
        });
    }

    // 지하철 정류장 검색
    public void onClickedSearchSubwayStop() {
        if (!isWifiOn()) {
            return;
        }
        subwayStopInfoViewModel.stopTimer();
        binding.subwayStopResultLayout.setVisibility(View.GONE);
        binding.subwayArriveResultLayout.setVisibility(View.GONE);

        HanApplication.getInstance(this).getHanDevice().displayAndPlayTTS(getString(R.string.start_search_bus_stop), true);
        getHanProgressDialog().show();
        subwayStopInfoViewModel.searchSubwayStop(binding.subwayStopEdittext.getText().toString());
    }

    // 지하철 도착 정보 검색
    public void onClickedSearchSubwayArrive() {
        if (!isWifiOn()) {
            return;
        }
        subwayStopInfoViewModel.stopTimer();
        binding.subwayArriveResultLayout.setVisibility(View.GONE);

        timerIsFirst = true;
        HanApplication.getInstance(this).getHanDevice().displayAndPlayTTS(binding.searchSubwayStopListview.getSelectedItem().toString().split(getString(R.string.slash))[0] + getString(R.string.space) + getString(R.string.start_search_bus_stop), true);
        getHanProgressDialog().show();

        subwayStopInfoViewModel.searchSubwayArrive(binding.searchSubwayStopListview.getSelectedItemPosition());
    }

    // 즐겨 찾기 추가
    public void onClickedAddBookMark() {
        subwayStopInfoViewModel.addBookMark(this, binding.regionSelectSpinner.getSelectedItem().toString());
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
}
