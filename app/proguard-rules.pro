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

# Keep BottomSheetDragHandleView if it's being removed by ProGuard/R8
-keep public class com.google.android.material.bottomsheet.BottomSheetDragHandleView {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public *;
}

# Also ensure its parent, BottomSheetBehavior, isn't being stripped if it's causing related issues
-keep public class com.google.android.material.bottomsheet.BottomSheetBehavior { *; }
-keep public class * extends com.google.android.material.bottomsheet.BottomSheetBehavior { *; }
-keepclassmembers public class * extends com.google.android.material.bottomsheet.BottomSheetBehavior {
   public <fields>;
   public <methods>;
}

# General Material Components rules (you might already have these, but ensure they are present)
-keep public class com.google.android.material.** { public <fields>; public <methods>; }
-keepclassmembers public class com.google.android.material.** {
    void set*(***);
    void add*(***);
    void remove*(***);
}
