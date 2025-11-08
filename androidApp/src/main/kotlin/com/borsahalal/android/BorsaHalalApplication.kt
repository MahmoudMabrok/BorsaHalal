package com.borsahalal.android

import android.app.Application
import com.borsahalal.data.database.BorsaDatabaseContext
import com.borsahalal.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class BorsaHalalApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize database context
        BorsaDatabaseContext.applicationContext = applicationContext

        // Start Koin
        startKoin {
            androidLogger()
            androidContext(this@BorsaHalalApplication)
            modules(appModules)
        }
    }
}
