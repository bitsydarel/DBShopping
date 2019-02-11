# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/darel/Android/Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class nickname to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file nickname.
#-renamesourcefileattribute SourceFile
# Add this global rule
-optimizationpasses 5
-optimizations !class/unboxing/enum
-verbose

-keepattributes Signature

# This rule will properly ProGuard all the model classes in
# the package com.yourcompany.models. Modify to fit the structure
# of your app.

-keepclassmembers class com.dbeginc.data.proxies.local.** {
  *;
}

-keepclassmembers class com.dbeginc.data.proxies.remote.** {
  *;
}

-keep public class com.dbeginc.data.implementations.repositories.**

-dontwarn javax.annotation.**
