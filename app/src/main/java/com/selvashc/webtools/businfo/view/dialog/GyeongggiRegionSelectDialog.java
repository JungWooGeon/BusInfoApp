package com.selvashc.webtools.businfo.view.dialog;

import android.content.Context;
import android.view.KeyEvent;
import android.view.View;

import com.jawon.han.key.HanBrailleKey;
import com.jawon.han.key.keyboard.usb.USB2Braille;
import com.jawon.han.util.HimsCommonFunc;
import com.jawon.han.widget.HanApplication;
import com.jawon.han.widget.HanDialog;
import com.jawon.han.widget.HanSpinner;
import com.jawon.han.widget.adapter.HanStringArrayAdapter;
import com.selvashc.webtools.businfo.R;
import com.selvashc.webtools.businfo.data.RegionId;
import com.selvashc.webtools.businfo.view.activity.BusInfoActivity;
import com.selvashc.webtools.businfo.view.activity.BusStopInfoActivity;

import java.util.ArrayList;
import java.util.Map;

/**
 * 검색 시 지역 선택이 '경기'로 되어있을 경우 세부 지역을 고르기 위한 Dialog
 * 선택이 완료되면 세부 지역으로 선택한 지역을 생성자에서 받은 Listener 를 통해 전달해준다.
 */
public class GyeongggiRegionSelectDialog extends HanDialog {

    private HanSpinner selectSpinner;

    // callback listener
    private BusInfoActivity.ListenerSelectGyeonggiRegion busListener;
    private BusStopInfoActivity.ListenerSelectGyeonggiRegion busStoplistener;

    public GyeongggiRegionSelectDialog(Context context, View v, BusInfoActivity.ListenerSelectGyeonggiRegion listenerSelectGyeonggiRegion) {
        super(context);
        busListener = listenerSelectGyeonggiRegion;
        init(v);
    }

    public GyeongggiRegionSelectDialog(Context context, View v, BusStopInfoActivity.ListenerSelectGyeonggiRegion listenerSelectGyeonggiRegion) {
        super(context);
        busStoplistener = listenerSelectGyeonggiRegion;
        init(v);
    }

    private void init(View v) {
        // init spinner, button
        selectSpinner = v.findViewById(R.id.select_spinner);
        ArrayList<String> itemList = new ArrayList<>();
        for (Map.Entry<Integer, String> entry : RegionId.getInstance(getContext().getResources()).getRegionIdName().entrySet()) {
            if (entry.getKey() == RegionId.getInstance().getDaejeonId() || entry.getKey() == RegionId.getInstance().getGyeonggiId()
                    || entry.getKey() == RegionId.getInstance().getIncheonId() || entry.getKey() == RegionId.getInstance().getSejongId()
                    || entry.getKey() == RegionId.getInstance().getGwangjuId() || entry.getKey() == RegionId.getInstance().getDaeguId()
                    || entry.getKey() == RegionId.getInstance().getUlsanId() || entry.getKey() == RegionId.getInstance().getJejuId()
                    || entry.getKey() == RegionId.getInstance().getWonjuId() || entry.getKey() == RegionId.getInstance().getBusanId())
                continue;
            itemList.add(entry.getValue());
        }
        selectSpinner.setAdapter(new HanStringArrayAdapter(getContext(), android.R.layout.simple_spinner_item, itemList));
        // enter 입력 시 옵션 저장
        selectSpinner.setOnKeyListener((view, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_UP) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                saveOption();
                return true;
            } else
                return false;
        });
        v.findViewById(R.id.save_button).setOnClickListener(v1 -> saveOption());
        v.findViewById(R.id.cancel_button).setOnClickListener(v1 -> dismiss());
    }

    // 설정한 옵션을 listener 를 통해 전달
    private void saveOption() {
        dismiss();
        HanApplication.getInstance(getContext()).getHanDevice().displayAndPlayTTS(getContext().getString(R.string.complete_select_region), true);
        if (busListener != null)
            busListener.completeSelectDetailRegion(selectSpinner.getSelectedItem().toString());
        else if (busStoplistener != null)
            busStoplistener.completeSelectDetailRegion(selectSpinner.getSelectedItem().toString());
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        final int scanCode = USB2Braille.getInstance().convertUSBtoBraille(getContext(), event);
        if (event.getAction() == KeyEvent.ACTION_UP && HimsCommonFunc.isExitKey(event.getScanCode(), event.getKeyCode())) {
            this.dismiss();
            return true;
        }
        if (event.getAction() == KeyEvent.ACTION_UP && (scanCode == (HanBrailleKey.HK_ADVANCE4) ||
                (scanCode == (HanBrailleKey.HK_Z | HanBrailleKey.HK_SPACE)))) {
            this.dismiss();
            return true;
        }
        return super.dispatchKeyEvent(event);
    }
}
