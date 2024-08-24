package dev.daniza.draflix.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.daniza.draflix.local.entity.SearchEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchDao {
    @Query("SELECT * FROM search")
    fun getAll(): List<SearchEntity>

    @Query("SELECT * FROM search WHERE type = :type OR title LIKE '%' || :title || '%'")
    fun getSearchByQuery(type: String = "", title: String = ""): Flow<List<SearchEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(search: List<SearchEntity>)
}