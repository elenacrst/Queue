package com.example.elena.queue.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

/**
 * Created by Elena on 9/13/2017.
 */

public class ShoppingDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "shopping.db";
    private static final int DATABASE_VERSION = 1;

    public ShoppingDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_TABLE = "create table "+ShoppingContract.ShoppingEntry.TABLE_NAME+ " ("+
                ShoppingContract.ShoppingEntry._ID + " integer primary key autoincrement, "+
                ShoppingContract.ShoppingEntry.COLUMN_PRODUCT_NAME+" text not null);";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists "+ ShoppingContract.ShoppingEntry.TABLE_NAME+";");
        onCreate(db);
    }
}
