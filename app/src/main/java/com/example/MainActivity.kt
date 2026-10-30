package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.Screen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LoadingScreen
import com.example.ui.screens.QuizScreen
import com.example.ui.screens.ResultScreen
import com.example.ui.screens.SettingsDialog
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.LgsViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val viewModel: LgsViewModel = viewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                var showSettings by remember { mutableStateOf(false) }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    contentWindowInsets = WindowInsets.safeDrawing
                ) { innerPadding ->
                    val screenModifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)

                    when (uiState.currentScreen) {
                        Screen.HOME -> {
                            HomeScreen(
                                uiState = uiState,
                                viewModel = viewModel,
                                onOpenSettings = { showSettings = true },
                                modifier = screenModifier
                            )
                        }

                        Screen.LOADING -> {
                            LoadingScreen(
                                subject = uiState.activeSubject,
                                tip = uiState.loadingTip,
                                onCancel = { viewModel.navigateTo(Screen.HOME) },
                                modifier = screenModifier
                            )
                        }

                        Screen.QUIZ -> {
                            QuizScreen(
                                uiState = uiState,
                                viewModel = viewModel,
                                modifier = screenModifier
                            )
                        }

                        Screen.RESULT -> {
                            ResultScreen(
                                uiState = uiState,
                                viewModel = viewModel,
                                modifier = screenModifier
                            )
                        }

                        Screen.SETTINGS -> {
                            HomeScreen(
                                uiState = uiState,
                                viewModel = viewModel,
                                onOpenSettings = { showSettings = true },
                                modifier = screenModifier
                            )
                        }
                    }

                    if (showSettings) {
                        SettingsDialog(
                            uiState = uiState,
                            viewModel = viewModel,
                            onDismiss = { showSettings = false }
                        )
                    }
                }
            }
        }
    }
}
