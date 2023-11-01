package com.wpf.dexfix.utils

/**
 * 映射到命令
 */

object AXMLEditor2Util {
    private val axmlEditorPath: String by lazy { ResourceManager.getResourceFile("AXMLEditor2Github.jar").path }

    fun delJar() {
        ResourceManager.delResourceByPath(axmlEditorPath)
    }

    /**
     * 操作属性
     * @param editType -i 新增 -r 删除 -m 更新
     * @param labelName 标签名
     * @param labelIdentification 标签唯一标识
     * @param attrName 属性名
     * @param attrValue 属性值
     * @param inputXmlPath 输入xml
     * @param outputXmlPath 输出xml
     */
    fun doCommandAttr(
        editType: String,
        labelName: String,
        labelIdentification: String,
        attrName: String,
        attrValue: String = "",
        inputXmlPath: String,
        outputXmlPath: String,
    ) {
        val cmd = mutableListOf(
            "-attr",
            editType,
            labelName,
            labelIdentification,
            attrName,
            attrValue,
            inputXmlPath,
            outputXmlPath
        )
        if ("-r" == editType) {
            cmd.remove(attrValue)
        }
//        Main.main(cmd)
        val result = Runtime.getRuntime().exec(RunJar.javaJar(axmlEditorPath, cmd.toTypedArray()))
        val resultStr = result.errorStream.readBytes().decodeToString()
        if (resultStr.isNotEmpty()) {
            println(resultStr)
        }
        result.destroy()
    }

    /**
     * 操作属性
     * @param editType -i 新增 -r 删除
     * @param insertXmlPath 待插入xml
     * @param inputXmlPath 输入xml
     * @param outputXmlPath 输出xml
     */
    fun doCommandTag(
        editType: String,
        insertXmlPath: String,
        inputXmlPath: String,
        outputXmlPath: String,
    ) {
        val cmd = arrayOf("-tag", editType, insertXmlPath, inputXmlPath, outputXmlPath)
//        Main.main(cmd)
        val result = Runtime.getRuntime().exec(RunJar.javaJar(axmlEditorPath, cmd))
        val resultStr = result.errorStream.readBytes().decodeToString()
        if (resultStr.isNotEmpty()) {
            println(resultStr)
        }
        result.destroy()
    }

    /**
     * 插入属性
     * @param insertXmlPath 待插入xml
     * @param inputXmlPath 输入xml
     * @param outputXmlPath 输出xml
     */
    fun doCommandTagInsert(
        insertXmlPath: String,
        inputXmlPath: String,
        outputXmlPath: String,
    ) {
        val cmd = arrayOf("-tag", "-i", insertXmlPath, inputXmlPath, outputXmlPath)
//        Main.main(cmd)
        val result = Runtime.getRuntime().exec(RunJar.javaJar(axmlEditorPath, cmd))
        val resultStr = result.errorStream.readBytes().decodeToString()
        if (resultStr.isNotEmpty()) {
            println(resultStr)
        }
        result.destroy()
        result.errorStream.close()
        result.outputStream.close()
        result.inputStream.close()
    }

    /**
     * 删除属性
     * @param labelName 标签名
     * @param labelIdentification 标签唯一标识
     * @param inputXmlPath 输入xml
     * @param outputXmlPath 输出xml
     */
    fun doCommandTagDel(
        labelName: String,
        labelIdentification: String,
        inputXmlPath: String,
        outputXmlPath: String,
    ) {
        val cmd = arrayOf("-tag", "-r", labelName, labelIdentification, inputXmlPath, outputXmlPath)
//        Main.main(cmd)
        val result = Runtime.getRuntime().exec(RunJar.javaJar(axmlEditorPath, cmd))
        val resultStr = result.errorStream.readBytes().decodeToString()
        if (resultStr.isNotEmpty()) {
            println(resultStr)
        }
        result.destroy()
    }
}