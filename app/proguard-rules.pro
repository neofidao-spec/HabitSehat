# HabitSehat ProGuard / R8 Rules
# Target: < 5MB APK with full functionality

# Keep data models (Room entities)
-keep class com.habitsehat.app.data.model.** { *; }

# Keep Compose runtime
-keep class androidx.compose.** { *; }

# Keep Kotlin coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# Keep WorkManager
-keep class androidx.work.** { *; }

# Keep widget provider (launched by system)
-keep class com.habitsehat.app.ui.widgets.** { *; }

# Remove debug logging
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
}

# General optimizations
-optimizationpasses 5
-allowaccessmodification
-overloadaggressively
-repackageclasses 'hab'
-mergeinterfacesaggressively

# Keep R (resources)
-keep class com.habitsehat.app.R$* { *; }
-keep class com.habitsehat.app.R { *; }
