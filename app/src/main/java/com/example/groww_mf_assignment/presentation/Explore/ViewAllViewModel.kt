package com.example.groww_mf_assignment.presentation.Explore

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groww_mf_assignment.Resource_Class
import com.example.groww_mf_assignment.data.remote.MfApiService
import com.example.groww_mf_assignment.data.remote.dto.SearchResponseDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewAllViewModel @Inject constructor(
    private val api: MfApiService,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val categoryTitle: String = checkNotNull(savedStateHandle["category"])
    private val query: String = checkNotNull(savedStateHandle["query"])

    private val _funds = MutableStateFlow<Resource_Class<List<SearchResponseDto>>>(Resource_Class.Loading())
    val funds = _funds.asStateFlow()

    init {
        fetchFunds()
    }

    private fun fetchFunds() {
        viewModelScope.launch {
            _funds.value = Resource_Class.Loading()
            try {
                val result = api.searchFunds(query)
                _funds.value = Resource_Class.Success(result)
            } catch (e: Exception) {
                _funds.value = Resource_Class.Error(e.message ?: "Failed to fetch funds")
            }
        }
    }
}