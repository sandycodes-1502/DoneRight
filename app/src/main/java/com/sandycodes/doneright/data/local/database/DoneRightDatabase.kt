package com.sandycodes.doneright.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sandycodes.doneright.data.local.Dao.TaskDao
import com.sandycodes.doneright.data.local.Entity.TaskEntity

@Database(entities = [TaskEntity::class], version = 1, exportSchema = false)
@TypeConverters(TaskStatusConverter::class)

abstract class DoneRightDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var INSTANCE: DoneRightDatabase? = null

        fun getInstance(context: Context): DoneRightDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    DoneRightDatabase::class.java,
                    "doneright_db"
                ).build().also { INSTANCE = it }
            }
    }
}