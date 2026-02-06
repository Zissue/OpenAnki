package com.openanki

import android.app.Application
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.openanki.model.Deck
import com.openanki.ui.screens.DeckListScreen
import com.openanki.ui.screens.StudyScreen
import com.openanki.ui.theme.OpenAnkiTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OpenAnkiApp()
        }
    }
}

@Composable
fun OpenAnkiApp() {
    val context = LocalContext.current
    val viewModel: MainViewModel = viewModel(
        factory = MainViewModelFactory(context.applicationContext as Application)
    )
    val uiState by viewModel.uiState.collectAsState()
    val navController = rememberNavController()

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri: Uri? ->
            if (uri != null) {
                viewModel.importDeck(context, uri)
            }
        }
    )

    LaunchedEffect(Unit) {
        viewModel.refreshDecks()
    }

    OpenAnkiTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            GradientBackground {
                AppNavHost(
                    navController = navController,
                    uiState = uiState,
                    onImport = {
                        importLauncher.launch(arrayOf("application/zip", "application/octet-stream", "*/*"))
                    },
                    onOpenDeck = { deck ->
                        viewModel.startStudy(deck)
                        navController.navigate("study")
                    },
                    onFlip = { viewModel.flipCard() },
                    onGrade = { grade -> viewModel.gradeCard(grade) },
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}

@Composable
private fun GradientBackground(content: @Composable () -> Unit) {
    val colors = MaterialTheme.colorScheme
    val brush = remember(colors) {
        Brush.verticalGradient(
            listOf(
                colors.surface,
                colors.surfaceVariant,
                colors.surface
            )
        )
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush)
    ) {
        content()
    }
}

@Composable
private fun AppNavHost(
    navController: NavHostController,
    uiState: UiState,
    onImport: () -> Unit,
    onOpenDeck: (Deck) -> Unit,
    onFlip: () -> Unit,
    onGrade: (Grade) -> Unit,
    onBack: () -> Unit,
) {
    NavHost(navController = navController, startDestination = "decks") {
        composable("decks") {
            DeckListScreen(
                uiState = uiState,
                onImport = onImport,
                onOpenDeck = onOpenDeck
            )
        }
        composable("study") {
            StudyScreen(
                uiState = uiState,
                onFlip = onFlip,
                onGrade = onGrade,
                onBack = onBack
            )
        }
    }
}

class MainViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
