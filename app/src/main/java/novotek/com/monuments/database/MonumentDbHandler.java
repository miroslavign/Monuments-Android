
/*
 * MonumentUserDbHandler.java
 * Heyandroid
 *
 * Created by Miroslav Ignjatovic on 2/17/2016
 * Copyright (c) 2015 CommonSun All rights reserved.
 */

package novotek.com.monuments.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;

import com.orhanobut.logger.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import novotek.com.monuments.Monuments;
import novotek.com.monuments.model.Monument;
import novotek.com.monuments.model.MonumentType;
import novotek.com.monuments.model.MonumentUser;

public class MonumentDbHandler {
    final static String TAG = MonumentDbHandler.class.getSimpleName();
    SQLiteDatabase db;

    private static final String TABLE_NAME = "monument";

    // Columns
    private static final String KEY_ID = "Id";
    private static final String KEY_NAME = "name";
    private static final String KEY_TYPE = "type";
    private static final String KEY_DESCRIPTION = "desc";
    private static final String KEY_URI = "uri";
    private static final String KEY_USER_ID = "user";
    private static final String KEY_TIME = "timed";


    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
            + KEY_ID + " TEXT PRIMARY KEY,"
            + KEY_NAME + " TEXT,"
            + KEY_TYPE + " INTEGER,"
            + KEY_URI + " TEXT,"
            + KEY_DESCRIPTION + " TEXT,"
            + KEY_USER_ID + " INTEGER,"
            + KEY_TIME + " INTEGER"
            + ");";

    public static final String[] FULL_PROJECTION = {
            KEY_ID,
            KEY_NAME,
            KEY_TYPE,
            KEY_URI,
            KEY_DESCRIPTION,
            KEY_USER_ID,
            KEY_TIME
    };

    public static final String[] SIMPLE_PROJECTION = {
            KEY_NAME,
            KEY_TYPE,
            KEY_URI,
            KEY_DESCRIPTION
    };

    // Creating Tables and indexes
    public void onCreate(SQLiteDatabase dbase) {
        db = dbase;
        db.execSQL(CREATE_TABLE);
    }

    // Upgrading database
    public void onUpgrade(SQLiteDatabase dbase, int oldVersion, int newVersion) {
        db = dbase;
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    public Monument save(Monument monument) {
        if (monument != null) {
            ContentValues values = new ContentValues();
            values.put(KEY_ID, monument.getUuid());
            values.put(KEY_NAME, monument.getName());
            values.put(KEY_TYPE, monument.getMonumentType());
            values.put(KEY_URI, monument.getMonumentUri().toString());
            values.put(KEY_DESCRIPTION, monument.getDescription());
            values.put(KEY_USER_ID, monument.getUserAddedId());
            Calendar cal = Calendar.getInstance();
            values.put(KEY_TIME, cal.getTimeInMillis());
            db = DatabaseHandler.instance.GetWritableDataBase();
            db.insert(TABLE_NAME, null, values);
        }
        return monument;
    }

    public Monument getFromCursor(Cursor cursor) {
        Monument monument = new Monument();
        try {
            String uuid = cursor.getString(cursor.getColumnIndex(KEY_ID));
            String name = cursor.getString(cursor.getColumnIndex(KEY_NAME));
            String description = cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION));
            String uriString = cursor.getString(cursor.getColumnIndex(KEY_URI));
            monument = new Monument(uuid, name, description, Uri.parse(uriString));
            monument.setUpdated(cursor.getLong(cursor.getColumnIndex(KEY_TIME)));
            Long userId = cursor.getLong(cursor.getColumnIndex(KEY_USER_ID));
            if (userId != null) {
                MonumentUserDbHandler monumentUserDbHandler = Monuments.instance.getDb().userDbHandler;
                MonumentUser user = monumentUserDbHandler.get(userId);
                if (user != null)
                    monument.setUser(user);
            }

            Long typeId = cursor.getLong(cursor.getColumnIndex(KEY_TYPE));
            if (typeId != null) {
                MonumentTypeDbHandler monumentTypeDbHandler = Monuments.instance.getDb().monumentTypeDbHandler;
                MonumentType monumentType = monumentTypeDbHandler.get(typeId);
                if (monumentType != null)
                    monument.setMonumentType(monumentType);
            }
        } catch (Exception e) {
            Logger.e(" MonumentDbHandler Exception " + e.getMessage());
        }

        return monument;
    }

    @Nullable
    public List<Monument> getByUserId(Long userId) {
        List<Monument> monumentList = null;
        db = DatabaseHandler.instance.GetReadableDataBase();
        String getQuery;
        if (userId == null)
            getQuery = "SELECT * FROM " + TABLE_NAME +
                    " ORDER BY " + KEY_TIME + " ASC";
        else
            getQuery = "SELECT * FROM " + TABLE_NAME +
                    " WHERE " + KEY_USER_ID + "=" + userId +
                    " ORDER BY " + KEY_TIME + " ASC";

        Cursor cursor = db.rawQuery(getQuery, null);
        try {
            if ((cursor != null) && (cursor.getCount() > 0)) {
                monumentList = new ArrayList<>(cursor.getCount());
                while (cursor.moveToNext()) {
                    Monument monument = getFromCursor(cursor);
                    monumentList.add(monument);
                }
            }
        } catch(Exception e) {
            Logger.e("[getByUserId] Exception ");
        } finally {
            cursor.close();
        }

        return monumentList;
    }


    public void removeAll(){
        db = DatabaseHandler.instance.GetWritableDataBase();
        String removeAllQuery = "DELETE FROM " + TABLE_NAME;
        db.execSQL(removeAllQuery);
    }

    public void dropTable() {
        db = DatabaseHandler.instance.GetWritableDataBase();
        String removeAllQuery = "DROP TABLE " + TABLE_NAME;
        db.execSQL(removeAllQuery);
    }


}
