//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.aliyun.aliinteraction.base;

import android.annotation.SuppressLint;
import android.content.Context;

public class AppContext {
    @SuppressLint({"StaticFieldLeak"})
    private static Context sContext;

    public AppContext() {
    }

    public static void setContext(Context context) {
        sContext = context.getApplicationContext();
    }

    public static Context getContext() {
        return sContext;
    }
}
