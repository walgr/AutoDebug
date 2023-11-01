package com.wpf.fileserver.http

import com.wpf.fileserver.PatchPath
import com.wpf.fileserver.PatchRootPath
import io.ktor.http.ContentDisposition
import io.ktor.http.HttpHeaders
import io.ktor.http.content.PartData
import io.ktor.http.content.readAllParts
import io.ktor.server.application.*
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.utils.io.core.readBytes
import java.io.File

const val Error_File_Not_Find = "文件未找到"

fun Application.configureRouting() {
    routing {
        //处理基础包
        get("/getApk{package?}{appVersion?}") {
            val packageName =
                call.parameters["package"] ?: return@get call.respondText("请输入包名")
            val appVersion =
                call.parameters["appVersion"] ?: return@get call.respondText("请输入版本号")
            runCatching {
                val file = File(PatchRootPath + File.separator + packageName + File.separator + appVersion)
                if (!file.exists() || !file.isDirectory) {
                    println("当前文件:${file.path}")
                    call.respondText(Error_File_Not_Find)
                    return@get
                }
                val findPatchFile = file.listFiles()?.findLast { it.isFile && it.extension == "apk" }
                if (findPatchFile == null) {
                    println("文件夹下未找到apk文件:${file.path}")
                    call.respondText(Error_File_Not_Find)
                    return@get
                }
                call.response.header(
                    HttpHeaders.ContentDisposition,
                    ContentDisposition.Attachment.withParameter(
                        ContentDisposition.Parameters.FileName,
                        findPatchFile.name
                    ).withParameter(
                        ContentDisposition.Parameters.Size,
                        findPatchFile.length().toString()
                    ).toString()
                )
                call.respondFile(findPatchFile)
            }.onFailure {
                call.respondText(Error_File_Not_Find)
            }
        }
        put("/uploadApk") {
            val part = call.receiveMultipart()
            val parts = part.readAllParts()
            val packageName = parts.value("package") ?: return@put call.respondText("请输入包名")
            val appVersion = parts.value("appVersion") ?: return@put call.respondText("请输入版本号")
            if (appVersion.isEmpty()) {
                call.respondText("未找到该版本号")
                return@put
            }
            val file = parts.file("file")
            if (file == null) {
                call.respondText("未接受到文件")
                return@put
            }
            file.originalFileName?.let {
                val patchRootFile = File(PatchRootPath + File.separator + packageName + File.separator + appVersion)
                if (!patchRootFile.exists()) {
                    patchRootFile.mkdirs()
                }
                val copyNewFile = File(patchRootFile.path + File.separator + it)
                if (!copyNewFile.exists()) {
                    copyNewFile.createNewFile()
                }
                copyNewFile.writeBytes(file.provider.invoke().readBytes())
                call.respondText("保存文件成功")
            }
        }
        delete("/deleteApk{package?}{appVersion?}") {
            val packageName =
                call.parameters["package"] ?: return@delete call.respondText("请输入包名")
            val appVersion =
                call.parameters["appVersion"] ?: return@delete call.respondText("请输入版本号")
            File(PatchRootPath + File.separator + packageName + File.separator + appVersion).listFiles()
                ?.filter { it.isFile && it.extension == "apk" }?.forEach {
                    it.deleteRecursively()
                }
            call.respondText("删除Patch" + "成功")
        }
        //处理patch
        get("/getPatch{package?}{appVersion?}") {
            val packageName =
                call.parameters["package"] ?: return@get call.respondText("请输入包名")
            val appVersion =
                call.parameters["appVersion"] ?: return@get call.respondText("请输入版本号")
            runCatching {
                val file =
                    File(PatchRootPath + File.separator + packageName + File.separator + appVersion + File.separator + PatchPath)
                if (!file.exists() || !file.isDirectory) {
                    call.respondText(Error_File_Not_Find)
                    return@get
                }
                val findPatchFile =
                    file.listFiles()?.findLast { it.isFile }
                if (findPatchFile == null) {
                    call.respondText(Error_File_Not_Find)
                    return@get
                }
                call.response.header(
                    HttpHeaders.ContentDisposition,
                    ContentDisposition.Attachment.withParameter(
                        ContentDisposition.Parameters.FileName,
                        findPatchFile.name
                    ).withParameter(
                        ContentDisposition.Parameters.Size,
                        findPatchFile.length().toString()
                    ).toString()
                )
                call.respondFile(findPatchFile)
            }.onFailure {
                call.respondText(Error_File_Not_Find)
            }
        }
        put("/uploadPatch") {
            val part = call.receiveMultipart()
            val parts = part.readAllParts()
            val packageName = parts.value("package") ?: return@put call.respondText("请输入包名")
            val appVersion = parts.value("appVersion") ?: return@put call.respondText("请输入版本号")
            if (appVersion.isEmpty()) {
                call.respondText("未找到该版本号")
                return@put
            }
            val file = parts.file("file")
            if (file == null) {
                call.respondText("未接受到文件")
                return@put
            }
            file.originalFileName?.let {
                val patchRootFile =
                    File(PatchRootPath + File.separator + packageName + File.separator + appVersion + File.separator + PatchPath)
                if (!patchRootFile.exists()) {
                    patchRootFile.mkdirs()
                }
                val copyNewFile = File(patchRootFile.path + File.separator + it)
                if (!copyNewFile.exists()) {
                    copyNewFile.createNewFile()
                } else {
                    copyNewFile.renameTo(File(copyNewFile.parent + File.separator + copyNewFile.nameWithoutExtension + "-${System.currentTimeMillis()}." + copyNewFile.extension))
                }
                copyNewFile.writeBytes(file.provider.invoke().readBytes())
                call.respondText("保存文件成功")
            }
        }
        delete("/deletePatch{package?}{appVersion?}") {
            val packageName =
                call.parameters["package"] ?: return@delete call.respondText("请输入包名")
            val appVersion =
                call.parameters["appVersion"] ?: return@delete call.respondText("请输入版本号")
            val result =
                File(PatchRootPath + File.separator + packageName + File.separator + appVersion + File.separator + PatchPath).deleteRecursively()
            call.respondText("删除Patch" + if (result) "成功" else "失败")
        }
    }
}

fun List<PartData>.value(name: String) =
    try {
        (first { it.name == name } as? PartData.FormItem)?.value
    } catch (e: Exception) {
        null
    }

fun List<PartData>.file(name: String) =
    try {
        first { it.name == name } as? PartData.FileItem
    } catch (e: Exception) {
        null
    }
