# app-vault — Integration Guide for Android

**app-vault** is a compile-time **string-obfuscation** library for Android, offered by **Sign3**. It
encrypts the string constants in your code at build time and transparently decrypts them at runtime, so
sensitive literals — endpoints, keys, file paths, detection signatures — are not readable with a plain
`strings`/grep of the APK. You integrate through app-vault's own Gradle DSL, interface, and annotation.

---

## Step 1 — Configure the repository

app-vault is published to Sign3's JFrog Artifactory. Add the repository so both the **plugin classpath**
and the **runtime dependency** can be resolved. Collect the username/password from the credentials
document.

In `settings.gradle` (`dependencyResolutionManagement`) — for the runtime dependency:

```groovy
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }   // transitive dependencies
        maven {
            url "https://sign3.jfrog.io/artifactory/intelligence-generic-local/"
            credentials {
                username = "provided in credential doc"
                password = "provided in credential doc"
            }
        }
    }
}
```

The plugin lives on the **buildscript** classpath, so the same repository is also declared in the module's
`buildscript { }` block (see Step 2).

---

## Step 2 — Apply the plugin and add the dependency

In the **module** `build.gradle` (the app or library you want to obfuscate):

```groovy
buildscript {
    repositories {
        mavenCentral()
        maven {
            url "https://sign3.jfrog.io/artifactory/intelligence-generic-local/"
            credentials {
                username = "provided in credential doc"
                password = "provided in credential doc"
            }
        }
    }
    dependencies {
        classpath 'com.sign3.app-vault:app-vault-plugin:1.0.0'
    }
}

plugins {
    id 'com.android.application'   // or 'com.android.library'
}

apply plugin: 'appvault'

import com.sign3.appvault.AppVaultMode

appvault {
    implementation 'com.sign3.appvault.AppVaultCipher'   // default IAppVault cipher
    enable true
    fogPackages = ['com.your.app.package']               // only these package prefixes are obfuscated
    mode AppVaultMode.base64
}

dependencies {
    // Needed at RUNTIME to decrypt; ships in your APK.
    implementation 'com.sign3.app-vault:app-vault:1.0.0'
}
```

Sync your project with Gradle after adding this.

### `appvault { }` options

| Option | Type | Default | Description |
|---|---|---|---|
| `implementation` | String (FQN) | `com.sign3.appvault.AppVaultCipher` | Fully-qualified name of an `IAppVault` implementation. Defaults to app-vault's built-in cipher. |
| `enable` | boolean | `true` | Master switch for obfuscation. |
| `fogPackages` | String[] | `[]` | Package **prefixes** to obfuscate. Anything outside them is left untouched. **Set this** — an empty list obfuscates nothing (a build warning is logged). |
| `mode` | `AppVaultMode` | `base64` | How the ciphertext is embedded: `base64` (opaque string constant, safest default) or `bytes` (raw byte array, slightly smaller). |

---

## Step 3 — Warm up at startup (recommended)

Key derivation is a one-time, CPU-heavy step that runs **once per process** and is cached. Warming it up on a
background thread during app start means the first obfuscated-string access is a cache hit instead of a
one-time delay. `warmUp()` is best-effort and **never throws**.

**Kotlin**
```kotlin
import com.sign3.appvault.AppVaultCipher

// e.g. in Application.onCreate(), off the main thread
CoroutineScope(Dispatchers.Default).launch {
    AppVaultCipher.warmUp()
}
```

**Java**
```java
import com.sign3.appvault.AppVaultCipher;

new Thread(AppVaultCipher::warmUp).start();
```

---

## Step 4 — Exclude classes that must not be obfuscated (`@IgnoreAppVault`)

Some classes must keep their string constants **plaintext** — most importantly anything that runs *before*
or *underneath* the obfuscation layer (e.g. your crypto core, which otherwise would need the decryptor in
order to decrypt its own key material). Annotate such a class with `@IgnoreAppVault`:

**Java**
```java
import com.sign3.appvault.IgnoreAppVault;

@IgnoreAppVault
public final class CryptoCore {
    // string literals here stay as-is
}
```

**Kotlin**
```kotlin
import com.sign3.appvault.IgnoreAppVault

@IgnoreAppVault
class CryptoCore { /* ... */ }
```
