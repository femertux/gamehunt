# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

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

# Keep Hilt/Dagger annotations and generated classes
-keep class dagger.** { *; }
-keep class javax.inject.** { *; }
-keep class androidx.hilt.** { *; }
-keep class dagger.hilt.** { *; }
-keep class com.google.dagger.** { *; }

# Prevent obfuscation of model classes used with Gson or Moshi
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Keep Retrofit classes and interfaces
-keep class retrofit2.** { *; }
-keep interface retrofit2.** { *; }

# Ignore warnings from OkHttp
-dontwarn okhttp3.**

# Support for Jetpack Compose
-keep class androidx.compose.** { *; }
-keep class androidx.activity.** { *; }

# Prevent obfuscation of @Parcelize classes
-keep class kotlinx.parcelize.** { *; }

# Common warning suppression
-dontwarn kotlinx.coroutines.**
-dontwarn org.intellij.lang.annotations.**