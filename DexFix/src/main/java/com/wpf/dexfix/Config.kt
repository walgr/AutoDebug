package com.wpf.dexfix

import java.io.File

var curPath = File(".").canonicalPath + File.separator

val isLinuxRuntime by lazy { System.getProperties().getProperty("os.name").contains("Linux") }
val isWinRuntime by lazy { System.getProperties().getProperty("os.name").contains("Windows") }
val isMacRuntime by lazy { System.getProperties().getProperty("os.name").contains("Mac") }