package com.selvashc.webtools.businfo.data;

/**
 * 즐겨 찾기 정보 (이름, ID)를 저장
 */
public class BookMarkInfo {
    private String bookMarkName;
    private String bookMarkId;

    public BookMarkInfo(String name, String id) {
        bookMarkName = name;
        bookMarkId = id;
    }

    public BookMarkInfo(String name) {
        bookMarkName = name;
        bookMarkId = "";
    }

    public String getBookMarkName() { return bookMarkName; }
    public String getBookMarkId() { return bookMarkId; }
}
