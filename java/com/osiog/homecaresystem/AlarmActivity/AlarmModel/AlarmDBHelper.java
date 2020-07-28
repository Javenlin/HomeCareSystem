package com.osiog.homecaresystem.AlarmActivity.AlarmModel;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by iii on 2018/1/12.
 */

        public class AlarmDBHelper extends SQLiteOpenHelper {
            public AlarmDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
                super(context, name, factory, version);
            }

            @Override
            public void onCreate(SQLiteDatabase db) {
                db.execSQL("CREATE  TABLE TableAlarm " +
                        "(_id INTEGER PRIMARY KEY NOT NULL , " +
                        "name VARCHAR ,"+
                        "time DATETIME NOT NULL , " +
                        "days INTEGER , " +
                        "weeks INTEGER ,"+
                        "pic VARCHAR)");
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}