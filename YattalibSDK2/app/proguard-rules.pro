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

# Manter classes públicas e métodos de entrada (entry points) para que possam ser acessados pelos aplicativos que usam a biblioteca
-keep class projeto.lib.yattalibsdk2.** { *; }

# Manter classes Android padrão
-keep class android.** { *; }

# Mantém classes e métodos usados pelo sistema de reflexão (Reflection API)
-keepclassmembers class ** {
    *;
}

# Se sua biblioteca utiliza serialization ou reflection
-keepattributes Signature
-keepattributes *Annotation*


# Opcional: Mantém classes usadas para debug (caso deseje manter durante o desenvolvimento)
-keep class **.BuildConfig { *; }

