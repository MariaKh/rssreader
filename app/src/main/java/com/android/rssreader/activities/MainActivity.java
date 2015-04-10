package com.android.rssreader.activities;

import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.android.rssreader.R;
import com.android.rssreader.data.RssContract.RSSEntry;
import com.android.rssreader.network.DownloadRss;
import com.android.rssreader.parser.RssXmlParser;
import com.android.rssreader.tools.ImageLoader;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends ActionBarActivity implements  LoaderManager.LoaderCallbacks<Cursor> {

    public static final String WIFI = "Wi-Fi";
    public static final String ANY = "Any";
    private static final String URL = "http://feeds.abcnews.com/abcnews/topstories";
    public static final String APP_PREFERENCES = "time_controller";
    public static final String APP_PREFERENCES_TIME = "time";
    private static final String ERROR_CONNECTION = "Check WiFi connection";
    private static final long day_millis = 86400000;
    private SimpleCursorAdapter mAdapter;
    private ListView rss_list;
    private RssBroadcastReceiver rssBroadcastReceiver;
    SharedPreferences updateSettings;
    private Calendar calendar;
    private long current_time;


        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
           updateRssList();
            rss_list = (ListView)findViewById(R.id.list_rss);
            mAdapter = new ItemSimpleCursorAdapter(this,
                    R.layout.list_item_rss, null,
                    new String[]{RSSEntry.COLUMN_IMAGE, RSSEntry.COLUMN_TITLE,RSSEntry.COLUMN_CATEGORY},
                    new int[]{R.id.image, R.id.title, R.id.category}, 0);
            rss_list.setAdapter(mAdapter);
            rss_list.setOnItemClickListener(
                    new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> list, View arg1, int position, long arg3) {
                            Cursor c = ((SimpleCursorAdapter) list.getAdapter()).getCursor();
                            c.moveToPosition(position);
                            Intent browse = new Intent(Intent.ACTION_VIEW, Uri.parse(c.getString
                                    (c.getColumnIndex(RSSEntry.COLUMN_LINK))));
                            startActivity(browse);
                        }
                    }
            );
            getLoaderManager().initLoader(0, null, this);
    }

    private void updateRssList(){
        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        current_time = calendar.getTimeInMillis();
        updateSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        if(updateSettings.getLong(APP_PREFERENCES_TIME,0)>0){
            if ((current_time-updateSettings.getLong(APP_PREFERENCES_TIME,0))>=day_millis){
                startService();
                SharedPreferences.Editor editor = updateSettings.edit();
                editor.putLong(APP_PREFERENCES_TIME, current_time);
                editor.apply();
            }
        }
        else{
            SharedPreferences.Editor editor = updateSettings.edit();
            editor.putLong(APP_PREFERENCES_TIME, current_time);
            editor.apply();
            startService();
        }
    }

    private void startService(){
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (mWifi.isConnected()) {
            Intent intentService = new Intent(this, DownloadRss.class);
            startService(intentService.putExtra(DownloadRss.RSS_URL, URL));
            rssBroadcastReceiver = new RssBroadcastReceiver();
            IntentFilter intentFilter = new IntentFilter(
                    DownloadRss.ACTION_INTENTSERVICE);
            intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
            registerReceiver(rssBroadcastReceiver, intentFilter);
        }
        else{
            Toast.makeText(this,ERROR_CONNECTION,Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(rssBroadcastReceiver);
    }

    public class RssBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<RssXmlParser.Item> result = intent.getParcelableArrayListExtra(DownloadRss.KEY_OUT);
            ArrayList<ContentValues> rss_history = new ArrayList<>();
                       for (RssXmlParser.Item item:result) {
                           ContentValues newRssValues = new ContentValues();
                           newRssValues.put(RSSEntry.COLUMN_TITLE, item.title);
                           newRssValues.put(RSSEntry.COLUMN_CATEGORY, item.category);
                           newRssValues.put(RSSEntry.COLUMN_IMAGE, item.image);
                           newRssValues.put(RSSEntry.COLUMN_LINK, item.link);
                           rss_history.add(newRssValues);

            }
            getApplicationContext().getContentResolver().bulkInsert(RSSEntry.CONTENT_URI,rss_history.toArray(new ContentValues[rss_history.size()]));
            mAdapter.getCursor().requery();
            mAdapter.notifyDataSetChanged();
        }
    }

    private class ItemSimpleCursorAdapter extends SimpleCursorAdapter {
        public ItemSimpleCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
            super(context, layout, c, from, to, flags);
        }

        @Override
        public void setViewImage(ImageView v,String value) {

            if(v.getId()==R.id.image){
            //image set background
            }
            super.setViewImage(v, value);

        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,RSSEntry.CONTENT_URI,
                new String[]{RSSEntry.TABLE_NAME + "." + RSSEntry._ID,
                        RSSEntry.COLUMN_IMAGE,RSSEntry.COLUMN_TITLE,RSSEntry.COLUMN_CATEGORY,RSSEntry.COLUMN_LINK}
                ,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}
