# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# Keep accessibility service
-keep class com.sanson.digitaldetox.service.DetoxAccessibilityService

# Keep data classes used by Room
-keep class com.sanson.digitaldetox.data.db.entity.** { *; }
-keep class com.sanson.digitaldetox.data.db.dao.** { *; }

# Coroutines
-dontwarn kotlinx.coroutines.**

# DataStore
-keep class androidx.datastore.** { *; }

# Keep line numbers for debugging
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
