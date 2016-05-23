
/*
 * DatabaseHandler.java
 * Heyandroid
 *
 * Created by Miroslav Ignjatovic on 12/25/2015
 * Copyright (c) 2015 CommonSun All rights reserved.
 */

package novotek.com.monuments.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHandler extends SQLiteOpenHelper {
	private static final String TAG = DatabaseHandler.class.getSimpleName();

	// All Static variables
	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "heydatabase";

	public static DatabaseHandler instance;
	
	// all tables listed here, and code in separate classes
    public MonumentUserDbHandler userDbHandler;
	public MonumentDbHandler monumentDbHandler;
	public MonumentTypeDbHandler monumentTypeDbHandler;



	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);

		instance = this;
		userDbHandler = new MonumentUserDbHandler();
		monumentDbHandler = new MonumentDbHandler();
		monumentTypeDbHandler = new MonumentTypeDbHandler();
	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		userDbHandler.onCreate(db);
		monumentDbHandler.onCreate(db);
		monumentTypeDbHandler.onCreate(db);

		// all other tables
		// ...
        //EventBus.getDefault().postSticky(new DatabaseCreatedEvent());
	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		userDbHandler.onUpgrade(db, oldVersion, newVersion);
		monumentDbHandler.onUpgrade(db, oldVersion, newVersion);
		monumentTypeDbHandler.onUpgrade(db, oldVersion, newVersion);

		// all other tables
		// ...
	}

	public SQLiteDatabase GetWritableDataBase()
	{
		return getWritableDatabase();
	}
	
	public SQLiteDatabase GetReadableDataBase()
	{
		return getReadableDatabase();
	}
	
	/**
	 * All CRUD(Create, Read, Update, Delete) Operations are in specific classes
	 */

	/** Debug utility that dumps a cursor on logcat. */
	public static final void dumpCursor(String logTag, String name, Cursor c) {
		Log.d(logTag, "Dumping cursor " + name + " containing " + c.getCount() + " rows");

		// Dump the column names.
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < c.getColumnCount(); i++) {
			if (sb.length() > 0) sb.append(" ");
			sb.append(c.getColumnName(i));
		}
		Log.d(logTag, sb.toString());

		// Dump the values.
		c.moveToPosition(-1);
		while (c.moveToNext()) {
			sb.setLength(0);
			sb.append("row#");
			sb.append(c.getPosition());

			for (int i = 0; i < c.getColumnCount(); i++) {
				sb.append(" ");

				String s = c.getString(i);
				sb.append(s == null ? "{null}" : s.replaceAll("\\s", "{space}"));
			}
			Log.d(logTag, sb.toString());
		}
	}



}
