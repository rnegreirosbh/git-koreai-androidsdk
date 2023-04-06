# Lifecycle Runtime
-keepattributes *Annotation*
-keepclassmembers enum androidx.lifecycle.Lifecycle$Event {
    <fields>;
}
-keep !interface * implements androidx.lifecycle.LifecycleObserver {
}
-keep class * implements androidx.lifecycle.GeneratedAdapter {
    <init>(...);
}
-keepclassmembers class ** {
    @androidx.lifecycle.OnLifecycleEvent *;
}
# this rule is need to work properly when app is compiled with api 28, see b/142778206
# Also this rule prevents registerIn from being inlined.
-keepclassmembers class androidx.lifecycle.ReportFragment$LifecycleCallbacks { *; }

# Core-ktx
# Never inline methods, but allow shrinking and obfuscation.
-keepclassmembernames,allowobfuscation,allowshrinking class androidx.core.view.ViewCompat$Api* {
  <methods>;
}
-keepclassmembernames,allowobfuscation,allowshrinking class androidx.core.view.WindowInsetsCompat$*Impl* {
  <methods>;
}
-keepclassmembernames,allowobfuscation,allowshrinking class androidx.core.app.NotificationCompat$*$Api*Impl {
  <methods>;
}
-keepclassmembernames,allowobfuscation,allowshrinking class androidx.core.os.UserHandleCompat$Api*Impl {
  <methods>;
}
-keepclassmembernames,allowobfuscation,allowshrinking class androidx.core.widget.EdgeEffectCompat$Api*Impl {
  <methods>;
}

# ServiceLoader support
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepnames class kotlinx.coroutines.android.AndroidExceptionPreHandler {}
-keepnames class kotlinx.coroutines.android.AndroidDispatcherFactory {}

-keep class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keep class kotlinx.coroutines.CoroutineExceptionHandler {}
-keep class kotlinx.coroutines.android.AndroidExceptionPreHandler {}
-keep class kotlinx.coroutines.android.AndroidDispatcherFactory {}

# Most of volatile fields are updated with AFU and should not be mangled
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

# JWT
-keepattributes InnerClasses

-keep class io.jsonwebtoken.** { *; }
-keepnames class io.jsonwebtoken.* { *; }
-keepnames interface io.jsonwebtoken.* { *; }

-keep class org.bouncycastle.** { *; }
-keepnames class org.bouncycastle.** { *; }
-dontwarn org.bouncycastle.**

# SDK
-keep class ai.kore.androidsdk.KoreAiSDK { *; }
-keepnames class ai.kore.androidsdk.KoreAiSDK { *; }

# Webview
-keep class * extends android.webkit.WebChromeClient { *; }
-dontwarn im.delight.android.webview.**