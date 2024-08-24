package dev.daniza.draflix.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.daniza.draflix.local.entity.MovieEntity

@Dao
interface MovieDao {
    @Query("SELECT * FROM movie WHERE id = :id")
    fun getMovieById(id: String): MovieEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(movie: MovieEntity)
}