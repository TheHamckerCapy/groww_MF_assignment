package com.example.groww_mf_assignment.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface MfDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExploreCache(funds: List<ExploreCacheEntity>)

    @Query("SELECT * FROM explore_cache WHERE category = :category")
    suspend fun getExploreCacheByCategory(category: String): List<ExploreCacheEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFolder(folder: FolderEntity)

    @Query("SELECT * FROM watchlist_folders")
    fun getAllFolders(): Flow<List<FolderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedFund(fund: SavedFundEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFolderFundCrossRef(crossRef: FolderFundCrossRef)

    @Delete
    suspend fun deleteFolderFundCrossRef(crossRef: FolderFundCrossRef)

    @Transaction
    @Query("SELECT * FROM watchlist_folders")
    fun getFoldersWithFunds(): Flow<List<FolderWithFunds>>

    @Query("SELECT EXISTS(SELECT 1 FROM folder_fund_cross_ref WHERE schemeCode = :schemeCode)")
    fun isFundSaved(schemeCode: Int): Flow<Boolean>


    @Query("DELETE FROM explore_cache WHERE category = :category")
    suspend fun deleteExploreCacheByCategory(category: String)

    @Query("DELETE FROM folder_fund_cross_ref WHERE folderId = :folderId")
    suspend fun clearFolderAssociations(folderId: Int)

    @Query("DELETE FROM watchlist_folders WHERE folderId = :folderId")
    suspend fun deleteFolderEntity(folderId: Int)

    // A transaction ensures both tables are updated safely at the same time
    @Transaction
    suspend fun deleteWatchlistFolder(folderId: Int) {
        clearFolderAssociations(folderId)
        deleteFolderEntity(folderId)
    }
}