package com.wpf.dexfix.utils

/**
 * 映射到命令
 */

object ManifestEditorUtil {
    private val manifestEditorPath: String by lazy { ResourceManager.getResourceFile("ManifestEditor.jar").path }

    fun delJar() {
        ResourceManager.delResourceByPath(manifestEditorPath)
    }

    /**
     * 操作
     */
    fun doCommand(
        cmd: MutableList<String>,
    ) {
//        Main.main(cmd)
        val result = Runtime.getRuntime().exec(RunJar.javaJar(manifestEditorPath, cmd.toTypedArray()))
        val resultStr = result.errorStream.readBytes().decodeToString()
        if (resultStr.isNotEmpty()) {
            println(resultStr)
        }
    }
}