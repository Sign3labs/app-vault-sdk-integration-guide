# NOTE: no -keep for the generated <namespace>.StringFog dispatcher. It is referenced by direct
# invokestatic from the fogged classes, so R8 keeps it (reachable) and renames it consistently -- no
# manual keep needed. Verified by a minified release build that decrypts correctly on-device.

# The app-vault cipher + key material only need to survive shrinking; allow R8 to rename them so they
# are not readable as clear names in the release APK.
-keep,allowobfuscation class com.sign3.appvault.** { *; }
