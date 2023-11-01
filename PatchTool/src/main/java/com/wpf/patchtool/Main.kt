package com.wpf.patchtool

import com.wpf.patchtool.deal.DealApk
import com.wpf.patchtool.http.PatchManager
import com.wpf.patchtool.utils.ResourceManager
import com.wpf.patchtool.utils.curPath
import net.dongliu.apk.parser.ApkParsers
import java.io.File

fun main(args: Array<String>? = null) {
    if (args.isNullOrEmpty()) {
        println("参数异常，请检查输入参数")
        return
    }
    var serviceBaseUrl = "http://0.0.0.0:8080/"
    var srcApkPath = ""
    var fixApkPath = ""
    var signFilePath = ""
    var signAlias = ""
    var keyStorePassword = ""
    var keyPassword = ""
    var classFilterFilePath = ""
    var isForceColdFix = false
    var isIgnoreSo = false
    var isIgnoreRes = false
    var uploadPatch = true
    args.forEachIndexed { index, arg ->
        val nextInput = args.getOrNull(index + 1) ?: ""
        if (arg.startsWith("-") && nextInput.startsWith("-")) {
            println("参数异常，请检查输入")
            return
        }
        if ("-serviceBaseUrl" == arg) {
            serviceBaseUrl = nextInput
        }
        if ("-srcApkPath" == arg) {
            srcApkPath = nextInput
        }
        if ("-fixApkPath" == arg) {
            fixApkPath = nextInput
        }
        if ("-uploadPatch" == arg) {
            uploadPatch = nextInput == "1"
        }
        if ("-signFilePath" == arg) {
            signFilePath = nextInput
        }
        if ("-signAlias" == arg) {
            signAlias = nextInput
        }
        if ("-keyStorePassword" == arg) {
            keyStorePassword = nextInput
        }
        if ("-keyPassword" == arg) {
            keyPassword = nextInput
        }
        if ("-ClassFilterFilePath" == arg) {
            classFilterFilePath = nextInput
        }
        if ("-isForceColdFix" == arg) {
            isForceColdFix = nextInput == "1"
        }
        if ("-isIgnoreSo" == arg) {
            isIgnoreSo = nextInput == "1"
        }
        if ("-isIgnoreRes" == arg) {
            isIgnoreRes = nextInput == "1"
        }
    }
    curPath = File(srcApkPath).parent + File.separator
    val patchPath = DealApk.deal(
        srcApkPath,
        fixApkPath,
        signFilePath,
        signAlias,
        keyStorePassword,
        keyPassword,
        classFilterFilePath,
        isForceColdFix,
        isIgnoreSo,
        isIgnoreRes
    )
    if (uploadPatch && patchPath.isNotEmpty()) {
        println("上传补丁包中")
        val apkMeta = ApkParsers.getMetaInfo(srcApkPath)
        PatchManager.uploadPatch(serviceBaseUrl, File(patchPath), apkMeta.packageName, apkMeta.versionName) {
            if (it) {
                println("上传补丁包成功")
            } else {
                println("上传补丁包失败")
            }
        }
    }
    File(srcApkPath).delete()
    ResourceManager.delTemp()
}