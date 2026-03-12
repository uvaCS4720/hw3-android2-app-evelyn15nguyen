package edu.nd.pmcburne.hwapp.one

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import edu.nd.pmcburne.hwapp.one.ui.ScoresScreen
import edu.nd.pmcburne.hwapp.one.ui.theme.HWStarterRepoTheme
import edu.nd.pmcburne.hwapp.one.viewmodel.ScoresViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: ScoresViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            HWStarterRepoTheme {
                val uiState by viewModel.uiState.collectAsState()

                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    ScoresScreen(
                        uiState = uiState,
                        onDateSelected = viewModel::onDateSelected,
                        onGenderSelected = viewModel::onGenderSelected,
                        onRefresh = viewModel::refreshScores,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}