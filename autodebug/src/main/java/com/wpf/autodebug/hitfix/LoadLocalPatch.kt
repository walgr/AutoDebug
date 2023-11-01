package com.wpf.autodebug.hitfix

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.os.RemoteException


object LoadLocalPatch {

    private var mIDownloadService: IDownloadService? = null
    private val mConnection: ServiceConnection = object : ServiceConnection {
        // android.content.ServiceConnection
        override fun onServiceConnected(componentName: ComponentName, iBinder: IBinder) {
            mIDownloadService = IDownloadService.Stub.asInterface(iBinder)
            addLocalPatch(patchPath)
        }

        // android.content.ServiceConnection
        override fun onServiceDisconnected(componentName: ComponentName) {
            mIDownloadService = null
        }

        fun addLocalPatch(str: String) {
            if (mIDownloadService != null) {
                try {
                    mIDownloadService?.addPatch(str, mCallback)
                } catch (e: RemoteException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private var isBindApp = false
    private var patchPath: String = ""
    private var mCallback: IPatchStatusCallback? = null
    fun load(
        context: Context,
        packageName: String,
        patchPath: String,
        mCallback: IPatchStatusCallback? = null
    ) {
        this.patchPath = patchPath
        this.mCallback = mCallback
        if (!isBindApp) {
            context.bindService(Intent().apply {
                action = "com.taobao.hotfix.action"
                `package` = packageName
            }, mConnection, Context.BIND_AUTO_CREATE)
            isBindApp = true
        }
    }

    fun destroy(context: Context) {
        kotlin.runCatching {
            context.unbindService(mConnection)
        }
    }
}