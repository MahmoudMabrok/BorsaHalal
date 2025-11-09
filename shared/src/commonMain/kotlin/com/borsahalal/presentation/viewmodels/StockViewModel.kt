package com.borsahalal.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.borsahalal.data.database.entities.Stock
import com.borsahalal.data.repository.ProfileRepository
import com.borsahalal.data.repository.StockRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class StockViewModel(
    private val stockRepository: StockRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val activeProfile = profileRepository.getActiveProfileFlow()

    val stocks: StateFlow<List<Stock>> = activeProfile
        .flatMapLatest { profile ->
            if (profile != null) {
                stockRepository.getStocksByProfile(profile.id)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedStock = MutableStateFlow<Stock?>(null)
    val selectedStock: StateFlow<Stock?> = _selectedStock.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun createStock(
        prefix: String,
        name: String,
        zakatPercentage: Double = 2.5,
        notes: String? = null
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                 activeProfile.collect { profile ->
                    stockRepository.createStock(
                        profileId = profile?.id ?: 1,
                        prefix = prefix,
                        name = name,
                        zakatPercentage = zakatPercentage,
                        notes = notes
                    )
                }


            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateStock(stock: Stock) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                stockRepository.updateStock(stock)
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteStock(stock: Stock) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                stockRepository.deleteStock(stock)
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun selectStock(stockId: Long) {
        viewModelScope.launch {
            _selectedStock.value = stockRepository.getStockById(stockId)
        }
    }

    fun clearSelection() {
        _selectedStock.value = null
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun clearError() {
        _error.value = null
    }
}
