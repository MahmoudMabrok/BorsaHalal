package com.borsahalal

import androidx.compose.runtime.Composable
import com.borsahalal.presentation.ui.screens.dashboard.DashboardScreen
import com.borsahalal.presentation.ui.theme.BorsaHalalTheme
import org.koin.compose.KoinContext

@Composable
fun App() {
    KoinContext {
        BorsaHalalTheme {
            DashboardScreen()
        }
    }
}
