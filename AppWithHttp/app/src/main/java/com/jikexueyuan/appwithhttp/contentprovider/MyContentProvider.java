package com.jikexueyuan.appwithhttp.contentprovider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * DataBase control
 */
public class MyContentProvider extends ContentProvider {


    public static final Uri url = Uri.parse("content://com.jikexueyuan.cp");

    private SQLiteDatabase mDataBase;

    public MyContentProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        mDataBase.delete("HttpData", selection, selectionArgs);
        return 1;
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        long id = mDataBase.insert("HttpData", null, values);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public boolean onCreate() {

        mDataBase = getContext().openOrCreateDatabase("HttpDataBase", Context.MODE_PRIVATE, null);
        mDataBase.execSQL("CREATE TABLE IF NOT EXISTS HttpData(" +
                "_id INTEGER primary key autoincrement, " +
                "time text not null," +
                "title text not null," +
                "content text " +
                ")");
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        //return mDataBase.query("HttpData", new String[]{"_id", "time","title", "content"}, null, null, null, null, "time DESC");
        return mDataBase.query("HttpData", projection, null, null, null, null, "time DESC");
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
