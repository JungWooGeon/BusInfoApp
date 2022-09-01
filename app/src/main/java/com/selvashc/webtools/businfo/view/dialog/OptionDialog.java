package com.selvashc.webtools.businfo.view.dialog;

import android.content.Context;
import android.content.SharedPreferences;
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

import java.util.ArrayList;

/**
 * MainActivity 에서 옵션 설정 시 버스 도착 시간을 설정하기 위한 Dialog
 * 옵션 설정을 완료하면 SharedPreferences 에 데이터를 저장한다.
 */
public class OptionDialog extends HanDialog {

    private HanSpinner alarmSpinner;

    // 설정한 시간 설정 옵션을 저장할 변수
    private String timeOption;

    private SharedPreferences sharedPreferences;

    public OptionDialog(Context c, View view) {
        super(c);

        // init
        sharedPreferences = getContext().getSharedPreferences(getContext().getString(R.string.file_name_fileoption), Context.MODE_PRIVATE);

        view.findViewById(R.id.save_button).setOnClickListener(vi -> saveOption());
        view.findViewById(R.id.cancel_button).setOnClickListener(vi -> dismiss());
        alarmSpinner = view.findViewById(R.id.alarm_spinner);

        ArrayList<String> itemList = new ArrayList<>();
        itemList.add(getContext().getString(R.string.no_use));
        itemList.add(getContext().getString(R.string.within_3_minute));
        itemList.add(getContext().getString(R.string.within_7_minute));
        itemList.add(getContext().getString(R.string.within_10_minute));

        alarmSpinner.setAdapter(new HanStringArrayAdapter(getContext(), android.R.layout.simple_spinner_item, itemList));
        alarmSpinner.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_UP) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                saveOption();
                return true;
            } else
                return false;
        });

        // 이미 설정되어 있는 옵션 설정으로 Spinner 의 값을 셋팅해줌
        timeOption = sharedPreferences.getString(getContext().getString(R.string.file_name_fileoption), getContext().getString(R.string.number_minus_1));
        int position = Integer.parseInt(timeOption);

        final int NO_USE = -1;
        final int THREE_MINUTE = 3;
        final int SEVEN_MINUTE = 7;
        final int TEN_MINUTE = 10;

        switch (position) {
            case NO_USE:
                position = 0;
                break;
            case THREE_MINUTE:
                position = 1;
                break;
            case SEVEN_MINUTE:
                position = 2;
                break;
            case TEN_MINUTE:
                position = 3;
                break;
            default:
                position = 0;
                break;
        }
        alarmSpinner.setSelection(position);
    }

    // 설정한 옵션을 SharedPreference 에 저장
    private void saveOption() {
        String position = getContext().getString(R.string.number_minus_1);
        if (alarmSpinner.getSelectedItemPosition() == 0) {
            position = getContext().getString(R.string.number_minus_1);
        } else if (alarmSpinner.getSelectedItemPosition() == 1) {
            position = getContext().getString(R.string.number_3);
        } else if (alarmSpinner.getSelectedItemPosition() == 2) {
            position = getContext().getString(R.string.number_7);
        } else if (alarmSpinner.getSelectedItemPosition() == 3) {
            position = getContext().getString(R.string.number_10);
        }
        HanApplication.getInstance(getContext()).getHanDevice().displayAndPlayTTS(getContext().getString(R.string.save_option), true);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getContext().getString(R.string.file_name_fileoption), position);
        editor.apply();

        timeOption = position;
        dismiss();
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