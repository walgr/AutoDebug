package com.wpf.autodebug.hitfix

import android.app.Application
import android.util.Log

class AutoDebugApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Log.i("热修复", "修复Application加载")
    }
}