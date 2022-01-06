package com.adammcneilly.toa.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [PersistableTask::class],
    version = 1,
    exportSchema = true,
)
abstract class TOADatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDAO
}
