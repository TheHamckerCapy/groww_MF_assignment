package com.example.groww_mf_assignment.domain.Repository

import com.example.groww_mf_assignment.data.local.FolderEntity
import com.example.groww_mf_assignment.data.local.FolderFundCrossRef
import com.example.groww_mf_assignment.data.local.FolderWithFunds
import com.example.groww_mf_assignment.data.local.MfDao
import com.example.groww_mf_assignment.data.local.SavedFundEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WatchlistRepository @Inject constructor(
    private val dao: MfDao
) {
    fun getWatchlistFolders(): Flow<List<FolderEntity>> {
        return dao.getAllFolders()
    }

    fun getFoldersWithFunds(): Flow<List<FolderWithFunds>> {
        return dao.getFoldersWithFunds()
    }

    fun isFundSaved(schemeCode: Int): Flow<Boolean> {
        return dao.isFundSaved(schemeCode)
    }

    suspend fun createFolder(folderName: String) {
        dao.insertFolder(FolderEntity(folderName = folderName))
    }

    suspend fun addFundToFolder(folderId: Int, fund: SavedFundEntity) {
        dao.insertSavedFund(fund)
        dao.insertFolderFundCrossRef(FolderFundCrossRef(folderId, fund.schemeCode))
    }

    suspend fun removeFundFromFolder(folderId: Int, schemeCode: Int) {
        dao.deleteFolderFundCrossRef(FolderFundCrossRef(folderId, schemeCode))
    }

    suspend fun deleteFolder(folderId: Int) {
        dao.deleteWatchlistFolder(folderId)
    }
}