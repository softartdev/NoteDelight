# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/artur/Android/Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

#-keep class androidx.appcompat.widget.SearchView { *; }
#-keep class androidx.core.view.** { *; }

-dontobfuscate

-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable        # Keep file names and line numbers.
-keep public class * extends java.lang.Exception  # Optional: Keep custom exceptions.

-keep class com.google.firebase.crashlytics.* { *; }
-dontwarn com.google.firebase.crashlytics.**

-keep interface com.softartdev.notedelight.shared.database.NoteDao

-keepclassmembers class androidx.arch.core.executor.ArchTaskExecutor { *; }

-keepclassmembers class net.sqlcipher.CursorWindow { *; }
-keepclassmembers class net.sqlcipher.database.SQLiteDatabase { *; }
-keepclassmembers class net.sqlcipher.database.SQLiteCompiledSql { *; }
-keepclassmembers class net.sqlcipher.database.SQLiteQuery { *; }
-keepclassmembers class net.sqlcipher.database.SQLiteStatement { *; }

-keepclassmembers class kotlin.coroutines.jvm.internal.Boxing { *; }
-keepclassmembers class kotlinx.coroutines.Job { *; }
-keepclassmembers class kotlinx.coroutines.BuildersKt { *; }
-keepclassmembers class kotlinx.coroutines.Deferred { *; }
-keepclassmembers class kotlinx.coroutines.internal.MainDispatchersKt { *; }
-keep class * implements kotlinx.coroutines.internal.MainDispatcherFactory

-keepclassmembers class kotlin.collections.CollectionsKt { *; }
-keepclassmembers class kotlin.collections.SetsKt { *; }
-keep class * implements kotlin.sequences.Sequence

-keep class androidx.test.espresso.IdlingRegistry { *; }
-keepclassmembers class androidx.test.espresso.IdlingResource { *; }

