package dev.daniza.draflix.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.daniza.draflix.local.entity.RemoteKeysEntity

@Dao
interface RemoteKeysDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<RemoteKeysEntity>)

    @Query("SELECT * FROM remote_keys WHERE movie_id = :movieId")
    suspend fun getRemoteKeyByMovieId(movieId: String): RemoteKeysEntity?

    @Query("DELETE FROM remote_keys")
    suspend fun clearRemoteKeys()

    @Query("SELECT created_at FROM remote_keys ORDER BY created_at DESC LIMIT 1")
    suspend fun getCreationTime(): Long?

}