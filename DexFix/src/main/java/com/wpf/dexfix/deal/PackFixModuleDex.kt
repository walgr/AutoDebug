package com.wpf.dexfix.deal

import com.android.zipflinger.BytesSource
import com.android.zipflinger.ZipArchive
import com.wpf.dexfix.curPath
import com.wpf.dexfix.isLinuxRuntime
import com.wpf.dexfix.isMacRuntime
import com.wpf.dexfix.isWinRuntime
import com.wpf.dexfix.utils.AXMLEditor2Util
import com.wpf.dexfix.utils.ApkSignerUtil
import com.wpf.dexfix.utils.FileUtil
import com.wpf.dexfix.utils.ManifestEditorUtil
import com.wpf.dexfix.utils.ResourceManager
import com.wpf.dexfix.utils.ZipalignUtil
import com.wpf.dexfix.utils.createCheck
import java.io.File
import java.util.zip.Deflater

/**
 * 热修复项目打包成dex
 * 1.项目zip解压
 * 2.替换项目中RealApp为源ApkApplication类
 * 3.替换热修复Application@SophixEntry(源ApkApplication.class)
 * 4.项目打包
 * 5.修改Manifest
 * 6.对齐签名
 */
object PackFixModuleDex {
    private const val SubstituteApp = "SubstituteApp"

    fun deal(
        serviceBaseUrl: String = "http://0.0.0.0:8080/",
        srcApkFilePath: String,
        srcApplicationPackage: String,
        srcApplicationClassName: String,
        otherDependencies: String = "",
        sdkPath: String = "",
        signFilePath: String = "",
        signAlias: String = "",
        keyStorePassword: String = "",
        keyPassword: String = "",
    ): String {
        val projectZipPath =
            ResourceManager.getResourceFile("AutoDebug.zip", overwrite = true).path
        //项目zip解压
        val projectFile = File(projectZipPath)
        val projectDecompressionPath =
            File(projectFile.parent + File.separator + "cache").createCheck().path + File.separator
        println("解压AutoDebug.zip")
        FileUtil.unZipFiles(projectFile, projectDecompressionPath)
        ResourceManager.delResourceByPath(projectZipPath)

        val projectRootPath = projectDecompressionPath + "AutoDebug" + File.separator
        //添加额外依赖
        if (otherDependencies.isNotEmpty()) {
            val autoDebugKts =
                File(projectRootPath + "autodebug" + File.separator + "build.gradle.kts")
            val autoDebugKtsStr = autoDebugKts.readBytes().decodeToString()
            val autoDebugKtsAppendStr = autoDebugKtsStr.replaceRange(
                autoDebugKtsStr.lastIndexOf(")\n}"),
                autoDebugKtsStr.length,
                ")\t\n${otherDependencies}\n}"
            )
            autoDebugKts.writeText(autoDebugKtsAppendStr)
        }
        //修改sdk目录
        if (sdkPath.isNotEmpty()) {
            val localPropertiesFile = File(projectRootPath + "local.properties")
            localPropertiesFile.writeText("sdk.dir=${sdkPath}")
        }

        //修改HitFixInitApp @SophixEntry()
        if (srcApplicationPackage.isNotEmpty() && srcApplicationClassName.isNotEmpty()) {
            val modulePath = projectRootPath +
                    "autodebug/src/main/java/com/wpf/autodebug/".replace("/", File.separator)
            //替换项目中SubstituteApp为源ApkApplication类
            val substituteAppFile = File("$modulePath$SubstituteApp.java")
            val srcRealAppFile = File("$modulePath$srcApplicationClassName.java")
            substituteAppFile.copyTo(srcRealAppFile, true)
            val newSrcRealAppFileStr = String(srcRealAppFile.readBytes())
                .replace("package com.wpf.autodebug;", "package ${srcApplicationPackage};")
                .replace(SubstituteApp, srcApplicationClassName)
            srcRealAppFile.writeText(newSrcRealAppFileStr)

            //替换热修复Application@SophixEntry(源ApkApplication.class）
            val hitFixAppFile = File(modulePath + File.separator + "HitFixInitApp.java")
            val newHitFixAppFileStr = String(hitFixAppFile.readBytes()).replace(
                "com.wpf.autodebug.${SubstituteApp}",
                "$srcApplicationPackage.$srcApplicationClassName"
            )
            hitFixAppFile.writeText(newHitFixAppFileStr)
        }

        //项目打包
        println("正在打热修复壳AAR")
        if (isWinRuntime) {
            val process = ProcessBuilder(
                "${projectRootPath}gradlew.bat", ":autodebug:assembleRelease",
            ).directory(File(projectRootPath)).start()
            val error = process.errorStream.readBytes().decodeToString()
            if (error.isNotEmpty()) {
                println(error)
            }
            val result = process.waitFor()
            println(if (result == 0) "AAR打包成功" else "AAR打包失败")
            if (result != 0) return ""
        } else if (isLinuxRuntime || isMacRuntime) {
            Runtime.getRuntime().exec(arrayOf("chmod", "-R", "+x", "${projectRootPath}gradlew"))
            val process = ProcessBuilder(
                "${projectRootPath}gradlew", ":autodebug:assembleRelease",
            ).directory(File(projectRootPath)).start()
            val error = process.errorStream.readBytes().decodeToString()
            if (error.isNotEmpty()) {
                println(error)
            }
            val result = process.waitFor()
            println(if (result == 0) "AAR打包成功" else "AAR打包失败")
            if (result != 0) return ""
        }

        val autoDebugAARFile = File(
            projectRootPath + "autodebug/build/outputs/aar/".replace("/", File.separator)
        ).listFiles()?.first { it.extension == "aar" }
        if (autoDebugAARFile == null) {
            println("打包aar失败")
            return ""
        }
        println("打包aar:${autoDebugAARFile.name}")
        val autoDebugAARZip = ZipArchive(autoDebugAARFile.toPath())
        val classJarName = "classes.jar"
        val classJarDecompressionFile =
            File(autoDebugAARFile.parent + File.separator + classJarName).createCheck(true)
        FileUtil.save2File(autoDebugAARZip.getInputStream(classJarName), classJarDecompressionFile)

        if (srcApplicationClassName.isNotEmpty()) {
            val classJarDecompressionZip = ZipArchive(classJarDecompressionFile.toPath())
            println("删除${classJarName}中的$srcApplicationClassName.class")
            classJarDecompressionZip.delete(classJarDecompressionZip.listEntries().find {
                it.contains("${srcApplicationClassName}.class")
            })
            classJarDecompressionZip.close()
        }

        autoDebugAARZip.delete(classJarName)
        autoDebugAARZip.add(
            BytesSource(
                classJarDecompressionFile.toPath(),
                classJarName,
                Deflater.DEFAULT_COMPRESSION
            )
        )
        autoDebugAARZip.close()

        val appLibsAARFile = File(
            projectRootPath + "app/libs/".replace("/", File.separator) + autoDebugAARFile.name
        )
        autoDebugAARFile.copyTo(appLibsAARFile, true)

        //app打包
        println("正在打修复Apk")
        if (isWinRuntime) {
            val process = ProcessBuilder(
                "${projectRootPath}gradlew.bat", ":app:assembleRelease",
            ).directory(File(projectRootPath)).start()
            val error = process.errorStream.readBytes().decodeToString()
            if (error.isNotEmpty()) {
                println(error)
            }
            val result = process.waitFor()
            println(if (result == 0) "Apk打包成功" else "Apk打包失败")
            if (result != 0) return ""
        } else if (isLinuxRuntime || isMacRuntime) {
            Runtime.getRuntime().exec(arrayOf("chmod", "-R", "+x", "${projectRootPath}gradlew"))
            val process = ProcessBuilder(
                "${projectRootPath}gradlew", ":app:assembleRelease",
            ).directory(File(projectRootPath)).start()
            val error = process.errorStream.readBytes().decodeToString()
            if (error.isNotEmpty()) {
                println(error)
            }
            val result = process.waitFor()
            println(if (result == 0) "Apk打包成功" else "Apk打包失败")
            if (result != 0) return ""
        }

        //把apk内的dex和lib下的.so、config文件复制到源Apk中，并对齐签名
        val autoDebugAppFile = File(
            projectRootPath + "app/build/outputs/apk/release/".replace("/", File.separator)
        ).listFiles()?.first {
            it.extension == "apk"
        } ?: return ""

        val srcApkFile = File(srcApkFilePath)
        val srcApkFileZip = ZipArchive(srcApkFile.toPath())
        //dex+1处理
        val srcDexList = srcApkFileZip.listEntries().toList().filter {
            it.contains(".dex")
        }
        val delFileList = arrayListOf<File>()
        srcDexList.sortedByDescending {
            val dexStart = "classes"
            it.subSequence(
                it.indexOf(dexStart) + dexStart.length,
                it.indexOf(".")
            ).ifEmpty { "0" }.toString().toInt()
        }.forEach {
            val dexStart = "classes"
            val newDexName = dexStart + (it.subSequence(
                it.indexOf(dexStart) + dexStart.length,
                it.indexOf(".")
            ).ifEmpty { "0" }.toString()
                .toInt() + if ("classes.dex" == it) 2 else 1).toString() + ".dex"
            println("包内dex重命名:${it}--->${newDexName}")
            val newDex = File(curPath + File.separator + newDexName)
            FileUtil.save2File(srcApkFileZip.getInputStream(it), newDex)
            srcApkFileZip.delete(it)
            srcApkFileZip.add(
                BytesSource(
                    newDex.toPath(), newDexName, Deflater.DEFAULT_COMPRESSION
                )
            )
            delFileList.add(newDex)
        }
        delFileList.forEach {
            it.delete()
        }
        //把壳dex放入源包
        val autoDebugAppZip = ZipArchive(autoDebugAppFile.toPath())
        val classesDexName = "classes.dex"
        val classesDexFile = File(autoDebugAppFile.parent + File.separator + classesDexName)
        FileUtil.save2File(autoDebugAppZip.getInputStream(classesDexName), classesDexFile)
        srcApkFileZip.add(
            BytesSource(
                classesDexFile.toPath(), classesDexName, Deflater.DEFAULT_COMPRESSION
            )
        )
        println("壳dex已放入源包")


        val configName = "autodebug.config"
        val configFile = File(
            projectRootPath + "autodebug/src/main/assets/".replace(
                "/",
                File.separator
            ) + configName
        )
        //修改配置文件
        if (serviceBaseUrl.isNotEmpty()) {
            configFile.writeText(
                configFile.readText().replace("http://0.0.0.0:8080/", serviceBaseUrl)
            )
            println("patch服务器配置文件已修改成:${serviceBaseUrl}")
        }
        //添加config配置文件
        srcApkFileZip.delete("assets/${configName}")
        srcApkFileZip.add(
            BytesSource(
                configFile.toPath(), "assets/${configName}", Deflater.DEFAULT_COMPRESSION
            )
        )
        println("patch服务器配置文件已放入源包")
        //添加.so文件
        srcApkFileZip.delete("lib/armeabi/libsophix.so")
        srcApkFileZip.add(
            BytesSource(
                autoDebugAppZip.getInputStream("lib/armeabi/libsophix.so"),
                "lib/armeabi/libsophix.so",
                Deflater.NO_COMPRESSION
            )
        )
        srcApkFileZip.delete("lib/armeabi-v7a/libsophix.so")
        srcApkFileZip.add(
            BytesSource(
                autoDebugAppZip.getInputStream("lib/armeabi-v7a/libsophix.so"),
                "lib/armeabi-v7a/libsophix.so",
                Deflater.NO_COMPRESSION
            )
        )
        srcApkFileZip.delete("lib/arm64-v8a/libsophix.so")
        srcApkFileZip.add(
            BytesSource(
                autoDebugAppZip.getInputStream("lib/arm64-v8a/libsophix.so"),
                "lib/arm64-v8a/libsophix.so",
                Deflater.NO_COMPRESSION
            )
        )
        autoDebugAppZip.close()

        //修改AndroidManifest里Application
        val srcManifestFile = File(srcApkFile.parent + File.separator + "AndroidManifest.xml")
        val srcManifestFixFile =
            File(srcApkFile.parent + File.separator + "AndroidManifest_Fix.xml").createCheck(true)
        FileUtil.save2File(srcApkFileZip.getInputStream("AndroidManifest.xml"), srcManifestFile)
        //插入AutoDebugService
        val insertFile =
            ResourceManager.getResourceFile("insert_autodebugservice.xml", overwrite = true)
        AXMLEditor2Util.doCommandTagInsert(
            insertFile.path,
            srcManifestFile.path,
            srcManifestFixFile.path
        )
        AXMLEditor2Util.delJar()
        insertFile.delete()
        ManifestEditorUtil.doCommand(
            mutableListOf(
                srcManifestFixFile.path,
                "-f",
                "-o",
                srcManifestFile.path,
                "-an",
                "com.wpf.autodebug.HitFixInitApp"
            )
        )
        srcApkFileZip.delete("AndroidManifest.xml")
        srcApkFileZip.add(
            BytesSource(
                srcManifestFile.toPath(), "AndroidManifest.xml", Deflater.DEFAULT_COMPRESSION
            )
        )
        ManifestEditorUtil.delJar()

        println("源Apk套壳完成")
        srcApkFileZip.close()
        srcManifestFile.delete()
        srcManifestFixFile.delete()
        classesDexFile.delete()
        File(projectDecompressionPath).deleteRecursively()

        //对齐修补后的Apk
        var srcSignPath = ""
        if (signFilePath.isNotEmpty()) {
            println("正在对齐签名...")
            val srcZipalignFile =
                File(srcApkFile.parent + File.separator + srcApkFile.nameWithoutExtension + "_fix_zipalign.apk")
            if (ZipalignUtil.zipalign(srcApkFile.path, srcZipalignFile.path)) {
                srcSignPath =
                    srcZipalignFile.parent + File.separator + srcApkFile.nameWithoutExtension + "_fix_sign.apk"
                if (ApkSignerUtil.sign(
                        signFilePath,
                        signAlias,
                        keyStorePassword,
                        keyPassword,
                        srcSignPath,
                        srcZipalignFile.path
                    )
                ) {
                    println("对齐签名完成")
                }
                srcZipalignFile.delete()
            }
        }
        ApkSignerUtil.delJar()
        ZipalignUtil.delJar()
        return srcSignPath
    }
}