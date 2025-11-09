package com.borsahalal.di

import com.borsahalal.data.database.BorsaDatabase
import com.borsahalal.data.database.getDatabaseBuilder
import com.borsahalal.data.repository.ProfileRepository
import com.borsahalal.data.repository.ReportRepository
import com.borsahalal.data.repository.StockRepository
import com.borsahalal.data.repository.TransactionRepository
import com.borsahalal.presentation.viewmodels.DashboardViewModel
import com.borsahalal.presentation.viewmodels.HoldingsViewModel
import com.borsahalal.presentation.viewmodels.ProfileViewModel
import com.borsahalal.presentation.viewmodels.StockViewModel
import com.borsahalal.presentation.viewmodels.TransactionViewModel
import com.borsahalal.utils.FIFOCalculator
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val databaseModule = module {
    single<BorsaDatabase> {
        getDatabaseBuilder()
            .fallbackToDestructiveMigration(true)
            .build()
    }

    single { get<BorsaDatabase>().profileDao() }
    single { get<BorsaDatabase>().stockDao() }
    single { get<BorsaDatabase>().transactionDao() }
    single { get<BorsaDatabase>().stockHoldingDao() }
    single { get<BorsaDatabase>().saleAllocationDao() }
}

val repositoryModule = module {
    singleOf(::ProfileRepository)
    singleOf(::StockRepository)
    singleOf(::TransactionRepository)
    singleOf(::ReportRepository)
}

val utilsModule = module {
    singleOf(::FIFOCalculator)
}

val viewModelModule = module {
    viewModelOf(::ProfileViewModel)
    viewModelOf(::StockViewModel)
    viewModelOf(::TransactionViewModel)
    viewModelOf(::DashboardViewModel)
    viewModelOf(::HoldingsViewModel)
}

val appModules = listOf(
    databaseModule,
    repositoryModule,
    utilsModule,
    viewModelModule
)
