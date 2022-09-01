package com.selvashc.webtools.businfo.view.activity;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.ViewModelStore;
import android.arch.lifecycle.ViewModelStoreOwner;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.KeyEvent;

import com.jawon.han.HanActivity;
import com.jawon.han.key.HanBrailleKey;
import com.jawon.han.key.keyboard.usb.USB2Braille;
import com.jawon.han.util.HimsCommonFunc;
import com.jawon.han.widget.HanApplication;
import com.jawon.han.widget.HanMenuPopup;
import com.jawon.han.widget.HanProgressDialog;
import com.selvashc.webtools.businfo.R;

/**
 * 모든 Activity 에서 사용하는 PopupMenu, DispatchKey, MVVM 을 사용하기 위한 Owner, Lifecycle 등을 정의
 */
public abstract class BaseActivity extends HanActivity implements LifecycleOwner, ViewModelStoreOwner, HanMenuPopup.OnMenuItemClickListener, HanMenuPopup.OnDismissListener {

    private final LifecycleRegistry lifecycleRegistry = new LifecycleRegistry(this);
    private ViewModelStore viewModelStore;

    private HanMenuPopup hanMenuPopup;
    private boolean isShowPopup = false;
    private boolean isShowPopupCancel = false;

    private HanProgressDialog hanProgressDialog;

    protected HanMenuPopup getHanMenuPopup() { return hanMenuPopup; }
    protected HanProgressDialog getHanProgressDialog() { return hanProgressDialog; }
    protected void setIsShowPopupCancel(boolean popupCancel) { isShowPopupCancel = popupCancel; }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.lifecycleRegistry.markState(Lifecycle.State.CREATED);
        hanMenuPopup = new HanMenuPopup(this);
        hanMenuPopup.setOnDismissListener(this);
        hanMenuPopup.setOnMenuItemClickListener(this);
        hanProgressDialog = new HanProgressDialog(this);
        hanProgressDialog.setMessage(getString(R.string.searching));
        hanProgressDialog.setType(HanProgressDialog.TYPE_PROGRESS_BEEP_AND_BRAILLE);
    }

    @Override
    protected void onStart() {
        this.lifecycleRegistry.markState(Lifecycle.State.STARTED);
        super.onStart();
    }

    @Override
    protected void onResume() {
        this.lifecycleRegistry.markState(Lifecycle.State.RESUMED);
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        this.lifecycleRegistry.markState(Lifecycle.State.DESTROYED);
        super.onDestroy();
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return lifecycleRegistry;
    }

    @NonNull
    @Override
    public ViewModelStore getViewModelStore() {
        if (getApplication() == null) {
            throw new IllegalStateException(getString(R.string.IllegalStateException));
        }
        ensureViewModelStore();
        return viewModelStore;
    }

    void ensureViewModelStore() {
        if (viewModelStore == null) {
            NonConfigurationInstances nc =
                    (NonConfigurationInstances) getLastNonConfigurationInstance();
            if (nc != null) {
                // Restore the ViewModelStore from NonConfigurationInstances
                viewModelStore = nc.viewModelStore;
            }
            if (viewModelStore == null) {
                viewModelStore = new ViewModelStore();
            }
        }
    }

    static final class NonConfigurationInstances {
        ViewModelStore viewModelStore;
    }

    // 메유 팝업 종료
    @Override
    public void onDismiss(HanMenuPopup menu) {
        isShowPopup = false;
        if (isShowPopupCancel)
            HanApplication.getInstance(this).getHanDevice().displayAndPlayTTS(getApplicationContext().getString(R.string.COMMON_MSG_CANCEL), true);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        final int scanCode = USB2Braille.getInstance().convertUSBtoBraille(this, event);

        // Menu popup 을 불러올 경우
        if (event.getAction() == KeyEvent.ACTION_UP && (HimsCommonFunc.isMenuKey(event.getKeyCode()) ||
                (scanCode == (HanBrailleKey.HK_M | HanBrailleKey.HK_SPACE)))) {
            if (!isShowPopup) {
                isShowPopup = true;
                isShowPopupCancel = true;
                hanMenuPopup.show();
            }
            return true;
        }
        if (event.getAction() == KeyEvent.ACTION_UP && HimsCommonFunc.isExitKey(event.getScanCode(), event.getKeyCode())) {
            finish();
            return true;
        }

        return super.dispatchKeyEvent(event);
    }

    // 인터넷 연결 확인
    protected boolean isWifiOn() {
        final int startDelayMills = 1000;

        if (!HimsCommonFunc.isWifiOn(this) && !HimsCommonFunc.isInternetConnected(this)) {
            // handler 를 사용하지 않으면 tts 출력이 안됨
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                HanApplication.getInstance(this).getHanDevice().displayAndPlayTTS(getString(R.string.plz_link_network), true);
                finish();
            }, startDelayMills);
            return false;
        } else {
            return true;
        }
    }
}