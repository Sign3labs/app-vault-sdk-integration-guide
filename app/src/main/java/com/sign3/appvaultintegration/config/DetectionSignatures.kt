package com.sign3.appvaultintegration.config

/**
 * Use case 3: strings inside collections (e.g. detection signatures) are obfuscated too,
 * so a plain `strings` search of the APK won't reveal what the app looks for.
 */
object DetectionSignatures {

    // Paths of tools an app might check for
    fun suspiciousPaths(): List<String> = listOf(
        "/data/local/tmp/frida-server",
        "/system/xbin/su",
        "/sbin/magisk",
    )
}
