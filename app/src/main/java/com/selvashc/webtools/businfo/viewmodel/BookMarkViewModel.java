package com.selvashc.webtools.businfo.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.content.Context;

import com.selvashc.webtools.businfo.data.BookMarkInfo;
import com.selvashc.webtools.businfo.model.BookMarkModel;

import java.util.List;

/**
 * View 의 이벤트에 따라 BookMarkModel 이 데이터를 반환/저장 하도록 통신하는 ViewModel
 * 즐겨 찾기 목록 읽기, 즐겨 찾기 삭제 동작 요청
 */
public class BookMarkViewModel {

    // Model
    private BookMarkModel model;

    // LiveData
    private MutableLiveData<List<BookMarkInfo>> bookMarkList = new MutableLiveData<>();

    public BookMarkViewModel(Context context) {
        model = new BookMarkModel();
        model.readBookMarkList(context);
        bookMarkList.setValue(model.getBookMarkList());
    }

    public MutableLiveData<List<BookMarkInfo>> getBookMarkList() { return bookMarkList; }

    // 즐겨 찾기 목록 읽기
    public void readBookMark(Context context) {
        model.readBookMarkList(context);
        bookMarkList.setValue(model.getBookMarkList());
    }

    // 즐겨 찾기 삭제
    public void deleteBookMark(Context c, int position) {
        model.deleteBookMarkList(c, position);
        bookMarkList.setValue(model.getBookMarkList());
    }
}
