package com.example.groww_mf_assignment.presentation.Explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groww_mf_assignment.Resource_Class
import com.example.groww_mf_assignment.data.local.ExploreCacheEntity
import com.example.groww_mf_assignment.domain.Repository.ExploreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val repository: ExploreRepository
) : ViewModel() {

    private val _indexFunds = MutableStateFlow<Resource_Class<List<ExploreCacheEntity>>>(Resource_Class.Loading())
    val indexFunds: StateFlow<Resource_Class<List<ExploreCacheEntity>>> = _indexFunds.asStateFlow()

    private val _bluechipFunds = MutableStateFlow<Resource_Class<List<ExploreCacheEntity>>>(Resource_Class.Loading())
    val bluechipFunds = _bluechipFunds.asStateFlow()

    private val _taxSaverFunds = MutableStateFlow<Resource_Class<List<ExploreCacheEntity>>>(Resource_Class.Loading())
    val taxSaverFunds = _taxSaverFunds.asStateFlow()

    private val _largeCapFunds = MutableStateFlow<Resource_Class<List<ExploreCacheEntity>>>(Resource_Class.Loading())
    val largeCapFunds = _largeCapFunds.asStateFlow()

    init {
        fetchCategory("Index Funds", "index", _indexFunds)
        fetchCategory("Bluechip Funds", "bluechip", _bluechipFunds)
        fetchCategory("Tax Saver", "elss", _taxSaverFunds)
        fetchCategory("Large Cap", "large cap", _largeCapFunds)
    }

    private fun fetchCategory(
        categoryName: String,
        query: String,
        stateFlow: MutableStateFlow<Resource_Class<List<ExploreCacheEntity>>>
    ) {
        viewModelScope.launch {
            repository.getCategoryFunds(categoryName, query).collect { result ->
                stateFlow.value = result
            }
        }
    }
}