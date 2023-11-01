package com.wpf.dexfix

import com.wpf.dexfix.deal.DealSrcApk
import com.wpf.dexfix.deal.PackFixModuleDex
import com.wpf.dexfix.http.ApkManager
import com.wpf.dexfix.utils.ResourceManager
import com.wpf.dexfix.utils.createCheck
import java.io.File

fun main(args: Array<String>? = null) {
    if (args.isNullOrEmpty()) {
        println("参数异常，请检查输入参数")
        return
    }
    var serviceBaseUrl = "http://0.0.0.0:8080/"
    var uploadBaseUrl = ""
    var srcApkPath = ""
    var otherDependencies = ""
    var signFilePath = ""
    var signAlias = ""
    var keyStorePassword = ""
    var keyPassword = ""
    var sdkPath = ""
    var uploadApk = true
    var replaceSrc = false
    args.forEachIndexed { index, arg ->
        val nextInput = args.getOrNull(index + 1) ?: ""
        if (arg.startsWith("-") && nextInput.startsWith("-")) {
            println("参数异常，请检查输入")
            return
        }
        if ("-serviceBaseUrl" == arg) {
            serviceBaseUrl = nextInput
        }
        if ("-uploadBaseUrl" == arg) {
            uploadBaseUrl = nextInput
        }
        if ("-srcApkPath" == arg) {
            srcApkPath = nextInput
        }
        if ("-otherDependencies" == arg) {
            otherDependencies = nextInput
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
        if ("-sdkPath" == arg) {
            sdkPath = nextInput
        }
        if ("-uploadApk" == arg) {
            uploadApk = nextInput == "1"
        }
        if ("-replaceSrc" == arg) {
            replaceSrc = nextInput == "1"
        }
    }
    if (uploadBaseUrl.isEmpty()) {
        uploadBaseUrl = serviceBaseUrl
    }
    val srcApkFile = File(srcApkPath)
    curPath = srcApkFile.parent + File.separator
    println("开始处理加壳,源Apk:${srcApkPath}")
    val srcApkCopyFile = File(srcApkFile.parent + File.separator + srcApkFile.nameWithoutExtension + "_copy." + srcApkFile.extension)
    srcApkFile.copyTo(srcApkCopyFile, overwrite = true)
    DealSrcApk.deal(srcApkCopyFile.path) { srcApplicationPackage, srcApplicationClassName ->
        var fixApkPath = PackFixModuleDex.deal(
            serviceBaseUrl = serviceBaseUrl,
            srcApkFilePath = srcApkCopyFile.path,
            srcApplicationPackage = srcApplicationPackage,
            srcApplicationClassName = srcApplicationClassName,
            otherDependencies = otherDependencies,
            sdkPath = sdkPath,
            signFilePath = signFilePath,
            signAlias = signAlias,
            keyStorePassword = keyStorePassword,
            keyPassword = keyPassword
        )
        println("加壳完毕，位置:${fixApkPath}")
        if (uploadApk && srcApkFile.exists()) {
            println("上传Apk到:${uploadBaseUrl}uploadApk")
            ApkManager.uploadApk(uploadBaseUrl, srcApkFile) {
                if (it) {
                    println("上传Apk成功")
                    if (replaceSrc && fixApkPath.isNotEmpty()) {
                        srcApkFile.copyTo(File(srcApkFile.parent + File.separator + srcApkFile.nameWithoutExtension + "_bak." + srcApkFile.extension).createCheck(true), overwrite = true)
                        File(fixApkPath).copyTo(srcApkFile, overwrite = true)
                        fixApkPath = srcApkFile.path
                    }
                } else {
                    println("上传Apk失败")
                }
            }
        }
    }
    ResourceManager.delTemp()
    srcApkCopyFile.delete()
    println("处理加壳已结束")
}