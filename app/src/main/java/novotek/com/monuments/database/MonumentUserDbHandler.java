
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
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;


import com.orhanobut.logger.Logger;


import novotek.com.monuments.model.MonumentUser;

public class MonumentUserDbHandler {
    final static String TAG = MonumentUserDbHandler.class.getSimpleName();
    SQLiteDatabase db;

    private static final String TABLE_NAME = "user";

    // Columns
    private static final String KEY_ID = "Id";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "pass";


    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_NAME + " TEXT,"
            + KEY_EMAIL + " TEXT,"
            + KEY_PASSWORD + " TEXT"
            + ");";

    public static final String[] FULL_PROJECTION = {
            KEY_ID,
            KEY_NAME,
            KEY_EMAIL,
            KEY_PASSWORD
    };

    public static final String[] SIMPLE_PROJECTION = {
            KEY_NAME,
            KEY_EMAIL,
            KEY_PASSWORD
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

    public MonumentUser save(MonumentUser user) {
        if (user != null) {
            ContentValues values = new ContentValues();
            boolean isUpdate = false;
            if (user.getUserId() != null) {
                isUpdate = true;
                values.put(KEY_ID, user.getUserId());
            }
            values.put(KEY_NAME, user.getUserName());
            values.put(KEY_EMAIL, user.getEmail());
            values.put(KEY_PASSWORD, user.getPassword());
            db = DatabaseHandler.instance.GetWritableDataBase();
            if (!isUpdate) {
                long userId = db.insert(TABLE_NAME, null, values);
                user.setUserId(userId);
            } else
                db.update(TABLE_NAME, values, KEY_ID + "=" + user.getUserId(), null);
        }
        return user;
    }

    public MonumentUser create(MonumentUser user) {
        if (getByEmail(user.getEmail()) != null) {
            Logger.e("Tried saving as existing user by email !");
            return null; // existing user !
        } else {
            return save(user);
        }
    }

    public MonumentUser getFromCursor(Cursor cursor) {
        MonumentUser user = new MonumentUser();
        try {
            user.setUserId(cursor.getLong(cursor.getColumnIndex(KEY_ID)));
            user.setEmail(cursor.getString(cursor.getColumnIndex(KEY_EMAIL)));
            user.setPassword(cursor.getString(cursor.getColumnIndex(KEY_PASSWORD)));
            user.setUserName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
        } catch (Exception e) {
            Logger.e(" MonumentUserDbHandler Exception " + e.getMessage());
        }

        return user;
    }

    public Pair<MonumentUser, Integer> checkUser(String email, String pass) {
        MonumentUser user = getByEmail(email);
        if (user != null) {
            if (user.getPassword().equals(pass)) {
                return new Pair<>(user, 0);
            } else
                return new Pair<>(user, -1);
        } else
            return new Pair<>(null, -1);
    }

    @Nullable
    public MonumentUser get(long userId) {
        MonumentUser user = null;
        db = DatabaseHandler.instance.GetReadableDataBase();
        String getQuery;
        getQuery = "SELECT * FROM " + TABLE_NAME +
                " WHERE " + KEY_ID + "=" + userId + "";

        Cursor cursor = db.rawQuery(getQuery, null);
        try {
            if ((cursor != null) && (cursor.getCount() > 0)) {
                cursor.moveToFirst();
                user = getFromCursor(cursor);
            }
        } catch(Exception e) {
            Logger.e("[get] Exception ");
        } finally {
            cursor.close();
        }

        return user;
    }


    @Nullable
    public MonumentUser getByEmail(String email) {
        MonumentUser user = null;
        db = DatabaseHandler.instance.GetReadableDataBase();
        String getQuery;
        getQuery = "SELECT * FROM " + TABLE_NAME +
                " WHERE " + KEY_EMAIL + "=\"" + email + "\"";

        Cursor cursor = db.rawQuery(getQuery, null);
        try {
            if ((cursor != null) && (cursor.getCount() > 0)) {
                cursor.moveToFirst();
                user = getFromCursor(cursor);
            }
        } catch(Exception e) {
            Logger.e("[getByEmail] Exception ");
        } finally {
            cursor.close();
        }

        return user;
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
