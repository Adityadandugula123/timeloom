package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.SessionEntity
import com.example.data.model.TaskEntity

@Database(
    entities = [TaskEntity::class, SessionEntity::class],
    version = 1,
    exportSchema = false
)
abstract class TimeLoomDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun sessionDao(): SessionDao

    companion object {
        @Volatile
        private var INSTANCE: TimeLoomDatabase? = null

        fun getDatabase(context: Context): TimeLoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TimeLoomDatabase::class.java,
                    "time_loom_database"
                )
                    .fallbackToDestructiveMigration(true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
