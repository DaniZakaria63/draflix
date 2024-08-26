package dev.daniza.draflix.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movie")
data class MovieEntity(
    @PrimaryKey(autoGenerate = false) val id: String,
    @ColumnInfo(name = "title") val title: String = "",
    @ColumnInfo(name = "year") val year: String = "",
    @ColumnInfo(name = "released") val released: String = "",
    @ColumnInfo(name = "runtime") val runtime: String = "",
    @ColumnInfo(name = "genre") val genre: String = "",
    @ColumnInfo(name = "poster") val poster: String = "",
    @ColumnInfo(name = "director") val director: String = "",
    @ColumnInfo(name = "writer") val writer: String = "",
    @ColumnInfo(name = "actor") val actor: String = "",
    @ColumnInfo(name = "plot") val plot: String = "",
    @ColumnInfo(name = "rating") val rating: String = "",
    @ColumnInfo(name = "created_date") val createdDate: Long = 0L,
)