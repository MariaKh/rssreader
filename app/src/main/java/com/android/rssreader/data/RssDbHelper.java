package com.android.rssreader.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.android.rssreader.data.RssContract.RSSEntry;

/**
 * Created by mariam on 09.04.2015.
 */
public class RssDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    static final String DATABASE_NAME = "rssreader.db";

    public RssDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_RSS_TABLE = "CREATE TABLE " + RSSEntry.TABLE_NAME + " (" +
                RSSEntry._ID + " INTEGER PRIMARY KEY," +
                RSSEntry.COLUMN_TITLE + " TEXT, " +
                RSSEntry.COLUMN_CATEGORY + " TEXT, " +
                RSSEntry.COLUMN_IMAGE + " TEXT, " +
                RSSEntry.COLUMN_LINK + " TEXT, " +
                "UNIQUE (" + RSSEntry.COLUMN_CATEGORY +" , "+ RSSEntry.COLUMN_TITLE
                +" , "+ RSSEntry.COLUMN_LINK+" , "+ RSSEntry.COLUMN_IMAGE
                +") ON CONFLICT IGNORE"+
                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_RSS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + RSSEntry.TABLE_NAME);
    }
}
