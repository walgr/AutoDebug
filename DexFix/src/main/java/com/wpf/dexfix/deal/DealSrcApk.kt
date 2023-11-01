package com.wpf.dexfix.deal

import com.android.zipflinger.ZipArchive
import com.wpf.dexfix.utils.FileUtil
import com.wpf.dexfix.utils.JADXUtil
import com.wpf.dexfix.utils.ResourceManager
import com.wpf.dexfix.utils.createCheck
import com.wpf.dexfix.utils.subString
import net.dongliu.apk.parser.ApkParsers
import java.io.File

object DealSrcApk {

    /**
     * 处理源Apk
     * @return 源Apk内Application类名
     */
    fun deal(srcApkPath: String, callback: ((String, String) -> Unit)? = null) {
        //获取Application
        val srcApkFile = File(srcApkPath)
        val androidManifest = ApkParsers.getManifestXml(srcApkFile)

        val applicationStartPos = androidManifest.indexOf("<application")
        val applicationStr = androidManifest.substring(
            applicationStartPos,
            androidManifest.indexOf(">", applicationStartPos + 1) + 1
        )

        val applicationClass = applicationStr.subString("android:name=\"", "\"")
        if (applicationClass.isEmpty()) {
            println("包内未设置Application")
            callback?.invoke("", "")
        } else {
            var applicationClassName = applicationClass.split(".").last()
            var applicationPackage = applicationClass.replace(".${applicationClassName}", "")
            //判断此application是否继承SophixApplication
            val srcApkZip = ZipArchive(srcApkFile.toPath())
            val srcDexClassFile =
                File(ResourceManager.getTempPath()+ "classes.dex").createCheck(true)
            FileUtil.save2File(srcApkZip.getInputStream("classes.dex"), srcDexClassFile)

            JADXUtil.getJavaInDex(
                "$applicationPackage.$applicationClassName",
                ResourceManager.getTempPath().take(ResourceManager.getTempPath().length - 1),
                srcDexClassFile.path
            )

            val srcApplicationClassFile = File(
                ResourceManager.getTempPath() + "sources" + File.separator + applicationPackage.replace(
                    ".",
                    File.separator
                ) + File.separator + applicationClassName + ".java"
            )

            val srcApplicationClassStr = srcApplicationClassFile.readText()
            srcDexClassFile.delete()
            srcApplicationClassFile.delete()
            JADXUtil.delJar()
            if (srcApplicationClassStr.contains("SophixApplication")) {
                //源包Application继承了SophixApplication
                println("源包Application继承了SophixApplication, 获取原始Application")
                val sophixEntryStr = srcApplicationClassStr.subString("@SophixEntry(", ")").replace(".class", "")
                println("原始Application:$sophixEntryStr")
                if (sophixEntryStr.contains(".")) {
                    applicationClassName = sophixEntryStr.split(".").last()
                    applicationPackage = sophixEntryStr.replace(".${applicationClassName}", "")
                } else {
                    applicationClassName = sophixEntryStr
                }
            }
            srcApkZip.close()
            srcDexClassFile.delete()

            println("包内Application:$applicationPackage.$applicationClassName")
            callback?.invoke(applicationPackage, applicationClassName)
        }
    }
}