package com.example.groww_mf_assignment.data.local

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "explore_cache")
data class ExploreCacheEntity(
    @PrimaryKey val schemeCode: Int,
    val schemeName: String,
    val category: String,
    val latestNav: String? = null
)

@Entity(tableName = "watchlist_folders")
data class FolderEntity(
    @PrimaryKey(autoGenerate = true) val folderId: Int = 0,
    val folderName: String
)

@Entity(tableName = "saved_funds")
data class SavedFundEntity(
    @PrimaryKey val schemeCode: Int,
    val schemeName: String,
    val amcName: String,
    val latestNav: String
)

@Entity(
    tableName = "folder_fund_cross_ref",
    primaryKeys = ["folderId", "schemeCode"]
)
data class FolderFundCrossRef(
    val folderId: Int,
    val schemeCode: Int
)

data class FolderWithFunds(
    @Embedded val folder: FolderEntity,
    @Relation(
        parentColumn = "folderId",
        entityColumn = "schemeCode",
        associateBy = Junction(FolderFundCrossRef::class)
    )
    val funds: List<SavedFundEntity>
)