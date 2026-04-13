package com.example.groww_mf_assignment.presentation.WatchList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groww_mf_assignment.data.local.FolderEntity
import com.example.groww_mf_assignment.data.local.FolderWithFunds
import com.example.groww_mf_assignment.data.local.SavedFundEntity
import com.example.groww_mf_assignment.domain.Repository.WatchlistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WatchlistViewModel @Inject constructor(
    private val repository: WatchlistRepository
) : ViewModel() {

    val watchlists: StateFlow<List<FolderWithFunds>> = repository.getFoldersWithFunds()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val allFolders: StateFlow<List<FolderEntity>> = repository.getWatchlistFolders()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun createNewFolder(name: String) {
        viewModelScope.launch {
            repository.createFolder(name)
        }
    }

    fun addFundToFolder(folderId: Int, fund: SavedFundEntity) {
        viewModelScope.launch {
            repository.addFundToFolder(folderId, fund)
        }
    }

    fun removeFundFromFolder(folderId: Int, schemeCode: Int) {
        viewModelScope.launch {
            repository.removeFundFromFolder(folderId, schemeCode)
        }
    }

    fun deleteFolder(folderId: Int) {
        viewModelScope.launch {
            repository.deleteFolder(folderId)
        }
    }
}