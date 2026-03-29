package com.sanson.digitaldetox.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sanson.digitaldetox.data.db.dao.CustomMessageDao
import com.sanson.digitaldetox.data.db.dao.MonitoredAppDao
import com.sanson.digitaldetox.data.db.dao.UsageLogDao
import com.sanson.digitaldetox.data.db.entity.CustomMessageEntity
import com.sanson.digitaldetox.data.db.entity.MonitoredAppEntity
import com.sanson.digitaldetox.data.db.entity.UsageLogEntity

@Database(
    entities = [
        MonitoredAppEntity::class,
        UsageLogEntity::class,
        CustomMessageEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun monitoredAppDao(): MonitoredAppDao
    abstract fun usageLogDao(): UsageLogDao
    abstract fun customMessageDao(): CustomMessageDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "digital_detox_db"
                )
                    .fallbackToDestructiveMigration(true)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
