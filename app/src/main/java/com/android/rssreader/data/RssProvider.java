package com.android.rssreader.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.android.rssreader.data.RssContract.RSSEntry;
import android.net.Uri;

/**
 * Created by mariam on 09.04.2015.
 */
public class RssProvider extends ContentProvider {

    private static final int RSS_TYPE = 100;
    private RssDbHelper mOpenHelper;
    private static final UriMatcher mUriMatcher = buildUriMatcher();

    static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(RssContract.CONTENT_AUTHORITY,RssContract.PATH_RSS, RSS_TYPE);
        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new RssDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor result;
        if(mUriMatcher.match(uri) == RSS_TYPE){
            result = mOpenHelper.getReadableDatabase().query(RssContract.RSSEntry.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder);
        }else{
            throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        return result;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = mUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case RSS_TYPE: {
                long _id = db.insert(RSSEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = RSSEntry.buildUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        db.close();
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = mUriMatcher.match(uri);
        int id;

        switch (match) {
            case RSS_TYPE: {
                id = db.update(RSSEntry.TABLE_NAME, values,selection,selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if(id != 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return id;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = mUriMatcher.match(uri);
        switch (match) {
            case RSS_TYPE:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {

                        long _id = db.insert(RSSEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }
}
