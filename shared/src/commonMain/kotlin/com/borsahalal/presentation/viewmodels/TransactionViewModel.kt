package com.borsahalal.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.borsahalal.data.database.entities.Transaction
import com.borsahalal.data.repository.ProfileRepository
import com.borsahalal.data.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TransactionViewModel(
    private val transactionRepository: TransactionRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val activeProfile = profileRepository.getActiveProfileFlow()

    val transactions: StateFlow<List<Transaction>> = activeProfile
        .flatMapLatest { profile ->
            if (profile != null) {
                transactionRepository.getTransactionsByProfile(profile.id)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedTransaction = MutableStateFlow<Transaction?>(null)
    val selectedTransaction: StateFlow<Transaction?> = _selectedTransaction.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun createBuyTransaction(
        stockId: Long,
        quantity: Double,
        pricePerUnit: Double,
        commission: Double = 0.0,
        date: Long = System.currentTimeMillis(),
        notes: String? = null
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                transactionRepository.createBuyTransaction(
                    stockId = stockId,
                    quantity = quantity,
                    pricePerUnit = pricePerUnit,
                    commission = commission,
                    date = date,
                    notes = notes
                )
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createSellTransaction(
        stockId: Long,
        quantity: Double,
        pricePerUnit: Double,
        commission: Double = 0.0,
        date: Long = System.currentTimeMillis(),
        notes: String? = null
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                transactionRepository.createSellTransaction(
                    stockId = stockId,
                    quantity = quantity,
                    pricePerUnit = pricePerUnit,
                    commission = commission,
                    date = date,
                    notes = notes
                )
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteTransaction(transactionId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                transactionRepository.deleteTransaction(transactionId)
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun selectTransaction(transactionId: Long) {
        viewModelScope.launch {
            _selectedTransaction.value = transactionRepository.getTransactionById(transactionId)
        }
    }

    fun clearSelection() {
        _selectedTransaction.value = null
    }

    fun clearError() {
        _error.value = null
    }
}
