package com.android.rssreader.network;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ImageView;

import com.android.rssreader.R;
import com.android.rssreader.data.RssContract;
import com.android.rssreader.data.RssContract.RSSEntry;
import com.android.rssreader.parser.RssXmlParser;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by mariam on 08.04.2015.
 */
public class DownloadRss extends IntentService{

    public static final String RSS_URL = "RSS_URL";
    public static final String KEY_OUT = "OUT_RESULTS";
    private static final String LOAD_RESULT = "success";
    public static final String ACTION_INTENTSERVICE = "com.android.rssreader.RESPONSE";
    private String url;
    private String load_result=null;
    private ArrayList<ContentValues> rss_histoty = new ArrayList<>();
    private ArrayList<RssXmlParser.Item> result_list = null;

    public DownloadRss() {
        super("RecognizeText");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ContentValues newRssValues;
        url = intent.getExtras().getString(RSS_URL);
        try {
             load_result = loadXmlFromNetwork(url);
        } catch (IOException e) {
            Log.e("error", e.getMessage());
        } catch (XmlPullParserException e) {
            Log.e("error", e.getMessage());
        }
        Intent intentResponse = new Intent();
        intentResponse.setAction(ACTION_INTENTSERVICE);
        intentResponse.addCategory(Intent.CATEGORY_DEFAULT);
        intentResponse.putParcelableArrayListExtra(KEY_OUT,result_list);
        sendBroadcast(intentResponse);
    }

    private String loadXmlFromNetwork(String urlString) throws XmlPullParserException, IOException {
        InputStream stream = null;
        RssXmlParser rssXmlParser = new RssXmlParser();
        ArrayList<RssXmlParser.Item> entries = null;
        String title = null;
        String url = null;
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean pref = sharedPrefs.getBoolean("summaryPref", false);

        try {
            stream = downloadUrl(urlString);
            entries = rssXmlParser.parse(stream);
        } finally {
            if (stream != null) {
                result_list=entries;
                Log.d("entries",String.valueOf(entries.size()));
                stream.close();
            }
        }
        return LOAD_RESULT;
    }

    private InputStream downloadUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        conn.connect();
        return conn.getInputStream();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }
}
