package dev.daniza.draflix.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "search")
data class SearchEntity(
    @PrimaryKey(autoGenerate = false) val id: String,
    @ColumnInfo(name = "title") val title: String = "",
    @ColumnInfo(name = "year") val year: String = "",
    @ColumnInfo(name = "type") val type: String = "",
    @ColumnInfo(name = "poster") val poster: String = "",
)