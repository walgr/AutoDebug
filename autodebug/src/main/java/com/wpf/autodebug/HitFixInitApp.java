package com.wpf.autodebug;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Keep;

import com.taobao.sophix.PatchStatus;
import com.taobao.sophix.SophixApplication;
import com.taobao.sophix.SophixEntry;
import com.taobao.sophix.SophixManager;
import com.wpf.autodebug.hitfix.AutoDebugService;

public class HitFixInitApp extends SophixApplication {

    @Keep
    @SophixEntry(com.wpf.autodebug.SubstituteApp.class)
    static class RealApplicationStub {
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        String appVersion = "0.0.0";
        try {
            Log.i("热修复", "壳热修复已初始化");
            appVersion = this.getPackageManager()
                    .getPackageInfo(this.getPackageName(), 0)
                    .versionName;
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("热修复", "" + e.getMessage());
        }
        final SophixManager instance = SophixManager.getInstance();
        instance.setContext(this)
                .setAppVersion(appVersion)
                .setSecretMetaData("", "", "")
                .setEnableDebug(true)
                .setEnableFullLog()
                .setPatchLoadStatusStub((mode, code, info, handlePatchVersion) -> {
                    Log.i("热修复", "加载数据(" + "mode:" + mode + " code:" + code + " info:" + info + " handlePatchVersion:" + handlePatchVersion + ")");
                    if (code == PatchStatus.CODE_LOAD_SUCCESS) {
                        Log.i("热修复", "补丁已生效");
                    } else if (code == PatchStatus.CODE_LOAD_RELAUNCH) {
                        // 如果需要在后台重启，建议此处用SharePreference保存状态。
                        Log.i("热修复", "需要重启补丁才生效");
                        Toast.makeText(getApplicationContext(), "需要重启补丁才生效", Toast.LENGTH_LONG).show();
                        SophixManager.getInstance().killProcessSafely();
                    } else {
                        Log.i("热修复", "修复失败");
                        Toast.makeText(getApplicationContext(), "修复失败，请联系开发", Toast.LENGTH_LONG).show();
                    }
                }).initialize();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // 启动services
        Log.i("热修复", "补丁管理服务启动");
        Toast.makeText(getApplicationContext(), "补丁管理服务启动", Toast.LENGTH_LONG).show();
        startService(new Intent(HitFixInitApp.this, AutoDebugService.class));
    }
}