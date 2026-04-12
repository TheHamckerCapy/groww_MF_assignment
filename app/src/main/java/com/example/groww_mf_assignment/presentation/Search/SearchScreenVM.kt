package com.example.groww_mf_assignment.presentation.Search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groww_mf_assignment.Resource_Class
import com.example.groww_mf_assignment.data.remote.MfApiService
import com.example.groww_mf_assignment.data.remote.dto.SearchResponseDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val api: MfApiService
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<Resource_Class<List<SearchResponseDto>>>(Resource_Class.Loading())
    val searchResults = _searchResults.asStateFlow()


    private var defaultFundsCache: List<SearchResponseDto>? = null

    init {
        viewModelScope.launch {
            _searchQuery
                .debounce(400L)
                .distinctUntilChanged()
                .collectLatest { query ->
                    if (query.isBlank()) {
                        showDefaultFunds()
                    } else {
                        performSearch(query)
                    }
                }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    private suspend fun showDefaultFunds() {

        if (defaultFundsCache != null) {
            _searchResults.value = Resource_Class.Success(defaultFundsCache)
            return
        }

        _searchResults.value = Resource_Class.Loading()
        try {
            val results = api.getInitialFunds(limit = 100, offset = 0)
            defaultFundsCache = results
            _searchResults.value = Resource_Class.Success(results)
        } catch (e: Exception) {
            _searchResults.value = Resource_Class.Error("Failed to load initial funds.")
        }
    }

    private suspend fun performSearch(query: String) {
        _searchResults.value = Resource_Class.Loading()
        try {
            val results = api.searchFunds(query)
            if (results.isEmpty()) {
                _searchResults.value = Resource_Class.Error("No funds found for '$query'")
            } else {
                _searchResults.value = Resource_Class.Success(results)
            }
        } catch (e: Exception) {
            _searchResults.value = Resource_Class.Error("Network error. Try again.")
        }
    }
}