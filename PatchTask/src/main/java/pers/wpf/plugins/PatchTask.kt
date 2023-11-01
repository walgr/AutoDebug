package pers.wpf.plugins

import com.wpf.patchtool.http.PatchManager
import net.dongliu.apk.parser.ApkParsers
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File

open class PatchTask : DefaultTask() {

    private lateinit var patchConfig: PatchConfig

    @TaskAction
    fun dealPatch() {
        patchConfig = project.extensions.findByType(PatchConfig::class.java) ?: PatchConfig()
        val curPath = project.buildFile.parent + File.separator
        println("自动打补丁包插件运行中...")
        println("当前目录:$curPath")
        val fixApkPath = curPath + "build/outputs/apk/release/".replace("/", File.separator)
        val fixApk = File(fixApkPath).listFiles()?.find {
            it.exists() && it.extension == "apk"
        }
        if (fixApk == null) {
            println("在${fixApkPath}下未找到已修复Apk,程序退出")
            return
        }
        val apkMeta = ApkParsers.getMetaInfo(fixApk)
        println("开始下载源Apk地址：${patchConfig.serverBaseUrl}getApk?package=${apkMeta.packageName}&appVersion=${apkMeta.versionName},下载到:${fixApk.parent}")
        PatchManager.downloadSrcApk(
            patchConfig.serverBaseUrl,
            apkMeta.packageName,
            apkMeta.versionName,
            fixApk.parent
        ) {
            if (it != null) {
                println("下载源Apk完成,开始打补丁包并上传")
                runCatching {
                    com.wpf.patchtool.main(
                        arrayOf(
                            "-serviceBaseUrl",
                            patchConfig.serverBaseUrl,
                            "-srcApkPath",
                            it.path.replace("\\", "\\\\"),
                            "-fixApkPath",
                            fixApk.path.replace("\\", "\\\\"),
                            "-signFilePath",
                            patchConfig.signFilePath,
                            "-signAlias",
                            patchConfig.signAlias,
                            "-keyStorePassword",
                            patchConfig.keyStorePassword,
                            "-keyPassword",
                            patchConfig.keyPassword,
                            "-ClassFilterFilePath",
                            patchConfig.classFilterFilePath,
                            "-isForceColdFix",
                            if (patchConfig.isForceColdFix) "1" else "0",
                            "-isIgnoreSo",
                            if (patchConfig.isIgnoreSo) "1" else "0",
                            "-isIgnoreRes",
                            if (patchConfig.isIgnoreRes) "1" else "0",
                        )
                    )
                }
                println("程序已执行结束")
            } else {
                println("未找到源包，程序结束")
            }
        }
    }
}