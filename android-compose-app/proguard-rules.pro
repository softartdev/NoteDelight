# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-keepattributes SourceFile,LineNumberTable        # Keep file names and line numbers.
-keep public class * extends java.lang.Exception  # Optional: Keep custom exceptions.

-keepclassmembers class net.sqlcipher.CursorWindow { *; }
-keepclassmembers class net.sqlcipher.database.SQLiteDatabase { *; }
-keepclassmembers class net.sqlcipher.database.SQLiteCompiledSql { *; }
-keepclassmembers class net.sqlcipher.database.SQLiteQuery { *; }
-keepclassmembers class net.sqlcipher.database.SQLiteStatement { *; }
-dontwarn net.sqlcipher.database.SQLiteException

-dontwarn kotlinx.serialization.KSerializer
-dontwarn kotlinx.serialization.Serializable