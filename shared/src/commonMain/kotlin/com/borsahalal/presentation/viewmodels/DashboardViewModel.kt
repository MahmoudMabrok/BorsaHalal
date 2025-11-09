package com.borsahalal.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.borsahalal.data.repository.ProfileRepository
import com.borsahalal.data.repository.ReportRepository
import com.borsahalal.data.repository.StockRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DashboardState(
    val totalPortfolioValue: Double = 0.0,
    val totalInvested: Double = 0.0,
    val totalRealizedProfit: Double = 0.0,
    val totalUnrealizedProfit: Double = 0.0,
    val totalProfit: Double = 0.0,
    val returnPercentage: Double = 0.0,
    val totalZakatDue: Double = 0.0,
    val stockCount: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)

class DashboardViewModel(
    private val profileRepository: ProfileRepository,
    private val stockRepository: StockRepository,
    private val reportRepository: ReportRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state.asStateFlow()

    init {
        loadDashboardData()
    }

    fun loadDashboardData(currentPrices: Map<Long, Double> = emptyMap()) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val activeProfile = profileRepository.getActiveProfile()
                if (activeProfile != null) {
                    val realizedProfit = reportRepository.getTotalRealizedProfit(activeProfile.id)
                    val unrealizedProfit = reportRepository.getTotalUnrealizedProfit(
                        activeProfile.id,
                        currentPrices
                    )
                    val portfolioValue = reportRepository.getPortfolioValue(
                        activeProfile.id,
                        currentPrices
                    )
                    val zakatDue = reportRepository.getTotalZakatDue(
                        activeProfile.id,
                        currentPrices
                    )
                    val stockCount = stockRepository.getStockCount(activeProfile.id)
                    val totalInvested = reportRepository.getTotalInvested(activeProfile.id)
                    val totalProfit = realizedProfit + unrealizedProfit
                    val returnPercentage = if (totalInvested > 0) {
                        (totalProfit / totalInvested) * 100
                    } else {
                        0.0
                    }

                    _state.value = DashboardState(
                        totalPortfolioValue = portfolioValue,
                        totalInvested = totalInvested,
                        totalRealizedProfit = realizedProfit,
                        totalUnrealizedProfit = unrealizedProfit,
                        totalProfit = totalProfit,
                        returnPercentage = returnPercentage,
                        totalZakatDue = zakatDue,
                        stockCount = stockCount,
                        isLoading = false
                    )
                } else {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = "No active profile found"
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
}
