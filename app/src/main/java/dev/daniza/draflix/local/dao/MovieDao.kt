package dev.daniza.draflix.local.dao

import androidx.room.Dao
import androidx.room.Query
import dev.daniza.draflix.local.entity.MovieEntity

@Dao
interface MovieDao {
    @Query("SELECT * FROM movie WHERE id = :id")
    fun getMovieById(id: String): MovieEntity?
}