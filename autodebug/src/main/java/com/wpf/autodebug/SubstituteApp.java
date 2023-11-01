package com.wpf.autodebug;

import android.app.Application;
import android.util.Log;

public class SubstituteApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("热修复", "替身Application加载");
    }
}
