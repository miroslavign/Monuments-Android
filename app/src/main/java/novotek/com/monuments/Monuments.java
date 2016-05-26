/*
 * Monuments.java
 * Monuments-Android
 *
 * Created by Miroslav Ignjatovic on 5/23/2016
 * Copyright (c) 2016 Novotek All rights reserved.
 */

package novotek.com.monuments;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;

import java.util.UUID;

import novotek.com.monuments.database.DatabaseHandler;
import novotek.com.monuments.model.MonumentUser;

public class Monuments extends Application {
    public static Monuments instance;
    private Context applicationContext;
    private SharedPreferences preferences;
    private int sdkVersion;

    public static final int MONUMENT_LIST_RESIZE_WIDTH = 100;
    public static final int MONUMENT_LIST_RESIZE_HEIGHT = 100;
    public static final int ROUND_CORNER_SIZE_PICASSO = 18;
    public static final String IMAGE_TYPE_SUFFIX = ".jpg";

    private DatabaseHandler db;
    private MonumentUser loggedUser;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        sdkVersion = android.os.Build.VERSION.SDK_INT;

        Logger
                .init("Monuments")                 // default PRETTYLOGGER or use just init()
                .methodCount(2)                 // default 2
                .hideThreadInfo()               // default shown
                .logLevel(LogLevel.FULL)        // default LogLevel.FULL
                .methodOffset(2);                // default 0
        db = new DatabaseHandler(getApplicationContext());
    }


    public DatabaseHandler getDb(){
        return db;
    }

    public String generateUuid(){
        return UUID.randomUUID().toString();
    }

    public void setLoggedUser(MonumentUser user) {
        this.loggedUser = user;
    }
    public MonumentUser getLoggedUser() {
        return this.loggedUser;
    }
}
