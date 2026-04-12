package com.example.groww_mf_assignment.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        ExploreCacheEntity::class,
        FolderEntity::class,
        SavedFundEntity::class,
        FolderFundCrossRef::class
    ],
    version = 1,
    exportSchema = false
)
abstract class MfDatabase : RoomDatabase() {
    abstract val mfDao: MfDao
}