package com.selvashc.webtools.businfo.model;

import android.content.Context;

import com.selvashc.webtools.businfo.R;
import com.selvashc.webtools.businfo.data.BookMarkInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * 즐겨 찾기 목록을 읽은 후 ViewModel에 값을 셋팅(반환)
 *  - 즐겨 찾기 목록을 파일에서 읽어온 후 값을 반환
 *  - 즐겨 찾기를 삭제하고, 삭제하고 난 후 결과값을 읽은 후 값을 반환
 */
public class BookMarkModel {

    // 즐겨찾기 정보와 버스 정보를 임시로 저장할 변수 목록
    private List<BookMarkInfo> bookMarkList = new ArrayList<>();

    public BookMarkModel() {
        // 필요한 context 는 method 에서 인자로 받음
    }

    public List<BookMarkInfo> getBookMarkList() { return bookMarkList; }

    // 즐겨 찾기 목록 읽기
    public void readBookMarkList(Context context) {
        bookMarkList.clear();
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(context.openFileInput(context.getString(R.string.file_name_bookmark))))) {
            String temp = "";
            while ((temp = bufferedReader.readLine()) != null) {
                String[] tmp = temp.split(context.getString(R.string.greater_than_sign));
                if (tmp.length >= 2) {
                    bookMarkList.add(new BookMarkInfo(tmp[0], tmp[1]));
                } else {
                    bookMarkList.add(new BookMarkInfo(tmp[0]));
                }
            }
        } catch (IOException e) {
            e.getCause();
        }
    }

    // 즐겨 찾기 삭제 후 목록 읽기
    public void deleteBookMarkList(Context context, int position) {
        StringBuilder tmp = new StringBuilder();
        for (int i = 0; i < this.bookMarkList.size(); i++) {
            if (i == position)
                continue;
            String s = bookMarkList.get(i).getBookMarkName() + context.getString(R.string.greater_than_sign) + bookMarkList.get(i).getBookMarkId();
            s += context.getString(R.string.next_lign);
            tmp.append(s);
        }

        try (OutputStreamWriter oStreamWriter = new OutputStreamWriter(context.openFileOutput(context.getString(R.string.file_name_bookmark), Context.MODE_PRIVATE))) {
            oStreamWriter.write(tmp.toString());
        } catch (IOException e) {
            e.getCause();
        }

        readBookMarkList(context);
    }
}
