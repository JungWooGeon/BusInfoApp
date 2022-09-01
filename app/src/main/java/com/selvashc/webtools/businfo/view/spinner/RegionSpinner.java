package com.selvashc.webtools.businfo.view.spinner;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;

import com.jawon.han.key.HanKeyTable;
import com.jawon.han.output.HanBeep;
import com.jawon.han.util.HimsCommonFunc;
import com.jawon.han.widget.HanSpinner;
import com.jawon.han.widget.adapter.HanStringArrayAdapter;
import com.jawon.han.widget.edittext.HanEditTextUtil;
import com.selvashc.webtools.businfo.R;

import java.util.ArrayList;

/**
 * 지역 정보를 선택할 수 있는 Spinner
 */
public class RegionSpinner extends HanSpinner {

    public RegionSpinner(Context c, AttributeSet attrs) {
        super(c, attrs);

        ArrayList<String> itemList = new ArrayList<>();
        itemList.add(getContext().getString(R.string.daejeon));
        itemList.add(getContext().getString(R.string.gyeonggi));
        itemList.add(getContext().getString(R.string.busan));
        itemList.add(getContext().getString(R.string.incheon));
        itemList.add(getContext().getString(R.string.sejong));
        itemList.add(getContext().getString(R.string.gwangju));
        itemList.add(getContext().getString(R.string.daegu));
        itemList.add(getContext().getString(R.string.ulsan));
        itemList.add(getContext().getString(R.string.jeju));
        itemList.add(getContext().getString(R.string.wonju));

        final OnKeyListener mOnKeyListener = (v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_UP && !moveToFirstChar(event) && !HanKeyTable.isCursorKey(keyCode)) {
                HanBeep.playBeep(getContext(), HanBeep.TYPE_WARNING);
            }
            return false;
        };
        this.setAdapter(new HanStringArrayAdapter(getContext(), android.R.layout.simple_spinner_item, itemList));
        this.setMoveToFirstCharMode(true, mOnKeyListener);
    }

    @Override
    public boolean moveToFirstChar(KeyEvent event) {
        int keyCode = event.getScanCode();

        /* Change usb keyboard to braille keyboard */
        if (event.getDeviceId() != -1) {
            keyCode = HimsCommonFunc.convertUSBtoBraille(getContext(), event);
        }

        /* Check language key */
        if (this.isLanguageKey(keyCode)) {
            return true;
        }

        boolean bASCIIMode = true;
        char charValue = HanKeyTable.getCharValue(getContext(), keyCode);

        if (charValue == 0) {
            return false;
        }

        int iSize = this.getAdapter().getCount();
        if (iSize > 1) {
            int iSavePosition = this.getSelectedItemPosition();
            int iCurrentPosition = iSavePosition;
            String sFirstChar = "";
            while (true) {
                iCurrentPosition++;
                if (iCurrentPosition == iSize) {
                    iCurrentPosition = 0;
                }
                if (iSavePosition == iCurrentPosition) {
                    break;
                }

                sFirstChar = (String) this.getAdapter().getItem(iCurrentPosition);
                sFirstChar = sFirstChar.toLowerCase();

                /* Check first key value */
                if (HanEditTextUtil.checkFirstCharacter(getContext(), sFirstChar, bASCIIMode, charValue, this.getBrailleTranslator())) {
                    this.setSelection(iCurrentPosition);
                    return true;
                }
            }
        }
        return false;
    }
}
