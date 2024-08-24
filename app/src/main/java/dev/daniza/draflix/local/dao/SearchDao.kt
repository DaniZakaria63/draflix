package dev.daniza.draflix.local.dao

import androidx.room.Dao
import androidx.room.Query
import dev.daniza.draflix.local.entity.SearchEntity

@Dao
interface SearchDao {
    @Query("SELECT * FROM search")
    fun getAll(): List<SearchEntity>
}