package com.sign3.appvaultintegration

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sign3.appvaultintegration.config.ApiConfigs
import com.sign3.appvaultintegration.config.DetectionSignatures
import com.sign3.appvaultintegration.config.PublicStrings

private const val TAG = "AppVaultIntegration"

/**
 * Shows each integration use case. On-screen text (and logcat tag "AppVaultIntegration") is the
 * original plaintext, proving app-vault decrypted it on-device. Inspect the APK to confirm the
 * obfuscated strings are NOT readable there.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(Modifier.fillMaxSize()) { DemoScreen() }
            }
        }
    }
}

@Composable
private fun DemoScreen() {
    Column(
        Modifier
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
    ) {
        // Obfuscated in the APK, decrypted transparently when accessed
        Section("Obfuscated: API secrets", listOf(
            ApiConfigs.baseUrl(), ApiConfigs.apiKey(), ApiConfigs.authHeader(),
        ))
        Section("Obfuscated: detection signatures", DetectionSignatures.suspiciousPaths())
        // Excluded via @IgnoreAppVault, so plaintext in the APK
        Section("Excluded (@IgnoreAppVault): plaintext", listOf(PublicStrings.appTitle()))
    }
}

// A titled group of values; each value is also logged
@Composable
private fun Section(title: String, values: List<String>) {
    Text(title, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 16.dp))
    values.forEach {
        Log.i(TAG, it)
        Text("• $it", style = MaterialTheme.typography.bodyMedium)
    }
}
