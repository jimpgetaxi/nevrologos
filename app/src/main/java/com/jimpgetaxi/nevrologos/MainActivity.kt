package com.jimpgetaxi.nevrologos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jimpgetaxi.nevrologos.presentation.screen.HomeScreen
import com.jimpgetaxi.nevrologos.presentation.screen.ProfileSetupScreen
import com.jimpgetaxi.nevrologos.presentation.viewmodel.MainViewModel
import com.jimpgetaxi.nevrologos.ui.theme.ΝευρολόγοςTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ΝευρολόγοςTheme {
                val navController = rememberNavController()
                val viewModel: MainViewModel = hiltViewModel()
                val profiles by viewModel.profiles.collectAsState()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    if (profiles.isNotEmpty() || profiles.isNotEmpty()) { // Just to trigger recomposition or check
                         // wait
                    }
                    
                    NavHost(
                        navController = navController,
                        startDestination = if (profiles.isEmpty()) "setup" else "home",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("setup") {
                            ProfileSetupScreen(viewModel) {
                                navController.navigate("home") {
                                    popUpTo("setup") { inclusive = true }
                                }
                            }
                        }
                        composable("home") {
                            HomeScreen(viewModel)
                        }
                    }
                }
            }
        }
    }
}
