package com.borsahalal

import androidx.compose.runtime.Composable
import com.borsahalal.presentation.ui.MainScreen
import com.borsahalal.presentation.ui.theme.BorsaHalalTheme
import org.koin.compose.KoinContext

@Composable
fun AndroidApp() {
    KoinContext {
        BorsaHalalTheme {
            MainScreen()
        }
    }
}
