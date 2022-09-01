package com.selvashc.webtools.businfo.view.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import com.jawon.han.key.keyboard.usb.USB2Braille;
import com.jawon.han.util.HimsCtrlType;
import com.jawon.han.widget.HanApplication;
import com.jawon.han.widget.adapter.HanStringArrayAdapter;
import com.selvashc.webtools.businfo.R;
import com.jawon.han.key.HanBrailleKey;
import com.jawon.han.widget.HanMenuPopup;
import com.selvashc.webtools.businfo.data.RegionId;
import com.selvashc.webtools.businfo.data.SubwayId;
import com.selvashc.webtools.businfo.databinding.ActivityMainBinding;
import com.selvashc.webtools.businfo.view.dialog.OptionDialog;
import com.selvashc.webtools.businfo.viewmodel.BookMarkViewModel;
import com.selvashc.webtools.businfo.viewmodel.BusInfoViewModel;
import com.selvashc.webtools.businfo.viewmodel.BusStopInfoViewModel;
import com.selvashc.webtools.businfo.viewmodel.SubwayStopInfoViewModel;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * 메인화면
 * - 즐겨 찾기 목록 업데이트 및 삭제
 * - 버스 노선 정보, 도착 정보, 지하철 도착 정보 검색
 * - 버스 노선 정보, 도착 정보, 지하철 도착 정보 화면으로 전환
 * - 옵션 설정
 */
public class MainActivity extends BaseActivity implements HanMenuPopup.OnMenuItemClickListener, HanMenuPopup.OnDismissListener {

    // ViewModel
    private BookMarkViewModel bookMarkViewModel;
    private BusInfoViewModel busInfoViewModel;
    private BusStopInfoViewModel busStopInfoViewModel;
    private SubwayStopInfoViewModel subwayStopInfoViewModel;

    // ListView 에 사용될 List
    private List<String> bookmarkList = new ArrayList<>();
    private List<String> arriveResultList = new ArrayList<>();

    // 즐겨 찾기 목록 ID 와 버스 도착 정보 검색 시 00분 이내 알람을 주기 위해 저장할 List
    private List<String> bookMarkId = new ArrayList<>();
    private List<String> busArriveTImeInfoList = new ArrayList<>();

    // ListView Adapter
    private HanStringArrayAdapter bookmarkListViewAdapter;
    private HanStringArrayAdapter resultBusArriveAdapter;

    // 옵션 설정 결과 조회 용도
    private SharedPreferences sharedPreferences;
    private boolean timerIsFirst;

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setActivity(this);

        // Wifi 가 연결되어 있지 않으면 종료 (isWifiOn() 은 Base 에 정의)
        if (isWifiOn()) {
            getHanMenuPopup().inflate(R.menu.popup_menu_main);
            bindViews();
            initViewModel();
            binding.deleteBookmarkButton.setHotKey(HanBrailleKey.HK_D, HanBrailleKey.HK_SPACE);
            sharedPreferences = getSharedPreferences(getString(R.string.file_name_fileoption), MODE_PRIVATE);
            RegionId.getInstance(getApplicationContext().getResources());
            SubwayId.getInstance(getApplicationContext().getResources());
            trustAllHosts();
        }
    }

    @Override
    protected void onPause() {
        // 화면 전환 시 timer 종료
        busStopInfoViewModel.stopTimer();
        binding.resultLayout.setVisibility(View.GONE);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        // finish() 사용 시, onPause 를 거치지 않고 종료되어 onDestroy 에서도 Timer 종료
        if (busStopInfoViewModel != null)
            busStopInfoViewModel.stopTimer();

        moveTaskToBack(true);
        finishAndRemoveTask();
        System.exit(0);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 화면이 다시 돌아오면 즐겨 찾기 목록이 추가 되어 있을 수 있으므로, 즐겨 찾기 목록을 읽어 update
        if (isWifiOn())
            bookMarkViewModel.readBookMark(this);
    }

    public void bindViews() {
        final int SEARCH_NAME = 0;
        final int SEARCH_TYPE = 0;
        final int SEARCH_REGION_NAME = 1;
        final int SEARCH_NAME_ID = 2;

        // wifi 가 연결 된 것을 확인한 후에 layout 이 나타나야 하기 때문에 init 에서 visible 설정
        binding.layout.setVisibility(View.VISIBLE);

        bookmarkListViewAdapter = new HanStringArrayAdapter(this, android.R.layout.simple_list_item_1, bookmarkList);
        binding.bookmarkListview.setAdapter(bookmarkListViewAdapter);
        binding.bookmarkListview.setEmptyView(binding.activityMainEmptyview);
        binding.bookmarkListview.setOnItemClickListener((parent, view, position, id) -> {
            if (!isWifiOn()) return;

            busStopInfoViewModel.stopTimer();
            getHanProgressDialog().show();
            binding.resultLayout.setVisibility(View.VISIBLE);

            String[] items = bookmarkListViewAdapter.getItem(position).split(getString(R.string.slash));

            if (items[SEARCH_TYPE].equals(getString(R.string.bus_node_info_no_space))) {
                // 버스 노선 정보에 대한 검색
                String[] item = items[SEARCH_NAME_ID].split(getString(R.string.greater_than_sign));
                HanApplication.getInstance(this).getHanDevice().displayAndPlayTTS(item[SEARCH_NAME] + getString(R.string.space) + getString(R.string.start_search_bus_node), true);
                busInfoViewModel.searchBusRoute(items[SEARCH_REGION_NAME], bookMarkId.get(position));
            } else if (items[SEARCH_TYPE].equals(getString(R.string.bus_arrive_info_no_space))) {
                // 버스 도착 정보에 대한 검색
                HanApplication.getInstance(this).getHanDevice().displayAndPlayTTS(items[SEARCH_NAME_ID] + getString(R.string.space) + getString(R.string.start_search_bus_stop), true);
                timerIsFirst = true;
                busStopInfoViewModel.searchBusArrive(items[SEARCH_REGION_NAME], bookMarkId.get(position));
            } else if (items[SEARCH_TYPE].equals(getString(R.string.subway_arrive_info_no_space))) {
                // 지하철 도착 정보에 대한 검색
                HanApplication.getInstance(this).getHanDevice().displayAndPlayTTS(items[SEARCH_NAME_ID] + getString(R.string.space) + getString(R.string.start_search_bus_stop), true);
                timerIsFirst = true;
                String[] item = items[SEARCH_NAME_ID].split(getString(R.string.space));
                subwayStopInfoViewModel.searchSubwayArrive(item[0], item[1]);
            }
        });

        binding.searchBookmarkResultEditext.setControlTypeLanbable(HimsCtrlType.CON_TYPE_MEB);
        binding.searchBookmarkResultEditext.setReadOnly(true);
        resultBusArriveAdapter = new HanStringArrayAdapter(this, android.R.layout.simple_list_item_1, arriveResultList);
        binding.searchBookmarkResultListview.setAdapter(resultBusArriveAdapter);
        binding.searchBookmarkResultListview.setOnItemClickListener((parent, view, position, id)
                -> HanApplication.getInstance(this).getHanDevice().displayAndPlayTTS(binding.searchBookmarkResultListview.getSelectedItem().toString(), true));
    }

    private void initViewModel() {
        // 즐겨 찾기 목록 update
        bookMarkViewModel = new BookMarkViewModel(this);
        bookMarkViewModel.getBookMarkList().observe(this, bookMarkInfoList -> {
            assert bookMarkInfoList != null;

            bookmarkList.clear();
            bookMarkId.clear();

            for (int i = 0; i < bookMarkInfoList.size(); i++) {
                bookmarkList.add(bookMarkInfoList.get(i).getBookMarkName());
                bookMarkId.add(bookMarkInfoList.get(i).getBookMarkId());
            }
            bookmarkListViewAdapter.notifyDataSetChanged();

            if (!bookMarkInfoList.isEmpty())
                binding.deleteBookmarkButton.setVisibility(View.VISIBLE);
            else
                binding.deleteBookmarkButton.setVisibility(View.GONE);
        });

        // 버스 노선 정보 검색 결과 update
        busInfoViewModel = new BusInfoViewModel(getResources());
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
                binding.searchBookmarkResultEditext.setText(busRouteResultString);
                binding.searchBookmarkResultEditext.setVisibility(View.VISIBLE);
                binding.searchBookmarkResultListview.setVisibility(View.GONE);
                binding.searchBookmarkResultEditext.requestFocus();
            }
        });

        // 버스 도착 정보 검색 결과 update
        busStopInfoViewModel = new BusStopInfoViewModel(getResources());
        busStopInfoViewModel.getBusArriveTimeInfoList().observe(this, busArriveTimeInfoResultList -> {
            if (busArriveTimeInfoResultList == null) {
                HanApplication.getInstance(this).getHanDevice().displayAndPlayTTS(getString(R.string.plz_try_again), true);
                arriveResultList.clear();
                busArriveTImeInfoList.clear();
                binding.resultLayout.setVisibility(View.GONE);
                busStopInfoViewModel.stopTimer();
                getHanProgressDialog().dismiss();
                return;
            }

            if (busArriveTimeInfoResultList.isEmpty()) {
                if (timerIsFirst) {
                    HanApplication.getInstance(this).getHanDevice().displayAndPlayTTS(getString(R.string.no_result), true);
                } else {
                    HanApplication.getInstance(this).getHanDevice().displayAndPlayTTS(getString(R.string.plz_try_again), true);
                }
                getHanProgressDialog().dismiss();
                busStopInfoViewModel.stopTimer();
            } else {
                arriveResultList.clear();
                busArriveTImeInfoList.clear();
                for (int i = 0; i < busArriveTimeInfoResultList.size(); i++) {
                    arriveResultList.add(busArriveTimeInfoResultList.get(i).getBusName());
                    busArriveTImeInfoList.add(busArriveTimeInfoResultList.get(i).getArriveTime());
                }
                resultBusArriveAdapter.notifyDataSetChanged();

                if (timerIsFirst) {
                    timerIsFirst = false;
                    getHanProgressDialog().dismiss();
                    HanApplication.getInstance(this).getHanDevice().displayAndPlayTTS(getString(R.string.complete_search), true);
                    binding.searchBookmarkResultEditext.setVisibility(View.GONE);
                    binding.searchBookmarkResultListview.setVisibility(View.VISIBLE);
                    binding.searchBookmarkResultListview.requestFocus();
                    binding.searchBookmarkResultListview.setSelection(0);
                } else {
                    if (Integer.parseInt(busArriveTImeInfoList.get(binding.searchBookmarkResultListview.getSelectedItemPosition()))
                            <= Integer.parseInt(sharedPreferences.getString(getString(R.string.file_name_fileoption), getString(R.string.number_minus_1)))) {
                        HanApplication.getInstance(this).getHanDevice().displayAndPlayTTS(busArriveTimeInfoResultList.get(binding.searchBookmarkResultListview.getSelectedItemPosition()).getBusName(), true);
                    }
                }
            }
        });

        // 지하철 도착 검색 결과 update
        subwayStopInfoViewModel = new SubwayStopInfoViewModel(getResources());
        subwayStopInfoViewModel.getSubwayArriveInfoList().observe(this, subwayArriveTimeInfoResultList -> {
            if (subwayArriveTimeInfoResultList == null) {
                HanApplication.getInstance(this).getHanDevice().displayAndPlayTTS(getString(R.string.api_error), true);
                arriveResultList.clear();
                binding.resultLayout.setVisibility(View.GONE);
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
                arriveResultList.clear();

                for (int i = 0; i < subwayArriveTimeInfoResultList.size(); i++) {
                    arriveResultList.add(subwayArriveTimeInfoResultList.get(i).getUpdownLine() + getString(R.string.space) + subwayArriveTimeInfoResultList.get(i).getTrainLineNumber() +
                            getString(R.string.space) + subwayArriveTimeInfoResultList.get(i).getArriveMsg());
                }
                resultBusArriveAdapter.notifyDataSetChanged();

                if (timerIsFirst) {
                    timerIsFirst = false;
                    getHanProgressDialog().dismiss();
                    HanApplication.getInstance(this).getHanDevice().displayAndPlayTTS(getString(R.string.complete_search), true);
                    binding.searchBookmarkResultEditext.setVisibility(View.GONE);
                    binding.searchBookmarkResultListview.setVisibility(View.VISIBLE);
                    binding.searchBookmarkResultListview.requestFocus();
                    binding.searchBookmarkResultListview.setSelection(0);
                }
            }
        });
    }

    // 즐겨 찾기 목록 삭제 - 단축키 사용으로 실행할 수 있도록 하기 위해 BaseActivity 에 있는 함수를 override
    public void onClickedDeleteBookmark() {
        busStopInfoViewModel.stopTimer();
        if (!bookmarkList.isEmpty()) {
            bookMarkViewModel.deleteBookMark(this, binding.bookmarkListview.getSelectedItemPosition());
            HanApplication.getInstance(this).getHanDevice().displayAndPlayTTS(getString(R.string.complete_delete_bookmark), true);
        } else
            HanApplication.getInstance(this).getHanDevice().displayAndPlayTTS(getString(R.string.no_delete_list), true);

        binding.resultLayout.setVisibility(View.GONE);
    }

    private void startDialog() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View v = inflater.inflate(R.layout.layout_custom_dlg, null);
        OptionDialog optionDialog = new OptionDialog(this, v);

        optionDialog.setContentView(v);
        optionDialog.setTitle(getString(R.string.option));
        optionDialog.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        setIsShowPopupCancel(false);

        switch (item.getItemId()) {
            case R.id.bus_node_info:
                Intent busInfoIntent = new Intent(getApplicationContext(), BusInfoActivity.class);
                startActivity(busInfoIntent);
                break;

            case R.id.bus_route_info:
                Intent busStopInfoIntent = new Intent(getApplicationContext(), BusStopInfoActivity.class);
                startActivity(busStopInfoIntent);
                break;

            case R.id.subway_stop_info:
                Intent subwayStopInfoIntent = new Intent(getApplicationContext(), SubwayStopInfoActivity.class);
                startActivity(subwayStopInfoIntent);

            case R.id.bookmark:
                // 즐겨찾기 화면으로 이동하는 것은 메인화면에 그대로 있는 것과 같기 때문에 아무 동작도 하지 않음
                break;

            case R.id.option:
                startDialog();
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

        if (event.getAction() != KeyEvent.ACTION_UP)
            return super.dispatchKeyEvent(event);

        // 엔터-R을 누를 경우 BusRouteActivity 실행
        if (scanCode == (HanBrailleKey.HK_R | HanBrailleKey.HK_ENTER)) {
            Intent intent = new Intent(getApplicationContext(), BusInfoActivity.class);
            startActivity(intent);
            return true;
        }

        // 스페이스-F를 누를 경우 BusArriveInfoActivity 실행
        if (scanCode == (HanBrailleKey.HK_F | HanBrailleKey.HK_SPACE)) {
            Intent intent = new Intent(getApplicationContext(), BusStopInfoActivity.class);
            startActivity(intent);
            return true;
        }

        // 스페이스-D를 누를 경우 현재 선택된 즐겨찾기 삭제
        if (scanCode == (HanBrailleKey.HK_D | HanBrailleKey.HK_SPACE)) {
            onClickedDeleteBookmark();
            return true;
        }

        // 백스페이스-O를 누를 경우 옵션 대화상자 호출
        if (scanCode == (HanBrailleKey.HK_O | HanBrailleKey.HK_BACKSPACE)) {
            startDialog();
            return true;
        }

        return super.dispatchKeyEvent(event);
    }

    // API 사용 시 URL 부분에서 CertPathValidatorException 오류가 발생하여 추가
    private void trustAllHosts() {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[]{};
            }

            @SuppressLint("TrustAllX509TrustManager")
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) {
                // 사용하지 않음
            }

            @SuppressLint("TrustAllX509TrustManager")
            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) {
                // 사용하지 않음
            }
        }};
        try {
            SSLContext sc = SSLContext.getInstance(getString(R.string.tls));
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.getCause();
        }
    }
}