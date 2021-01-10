-keep class com.softartdev.notedelight.** { *; }
-keepclassmembers class com.softartdev.notedelight.** { *; }

-keep class kotlinx.coroutines.** { *; }
-keepclassmembers class kotlinx.coroutines.** { *; }

-keep class androidx.test.** { *; }
-keepclassmembers class androidx.test.** { *; }

-keepattributes *Annotation*

-dontobfuscate
-dontoptimize
-dontwarn

# The Android pre-handler for exceptions is loaded reflectively (via ServiceLoader).
-keep class kotlinx.coroutines.experimental.android.AndroidExceptionPreHandler { *; }

-keep class kotlinx.coroutines.internal.MainDispatcherFactory { *; }
-keepclassmembers class kotlinx.coroutines.internal.MainDispatcherFactory { *; }

-keep class org.koin.java.KoinJavaComponent { *; }
-keepclassmembers class org.koin.java.KoinJavaComponent { *; }