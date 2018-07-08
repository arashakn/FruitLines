# To enable ProGuard in your project, edit project.properties
# to define the proguard.config property as described in that file.
#
# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in ${sdk.dir}/tools/proguard/proguard-rules.pro
# You can edit the include path and order by changing the ProGuard
# include property in project.properties.
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
-dontwarn android.webkit.JavascriptInterface
-keep class com.google.ads.** {*;}
-dontwarn com.google.ads.**
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
-keep class com.searchboxsdk.** {
*;
}
-keep class com.startapp.android.eula.** {
*;
}
-keep class com.startapp.** {
*;
}
-keepattributes Exceptions, InnerClasses, Signature, Deprecated,  SourceFile,LineNumberTable, *Annotation*, EnclosingMethod
-dontwarn android.webkit.JavascriptInterface
-dontwarn com.searchboxsdk.android.**
-dontwarn com.startapp.**
-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
    public static final *** NULL;
}

-keepnames class * implements android.os.Parcelable
-keepclassmembers class * implements android.os.Parcelable {
  public static final *** CREATOR;
}

-keep @interface android.support.annotation.Keep
-keep @android.support.annotation.Keep class *
-keepclasseswithmembers class * {
  @android.support.annotation.Keep <fields>;
}
-keepclasseswithmembers class * {
  @android.support.annotation.Keep <methods>;
}

-keep @interface com.google.android.gms.common.annotation.KeepName
-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
  @com.google.android.gms.common.annotation.KeepName *;
}

-keep @interface com.google.android.gms.common.util.DynamiteApi
-keep public @com.google.android.gms.common.util.DynamiteApi class * {
  public <fields>;
  public <methods>;
}

-dontwarn android.security.NetworkSecurityPolicy
-dontwarn okio.**
-keep class okio.** {*;}
-dontwarn com.alexvasilkov.**
-dontwarn android.graphics.**
-keep class com.bumptech.glide.** {*;}
-keep class com.alexvasilkov.** {*;}
-keep class org.sqlite.** { *; }
-keep class org.sqlite.database.** { *; }