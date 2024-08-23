package dev.daniza.draflix.local

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import dev.daniza.draflix.utilities.LOCAL_DATABASE_NAME

abstract class DraflixDatabase : RoomDatabase() {
    companion object {

        // For Singleton instantiation
        @Volatile
        private var instance: DraflixDatabase? = null

        fun getInstance(context: Context): DraflixDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): DraflixDatabase {
            return Room.databaseBuilder(context, DraflixDatabase::class.java, LOCAL_DATABASE_NAME)
                .build()
        }
    }
}