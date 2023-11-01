package pers.wpf.plugins

open class PatchConfig {

    //patch服务器地址
    open var serverBaseUrl = "http://0.0.0.0:8081/"
    //排除文件配置文件路径
    open var classFilterFilePath = ""

    //签名文件地址
    open var signFilePath = ""

    open var signAlias = ""

    open var keyStorePassword = ""

    open var keyPassword = ""

    //是否冷启动生效
    open var isForceColdFix = false

    //是否忽略so检查
    open var isIgnoreSo = true

    //是否忽略资源检查
    open var isIgnoreRes = true
}