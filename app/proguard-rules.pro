# Add project specific ProGuard rules here.
-keep class * extends androidx.compose.runtime.Composable
-keepclassmembers class * {
    @androidx.compose.runtime.Composable *;
}
