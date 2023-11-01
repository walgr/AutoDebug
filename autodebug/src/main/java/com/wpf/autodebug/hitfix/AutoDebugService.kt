package com.wpf.autodebug.hitfix

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import com.taobao.sophix.SophixManager
import com.wpf.autodebug.http.PatchManager
import java.lang.ref.SoftReference

class AutoDebugService : Service() {

    private val uiHandler = Handler()

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        var appVersion = "0.0.0"
        try {
            appVersion = this.packageManager
                .getPackageInfo(this.packageName, 0).versionName
        } catch (ignore: Exception) {
        }
        PatchManager.context = SoftReference(this)
        Log.i("热修复", "服务已启动,地址:${PatchManager.baseUrl}")
        PatchManager.getPatch(this.packageName, appVersion) {
            it?.let {
                if (!it.exists() && it.length() == 0L) return@getPatch
                Log.i("热修复", "发现新patch，正在加载:${it.path}")
                uiHandler.post {
                    Toast.makeText(applicationContext, "发现新补丁，正在修复，请稍等", Toast.LENGTH_LONG).show()
                }
                SophixManager.getInstance().cleanPatches()
                LoadLocalPatch.load(this, packageName ?: "", it.absolutePath, object : IPatchStatusCallback.Stub() {
                    override fun onLoad(i: Int, i2: Int, str: String?, i3: Int) {
                        if (i2 == 100) {
                            Log.i("热修复", "patch加载完成")
                            uiHandler.post {
                                Toast.makeText(applicationContext, "已修复完成，可以重启验证", Toast.LENGTH_LONG).show()
                            }
                            PatchManager.loadPatchName = it.name
                            it.deleteRecursively()
                        }
                    }
                })
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }
}