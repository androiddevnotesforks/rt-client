package com.automotivecodelab.featurefavoritesimpl.data

import androidx.room.*
import java.util.*
import kotlinx.coroutines.flow.Flow

interface FavoritesDatabase {
    fun favoritesDao(): FavoritesDao
}

@Entity
data class FavoriteDatabaseModel(
    @PrimaryKey val torrentId: String,
    val threadId: String,
    val category: String,
    val author: String,
    val title: String,
    val url: String
)

@Dao
interface FavoritesDao {
    @Query("SELECT * FROM favoritedatabasemodel")
    fun observeFavorites(): Flow<List<FavoriteDatabaseModel>>

    @Insert
    suspend fun insertFavorite(vararg favoriteDatabaseModel: FavoriteDatabaseModel)

    @Delete
    suspend fun deleteFavorite(favoriteDatabaseModel: FavoriteDatabaseModel)
}
