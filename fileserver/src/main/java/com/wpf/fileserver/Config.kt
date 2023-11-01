package com.wpf.fileserver

import java.io.File

val HOST = "0.0.0.0"
val BASE_URL = "http://$HOST"
var PORT = 8081

var curPath = File(".").canonicalPath
val PatchRootPath: String by lazy {
    curPath + File.separator + "UploadPatch" + File.separator
}
val PatchPath = "patch"