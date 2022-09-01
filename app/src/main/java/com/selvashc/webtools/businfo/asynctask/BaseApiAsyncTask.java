package com.selvashc.webtools.businfo.asynctask;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.os.AsyncTask;

import com.selvashc.webtools.businfo.R;
import com.selvashc.webtools.businfo.data.RegionId;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * 모든 AsyncTask 에서 사용하는 resources, serviceKey 등을 저장하고 있고, 공통적으로 실행될 API 검색 함수를 정의하였음
 */
public abstract class BaseApiAsyncTask extends AsyncTask<Void, Integer, Void> {

    static final int BUS_SEARCH = 1;
    static final int SUBWAY_SEARCH = 2;

    @SuppressLint("StaticFieldLeak")
    private Resources r;
    private String serviceKey;
    private boolean isError = false;
    private int regionId;

    Resources getR() { return r; }
    int getRegionId() { return regionId; }
    boolean isError() { return isError; }

    BaseApiAsyncTask(Resources resources, int rId) {
        r = resources;
        regionId = rId;
        serviceKey = r.getString(R.string.API_KEY);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        return null;
    }

    // API 에서 필요한 정보를 검색 후 저장
    void apiSearch(String startURL, String endURL, int searchType) {
        StringBuilder urlBuilder = new StringBuilder(startURL); /*URL*/
        URL url = null;
        try {
            if (searchType == BUS_SEARCH) {
                urlBuilder.append(r.getString(R.string.question_mark) + URLEncoder.encode(r.getString(R.string.serviceKey), r.getString(R.string.utf8)) + r.getString(R.string.equal) + serviceKey); /*Service Key*/
            }
            urlBuilder.append(endURL);
            url = new URL(urlBuilder.toString());
        } catch (UnsupportedEncodingException | MalformedURLException e) {
            e.getCause();
        }

        if (url == null)
            return;

        try (InputStream is = url.openStream()) {
            XmlPullParserFactory xmlPullParserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = xmlPullParserFactory.newPullParser();

            parser.setInput(new InputStreamReader(is, r.getString(R.string.utf8)));
            String tagName = "";

            int eventType = parser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    tagName = parser.getName();
                } else if (eventType == XmlPullParser.TEXT) {
                    if (regionId == RegionId.getInstance().getDaejeonId() || regionId == RegionId.getInstance().getIncheonId()
                            || regionId == RegionId.getInstance().getDaeguId() || regionId == RegionId.getInstance().getGwangjuId()
                            || regionId == RegionId.getInstance().getSejongId() || regionId == RegionId.getInstance().getUlsanId()
                            || regionId == RegionId.getInstance().getJejuId() || regionId == RegionId.getInstance().getWonjuId()) {
                        compareAndStore(tagName, parser.getText());
                    } else if (regionId == RegionId.getInstance().getBusanId()) {
                        compareAndStoreBusan(tagName, parser.getText());
                    } else {
                        compareAndStoreGyeonggi(tagName, parser.getText());
                    }
                }
                eventType = parser.next();
            }
        } catch(IOException | XmlPullParserException e) {
            isError = true;
            e.getCause();
        }
    }

    // API마다 사용하는 tagName과 필요로 하는 text가 다르기 때문에 child class에서 override 할 수 있도록 하였음
    public void compareAndStore(String tagName, String text) {
        // child class에서 override하여 사용 (경기, 부산 지역 외 검색 시 tag목록에 따라 text 저장)
    }

    public void compareAndStoreGyeonggi(String tagName, String text) {
        // child class에서 override하여 사용 (경기 지역)
    }

    public void compareAndStoreBusan(String tagName, String text) {
        // child class에서 override하여 사용 (부산 지역)
    }
}
