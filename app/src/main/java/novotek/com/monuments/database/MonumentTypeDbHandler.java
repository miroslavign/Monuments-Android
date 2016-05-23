
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
import android.text.TextUtils;
import android.widget.ListView;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import novotek.com.monuments.model.MonumentType;
import novotek.com.monuments.model.MonumentUser;

public class MonumentTypeDbHandler {
    final static String TAG = MonumentTypeDbHandler.class.getSimpleName();
    SQLiteDatabase db;

    private static final String TABLE_NAME = "monutype";

    // Columns
    private static final String KEY_ID = "Id";
    private static final String KEY_NAME = "name";

    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_NAME + " TEXT"
            + ");";

    public static final String[] FULL_PROJECTION = {
            KEY_ID,
            KEY_NAME
    };

    public static final String[] SIMPLE_PROJECTION = {
            KEY_NAME
    };

    private static final String[] CREATE_DEMO_DATA =
            {"INSERT INTO monutype (\"name\") VALUES(\"History\") ",
                    "INSERT INTO monutype (\"name\") VALUES(\"Social\") ",
            };


    // Creating Tables and indexes
    public void onCreate(SQLiteDatabase dbase) {
        db = dbase;
        db.execSQL(CREATE_TABLE);
        for (String sql: CREATE_DEMO_DATA) {
            db.execSQL(sql);
        }
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

    public MonumentType save(MonumentType type) {
        if (type != null) {
            ContentValues values = new ContentValues();
            boolean isUpdate = false;
            if (type.id != null) {
                values.put(KEY_ID, type.id);
                isUpdate = true;
            }

            values.put(KEY_NAME, type.typeName);
            db = DatabaseHandler.instance.GetWritableDataBase();
            if (!isUpdate) {
                long typeId = db.insert(TABLE_NAME, null, values);
                type.id = typeId;
            } else {
                String strFilter = KEY_ID + "=" + type.id;
                db.update(TABLE_NAME, values, strFilter, null);
            }
        }
        return type;
    }

    public MonumentType create(String typeName) {
        if (!TextUtils.isEmpty(typeName)) {
            MonumentType monumentType = getFromName(typeName);
            if (monumentType != null)
                return null;

            monumentType = new MonumentType(typeName);
            return save(monumentType);
        }
        return null;
    }

    public MonumentType getFromCursor(Cursor cursor) {
        MonumentType type = new MonumentType();
        try {
            type.id = cursor.getLong(cursor.getColumnIndex(KEY_ID));
            type.typeName = cursor.getString(cursor.getColumnIndex(KEY_NAME));
        } catch (Exception e) {
            Logger.e(" MonumentTypeDbHandler Exception " + e.getMessage());
        }

        return type;
    }

    @Nullable
    public MonumentType get(long typeId) {
        MonumentType type = null;
        db = DatabaseHandler.instance.GetReadableDataBase();
        String getQuery;
        getQuery = "SELECT * FROM " + TABLE_NAME +
                " WHERE " + KEY_ID + "=" + typeId + "";

        Cursor cursor = db.rawQuery(getQuery, null);
        try {
            if ((cursor != null) && (cursor.getCount() > 0)) {
                cursor.moveToFirst();
                type = getFromCursor(cursor);
            }
        } catch(Exception e) {
            Logger.e("[get] Exception ");
        } finally {
            cursor.close();
        }

        return type;
    }

    public MonumentType getFromName(String name) {
        MonumentType type = null;
        db = DatabaseHandler.instance.GetReadableDataBase();
        String getQuery;
        getQuery = "SELECT * FROM " + TABLE_NAME +
                " WHERE lower(" + KEY_NAME + ")=\"" + name.toLowerCase() + "\"";

        Cursor cursor = db.rawQuery(getQuery, null);
        try {
            if ((cursor != null) && (cursor.getCount() > 0)) {
                cursor.moveToFirst();
                type = getFromCursor(cursor);
            }
        } catch(Exception e) {
            Logger.e("[getFromName] Exception ");
        } finally {
            cursor.close();
        }

        return type;
    }

    public List<String> getAllTypes() {
        List<String> toReturn = null;
        db = DatabaseHandler.instance.GetReadableDataBase();
        String getQuery;
        getQuery = "SELECT * FROM " + TABLE_NAME
                + " ORDER BY " + KEY_NAME;

        Cursor cursor = db.rawQuery(getQuery, null);
        try {
            if ((cursor != null) && (cursor.getCount() > 0)) {
                toReturn = new ArrayList<>(cursor.getCount());
                while (cursor.moveToNext()) {
                    MonumentType monumentType = getFromCursor(cursor);
                    toReturn.add(monumentType.typeName);
                }
            }
        } catch(Exception e) {
            Logger.e("[getAllTypes] Exception ");
        } finally {
            cursor.close();
        }

        return toReturn;
    }


    public List<MonumentType> getAllTypesFull() {
        List<MonumentType> toReturn = null;
        db = DatabaseHandler.instance.GetReadableDataBase();
        String getQuery;
        getQuery = "SELECT * FROM " + TABLE_NAME
                + " ORDER BY " + KEY_NAME;

        Cursor cursor = db.rawQuery(getQuery, null);
        try {
            if ((cursor != null) && (cursor.getCount() > 0)) {
                toReturn = new ArrayList<>(cursor.getCount());
                while (cursor.moveToNext()) {
                    MonumentType monumentType = getFromCursor(cursor);
                    toReturn.add(monumentType);
                }
            }
        } catch(Exception e) {
            Logger.e("[getAllTypesFull] Exception ");
        } finally {
            cursor.close();
        }

        return toReturn;
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
