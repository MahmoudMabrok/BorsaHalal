package com.borsahalal.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.borsahalal.data.database.dao.SaleAllocationDao
import com.borsahalal.data.database.dao.StockHoldingDao
import com.borsahalal.data.database.entities.SaleAllocation
import com.borsahalal.data.database.entities.StockHolding
import com.borsahalal.data.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class StockSummary(
    val totalShares: Double,
    val averagePrice: Double,
    val currentValue: Double,
    val realizedProfit: Double,
    val unrealizedProfit: Double
)

class HoldingsViewModel(
    private val stockHoldingDao: StockHoldingDao,
    private val saleAllocationDao: SaleAllocationDao,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _selectedStockId = MutableStateFlow<Long?>(null)
    val selectedStockId: StateFlow<Long?> = _selectedStockId.asStateFlow()

    private val _holdings = MutableStateFlow<List<StockHolding>>(emptyList())
    val holdings: StateFlow<List<StockHolding>> = _holdings.asStateFlow()

    private val _allocations = MutableStateFlow<List<SaleAllocation>>(emptyList())
    val allocations: StateFlow<List<SaleAllocation>> = _allocations.asStateFlow()

    private val _stockSummary = MutableStateFlow<StockSummary?>(null)
    val stockSummary: StateFlow<StockSummary?> = _stockSummary.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadStockHoldings(stockId: Long) {
        _selectedStockId.value = stockId
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Load active holdings
                stockHoldingDao.getActiveHoldingsFlow(stockId)
                    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
                    .collect { holdingsList ->
                        _holdings.value = holdingsList
                    }

                // Load sale allocations
                saleAllocationDao.getAllocationsByStock(stockId)
                    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
                    .collect { allocationsList ->
                        _allocations.value = allocationsList
                    }

                // Calculate summary
                calculateStockSummary(stockId)
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun calculateStockSummary(stockId: Long) {
        viewModelScope.launch {
            val totalShares = stockHoldingDao.getTotalRemainingQuantity(stockId) ?: 0.0
            val averagePrice = stockHoldingDao.getAverageBuyPrice(stockId) ?: 0.0
            val currentValue = totalShares * averagePrice
            val realizedProfit = saleAllocationDao.getTotalProfitForStock(stockId) ?: 0.0

            // For unrealized profit, we'd need current market price
            // For now, we'll set it to 0 or calculate based on average price
            val unrealizedProfit = 0.0

            _stockSummary.value = StockSummary(
                totalShares = totalShares,
                averagePrice = averagePrice,
                currentValue = currentValue,
                realizedProfit = realizedProfit,
                unrealizedProfit = unrealizedProfit
            )
        }
    }

    fun refreshData() {
        _selectedStockId.value?.let { stockId ->
            loadStockHoldings(stockId)
        }
    }
}
