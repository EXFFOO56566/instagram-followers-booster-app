-ignorewarnings

-keep class **$Properties
-keepattributes InnerClasses
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-dontpreverify
-verbose

-printmapping mapping.txt
-keepattributes *Annotation*
-keepattributes Signature

-keep class com.android.vending.billing.**

#Maintain enums
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep class * {
    public enum **;
}

#To keep parcelable classes (to serialize - deserialize objects to sent through Intents)
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

#Uncomment if using Serializable
-keepclassmembers class * implements java.io.Serializable {
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# class javax
-keep class javax.** { *; }
-keep class android.support.** {*;}
-keep class android.**{*;}
-keep class org.codeaurora.**{*;}
-keep class mediatek.**{*;}
-keep class com.google.**{*;}
-keep interface com.android.internal.**{*;}
-keep class com.android.internal.**{*;}
-keep class org.codeaurora.**{*;}

-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

# Preserve all native method names and the names of their classes.
-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keep class okhttp3.** { *; }

-keep class * extends com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule {
 <init>(...);
}
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-keep class com.bumptech.glide.load.data.ParcelFileDescriptorRewinder$InternalRewinder {
  *** rewind();
}

# Uncomment for DexGuard only
#-keepresourcexmlelements manifest/application/meta-data@value=GlideModule

-keep class com.lxquyen.instabooster.data.model.**
