package com.selvashc.webtools.businfo.view.activity;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import com.jawon.han.key.HanBrailleKey;
import com.jawon.han.key.keyboard.usb.USB2Braille;
import com.jawon.han.util.HimsConstant;
import com.jawon.han.util.HimsCtrlType;
import com.jawon.han.widget.HanApplication;
import com.jawon.han.widget.adapter.HanStringArrayAdapter;
import com.selvashc.webtools.businfo.R;
import com.selvashc.webtools.businfo.data.RegionId;
import com.selvashc.webtools.businfo.databinding.ActivityBusRouteBinding;
import com.selvashc.webtools.businfo.view.dialog.GyeongggiRegionSelectDialog;
import com.selvashc.webtools.businfo.viewmodel.BusInfoViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * 버스 노선 정보 화면
 *  - 버스 번호와 버스 경로 정보 검색 (ViewModel 에 요청)
 *  - 즐겨 찾기 추가
 */
public class BusInfoActivity extends BaseActivity {

    // ViewModel
    private BusInfoViewModel busInfoViewModel;

    // ListView 에 사용될 List
    private List<String> busNameList = new ArrayList<>();

    // 버스 ID를 저장할 List
    private List<String> busIdList = new ArrayList<>();

    // ListView Adapter
    private HanStringArrayAdapter searchBusListViewAdapter;

    // 지역 선택 목록에서 선택한 지역 이름
    private String regionName;

    private ActivityBusRouteBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_bus_route);
        binding.setActivity(this);

        getHanMenuPopup().inflate(R.menu.popup_menu_bus_info);
        bindViews();
        initViewModel();
    }

    private void bindViews() {
        // EditText 에서 엔터키를 누르면 바로 검색할 수 있도록 설정
        binding.busNumberEdittext.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_UP) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                onClickedSearchBusInfo();
                return true;
            }
            return false;
        });
        binding.busNumberEdittext.setMaskAndInputType(HimsConstant.NUMBER_ONLY, false);

        binding.busRouteResultEdittext.setControlTypeLanbable(HimsCtrlType.CON_TYPE_MEB);
        binding.busRouteResultEdittext.setReadOnly(true);

        searchBusListViewAdapter = new HanStringArrayAdapter(this, android.R.layout.simple_list_item_1, busNameList);
        binding.searchBusListview.setAdapter(searchBusListViewAdapter);
        binding.searchBusListview.setOnItemClickListener((parent, view, position, id) -> onClickedSearchBusRoute());
    }

    private void initViewModel() {
        // 버스 번호 정보 검색 결과 update
        busInfoViewModel = new BusInfoViewModel(getResources());
        busInfoViewModel.getBusInfoList().observe(this, busInfoResultList -> {
            getHanProgressDialog().dismiss();

            if (busInfoResultList == null) {
                HanApplication.getInstance(this).getHanDevice().displayAndPlayTTS(getString(R.string.api_error), true);
                return;
            }

            if (busInfoResultList.isEmpty()) {
                HanApplication.getInstance(this).getHanDevice().displayAndPlayTTS(getString(R.string.no_result), true);
            } else {
                HanApplication.getInstance(this).getHanDevice().displayAndPlayTTS(getString(R.string.complete_search), true);
                busNameList.clear();
                busIdList.clear();
                for (int i = 0; i < busInfoResultList.size(); i++) {
                    busNameList.add(busInfoResultList.get(i).getBusNum());
                    busIdList.add(busInfoResultList.get(i).getBusId());
                }
                searchBusListViewAdapter.notifyDataSetChanged();
                binding.busNodeResultLayout.setVisibility(View.VISIBLE);
                binding.searchBusListview.requestFocus();
                binding.searchBusListview.setSelection(0);
            }
        });

        // 버스 경로 정보 검색 결과 update
        busInfoViewModel.getBusRouteResultList().observe(this, busRouteResultString -> {
            getHanProgressDialog().dismiss();

            if (busRouteResultString == null) {
                HanApplication.getInstance(this).getHanDevice().displayAndPlayTTS(getString(R.string.api_error), true);
                return;
            }

            if (busRouteResultString.equals("")) {
                HanApplication.getInstance(this).getHanDevice().displayAndPlayTTS(getString(R.string.no_result), true);
            } else {
                HanApplication.getInstance(this).getHanDevice().displayAndPlayTTS(getString(R.string.complete_search), true);
                binding.busRouteResultEdittext.setText(busRouteResultString);
                binding.busRouteResultLayout.setVisibility(View.VISIBLE);
                binding.busRouteResultEdittext.requestFocus();
            }
        });
    }

    // 버스 번호 검색
    public void onClickedSearchBusInfo() {
        if (!isWifiOn())
            return;

        binding.busNodeResultLayout.setVisibility(View.GONE);
        binding.busRouteResultLayout.setVisibility(View.GONE);

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
            HanApplication.getInstance(this).getHanDevice().displayAndPlayTTS(getString(R.string.start_search_bus), true);
            getHanProgressDialog().show();
            regionName = binding.regionSelectSpinner.getSelectedItem().toString();
            busInfoViewModel.searchBusInfo(regionName, binding.busNumberEdittext.getText().toString());
        }
    }

    // 버스 경로 검색
    public void onClickedSearchBusRoute() {
        binding.busRouteResultLayout.setVisibility(View.GONE);
        if (!isWifiOn()) return;

        HanApplication.getInstance(this).getHanDevice().displayAndPlayTTS(getString(R.string.start_search_bus_node), true);
        getHanProgressDialog().show();
        busInfoViewModel.setNowPosition(binding.searchBusListview.getSelectedItemPosition());
        busInfoViewModel.searchBusRoute(regionName, busIdList.get(binding.searchBusListview.getSelectedItemPosition()));
    }

    // 즐겨 찾기 추가
    public void onClickedAddBookMark() {
        busInfoViewModel.addBookMark(this);
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

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        final int scanCode = USB2Braille.getInstance().convertUSBtoBraille(this, event);

        if (event.getAction() == KeyEvent.ACTION_UP && (scanCode == (HanBrailleKey.HK_V | HanBrailleKey.HK_ENTER))) {
            finish();
            return true;
        }

        return super.dispatchKeyEvent(event);
    }

    // 경기 지역 대화 상자에서 callback 하기 위해 사용될 Listener
    public class ListenerSelectGyeonggiRegion {
        // 경기 세부 지역 선택 완료 후 검색 요청
        public void completeSelectDetailRegion(String name) {
            regionName = name;
            getHanProgressDialog().show();
            HanApplication.getInstance(getApplicationContext()).getHanDevice().displayAndPlayTTS(getString(R.string.start_search_bus), true);
            busInfoViewModel.searchBusInfo(regionName, binding.busNumberEdittext.getText().toString());
        }
    }
}