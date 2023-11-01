package com.wpf.fileserver.http

import com.wpf.fileserver.BASE_URL
import com.wpf.fileserver.HOST
import com.wpf.fileserver.PORT
import com.wpf.fileserver.curPath
import io.ktor.server.application.Application
import io.ktor.server.cio.CIO
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.embeddedServer

fun main(args: Array<String>? = null) {
    args?.forEachIndexed { index, arg ->
        val nextInput = args.getOrNull(index + 1) ?: ""
        if (arg.startsWith("-") && nextInput.startsWith("-")) {
            println("参数异常，请检查输入")
            return
        }
        if ("-savePath" == arg) {
            curPath = nextInput
        }
        if ("-port" == arg) {
            PORT = nextInput.toInt()
        }
    }
    HttpService.startServer()
}

object HttpService {
    private var engine: ApplicationEngine? = null
    fun startServer() {
        println("文件服务已启动，地址：${BASE_URL}:${PORT}，当前运行目录:${curPath}")
        engine = embeddedServer(CIO, port = PORT, host = HOST, module = Application::module).start(wait = true)
    }

    fun stopServer() {
        println("文件服务已关闭")
        engine?.stop()
    }
}

fun Application.module() {
    configureRouting()
}