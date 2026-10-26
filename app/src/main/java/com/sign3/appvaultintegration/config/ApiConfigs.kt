package com.sign3.appvaultintegration.config

/**
 * Use case 2: obfuscated literals. This package is listed in `fogPackages`, so every string
 * literal here is encrypted at build time and decrypted on first use at runtime.
 *
 * Literals sit inside functions on purpose: `const val` is inlined into callers (possibly outside
 * fogPackages), which would leak a plaintext copy.
 */
object ApiConfigs {

    // Endpoint URL
    fun baseUrl() = "https://api.example.com/v1/collect"

    // API key
    fun apiKey() = "demo-api-key-5f3a9c1e7b2d40"

    // Literal built from parts: each part is encrypted separately
    fun authHeader() = "Bearer " + "token-0123456789"
}
