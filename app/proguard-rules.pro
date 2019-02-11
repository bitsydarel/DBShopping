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
-optimizationpasses 5
-verbose

-dontwarn com.google.errorprone.annotations.**
-dontwarn javax.annotation.**

-keep public class android.support.v7.widget.SearchView {
   public <init>(android.content.Context);
   public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclassmembers class com.dbeginc.domain.entities.user.FriendRequest {
    *;
}

-keepattributes Signature,RuntimeVisibleAnnotations,AnnotationDefault,Exceptions

-dontwarn com.zhihu.matisse.**

# https://github.com/square/okhttp/issues/2230
-dontwarn okhttp3.**
-dontnote okhttp3.**

# https://github.com/square/okio#proguard
-dontnote okio.**
-dontwarn okio.**
-dontwarn com.bea.xml.**

-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

-dontwarn com.squareup.picasso.**

-keepclassmembers class * implements java.io.Serializable {
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

-keepnames class com.facebook.FacebookActivity
-keepnames class com.facebook.CustomTabActivity

-keep class com.facebook.all.All

-keep public class com.android.vending.billing.IInAppBillingService {
    public static com.android.vending.billing.IInAppBillingService asInterface(android.os.IBinder);
    public android.os.Bundle getSkuDetails(int, java.lang.String, java.lang.String, android.os.Bundle);
}