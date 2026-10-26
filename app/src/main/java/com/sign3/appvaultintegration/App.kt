package com.sign3.appvaultintegration

import android.app.Application
import com.sign3.appvault.AppVaultCipher
import kotlin.concurrent.thread

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        /*** Use case 1: warm up. Key derivation is slow and runs once per process, so do it off the
         * main thread now; first access to an obfuscated string is then a cache hit. Never throws.
         **/
        thread { AppVaultCipher.warmUp() }
    }
}
