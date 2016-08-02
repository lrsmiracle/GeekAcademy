package com.jikexueyuan.note.contentProvider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class MyContentProvider extends ContentProvider {


    public static final Uri url = Uri.parse("content://com.jikexueyuan.cp");
    private SQLiteDatabase mDataBase;


    public MyContentProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        mDataBase.delete("Note", selection, selectionArgs);
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

        long id = mDataBase.insert("Note", null, values);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public boolean onCreate() {

        mDataBase = getContext().openOrCreateDatabase("NoteDB", Context.MODE_PRIVATE, null);
        mDataBase.execSQL("CREATE TABLE IF NOT EXISTS Note(" +
                "_id INTEGER primary key autoincrement, " +
                "time text not null," +
                "context text " +
                ")");
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        return mDataBase.query("Note", new String[]{"_id", "time", "context"}, null, null, null, null, "time ASC");
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
