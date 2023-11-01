package com.wpf.autodebug.demo

import android.app.Application
import android.util.Log

class RealApp: Application() {

    override fun onCreate() {
        super.onCreate()
        Log.i("热修复", "原始Application加载")
    }
}