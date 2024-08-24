package dev.daniza.draflix.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import dev.daniza.draflix.local.dao.MovieDao
import dev.daniza.draflix.local.dao.RemoteKeysDao
import dev.daniza.draflix.local.dao.SearchDao
import dev.daniza.draflix.local.entity.MovieEntity
import dev.daniza.draflix.local.entity.SearchEntity
import dev.daniza.draflix.utilities.LOCAL_DATABASE_NAME

@Database(entities = [MovieEntity::class, SearchEntity::class], version = 1)
abstract class DraflixDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
    abstract fun searchDao(): SearchDao
    abstract fun remoteKeysDao(): RemoteKeysDao

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