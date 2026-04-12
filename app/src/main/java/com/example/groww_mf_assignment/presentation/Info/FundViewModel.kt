package com.example.groww_mf_assignment.presentation.Info

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groww_mf_assignment.Resource_Class
import com.example.groww_mf_assignment.data.remote.dto.FundDetailsResponseDto
import com.example.groww_mf_assignment.domain.Repository.FundDetailsRepository
import com.example.groww_mf_assignment.domain.Repository.WatchlistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject



@HiltViewModel
class ProductViewModel @Inject constructor(
    private val detailsRepository: FundDetailsRepository,
    private val watchlistRepository: WatchlistRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val schemeCode: Int = checkNotNull(savedStateHandle["schemeCode"])

    private val _fundDetails = MutableStateFlow<Resource_Class<FundDetailsResponseDto>>(Resource_Class.Loading())
    val fundDetails: StateFlow<Resource_Class<FundDetailsResponseDto>> = _fundDetails.asStateFlow()

    val isSaved: StateFlow<Boolean> = watchlistRepository.isFundSaved(schemeCode)
        .stateIn(viewModelScope, SharingStarted.Lazily, false)

    init {
        fetchDetails()
    }

    private fun fetchDetails() {
        viewModelScope.launch {
            detailsRepository.getFundDetails(schemeCode).collect { result ->
                _fundDetails.value = result
            }
        }
    }
}