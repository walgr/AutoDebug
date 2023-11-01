package com.wpf.patchtool.utils

class PatchConfig(
    val AesKey: String? = null,
    val ClassFilterFilePath: String? = null,
    val KeyAlias: String? = null,
    val KeyPassword: String? = null,
    val KeyStorePassword: String? = null,
    val KeyStorePath: String? = null,
    val NewApkPath: String? = null,
    val OldApkPath: String? = null,
    val OutputDirPath: String? = null,
    val isForceColdFix: Boolean? = null,
    val isIgnoreRes: Boolean? = null,
    val isIgnoreSo: Boolean? = null,
)