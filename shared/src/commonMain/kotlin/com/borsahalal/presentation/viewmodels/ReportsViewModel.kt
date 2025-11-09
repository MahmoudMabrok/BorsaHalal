package com.borsahalal.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.borsahalal.data.database.dao.SaleAllocationDao
import com.borsahalal.data.database.dao.StockHoldingDao
import com.borsahalal.data.repository.ProfileRepository
import com.borsahalal.data.repository.ReportRepository
import com.borsahalal.data.repository.StockRepository
import com.borsahalal.data.repository.TransactionRepository
import com.borsahalal.presentation.ui.components.DateRange
import com.borsahalal.presentation.ui.components.DateRangeOption
import com.borsahalal.utils.CsvExporter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class ReportData(
    val csvContent: String = "",
    val reportType: String = "",
    val isGenerating: Boolean = false,
    val error: String? = null
)

class ReportsViewModel(
    private val profileRepository: ProfileRepository,
    private val stockRepository: StockRepository,
    private val transactionRepository: TransactionRepository,
    private val reportRepository: ReportRepository,
    private val stockHoldingDao: StockHoldingDao,
    private val saleAllocationDao: SaleAllocationDao
) : ViewModel() {

    private val _reportData = MutableStateFlow(ReportData())
    val reportData: StateFlow<ReportData> = _reportData.asStateFlow()

    private val _selectedDateRange = MutableStateFlow(DateRangeOption.ALL_TIME)
    val selectedDateRange: StateFlow<DateRangeOption> = _selectedDateRange.asStateFlow()

    fun updateDateRange(option: DateRangeOption) {
        _selectedDateRange.value = option
    }

    fun generateTransactionReport(dateRange: DateRange? = null) {
        viewModelScope.launch {
            _reportData.value = _reportData.value.copy(isGenerating = true, error = null)
            try {
                val activeProfile = profileRepository.getActiveProfile()
                if (activeProfile != null) {
                    val transactions = transactionRepository.getTransactionsByProfile(activeProfile.id).first()
                    val filteredTransactions = if (dateRange != null) {
                        transactions.filter { it.date >= dateRange.startDate && it.date <= dateRange.endDate }
                    } else {
                        transactions
                    }

                    val stocks = stockRepository.getStocksByProfile(activeProfile.id).first()
                    val stockMap = stocks.associateBy { it.id }

                    val csvContent = CsvExporter.exportTransactionsToCsv(filteredTransactions, stockMap)

                    _reportData.value = ReportData(
                        csvContent = csvContent,
                        reportType = "Transaction History",
                        isGenerating = false
                    )
                } else {
                    _reportData.value = _reportData.value.copy(
                        isGenerating = false,
                        error = "No active profile found"
                    )
                }
            } catch (e: Exception) {
                _reportData.value = _reportData.value.copy(
                    isGenerating = false,
                    error = e.message
                )
            }
        }
    }

    fun generateStocksReport() {
        viewModelScope.launch {
            _reportData.value = _reportData.value.copy(isGenerating = true, error = null)
            try {
                val activeProfile = profileRepository.getActiveProfile()
                if (activeProfile != null) {
                    val stocks = stockRepository.getStocksByProfile(activeProfile.id).first()

                    val holdingsMap = mutableMapOf<Long, Double>()
                    val avgPriceMap = mutableMapOf<Long, Double>()

                    for (stock in stocks) {
                        holdingsMap[stock.id] = stockHoldingDao.getTotalRemainingQuantity(stock.id) ?: 0.0
                        avgPriceMap[stock.id] = stockHoldingDao.getAverageBuyPrice(stock.id) ?: 0.0
                    }

                    val csvContent = CsvExporter.exportStocksToCsv(stocks, holdingsMap, avgPriceMap)

                    _reportData.value = ReportData(
                        csvContent = csvContent,
                        reportType = "Stock Holdings",
                        isGenerating = false
                    )
                } else {
                    _reportData.value = _reportData.value.copy(
                        isGenerating = false,
                        error = "No active profile found"
                    )
                }
            } catch (e: Exception) {
                _reportData.value = _reportData.value.copy(
                    isGenerating = false,
                    error = e.message
                )
            }
        }
    }

    fun generatePortfolioSummaryReport() {
        viewModelScope.launch {
            _reportData.value = _reportData.value.copy(isGenerating = true, error = null)
            try {
                val activeProfile = profileRepository.getActiveProfile()
                if (activeProfile != null) {
                    val totalInvested = reportRepository.getTotalInvested(activeProfile.id)
                    val realizedProfit = reportRepository.getTotalRealizedProfit(activeProfile.id)
                    val unrealizedProfit = reportRepository.getTotalUnrealizedProfit(activeProfile.id, emptyMap())
                    val portfolioValue = reportRepository.getPortfolioValue(activeProfile.id, emptyMap())
                    val totalProfit = realizedProfit + unrealizedProfit
                    val returnPercentage = if (totalInvested > 0) (totalProfit / totalInvested) * 100 else 0.0
                    val stockCount = stockRepository.getStockCount(activeProfile.id)
                    val zakatDue = reportRepository.getTotalZakatDue(activeProfile.id, emptyMap())

                    val csvContent = CsvExporter.exportPortfolioSummaryToCsv(
                        totalInvested, portfolioValue, realizedProfit, unrealizedProfit,
                        totalProfit, returnPercentage, stockCount, zakatDue, activeProfile.currency
                    )

                    _reportData.value = ReportData(
                        csvContent = csvContent,
                        reportType = "Portfolio Summary",
                        isGenerating = false
                    )
                } else {
                    _reportData.value = _reportData.value.copy(
                        isGenerating = false,
                        error = "No active profile found"
                    )
                }
            } catch (e: Exception) {
                _reportData.value = _reportData.value.copy(
                    isGenerating = false,
                    error = e.message
                )
            }
        }
    }

    fun clearReport() {
        _reportData.value = ReportData()
    }
}
