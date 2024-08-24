package dev.daniza.draflix.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.daniza.draflix.local.entity.SearchEntity

@Dao
interface SearchDao {
    @Query("SELECT * FROM search ORDER BY page")
    fun getAll(): PagingSource<Int, SearchEntity>

    @Query("SELECT * FROM search WHERE type = :type OR title LIKE :title")
    fun getSearchByQuery(type: String = "", title: String = ""): PagingSource<Int, SearchEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(search: List<SearchEntity>)

    @Query("DELETE FROM search")
    fun clearSearch()
}