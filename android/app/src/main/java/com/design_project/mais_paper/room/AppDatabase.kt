package com.design_project.mais_paper.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [PaperCycle::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun paperCycleDao(): PaperCycleDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "paper-db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

