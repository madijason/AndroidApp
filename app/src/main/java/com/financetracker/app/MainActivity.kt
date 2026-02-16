package com.financetracker.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.financetracker.app.data.SavingsRepository
import com.financetracker.app.ui.ProjectsViewModel
import com.financetracker.app.ui.screens.MainNavigation
import com.financetracker.app.ui.theme.FinanceTrackerTheme

class MainActivity : ComponentActivity() {
    
    private lateinit var viewModel: ProjectsViewModel
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val repository = SavingsRepository(applicationContext)
        viewModel = ProjectsViewModel(repository)
        
        setContent {
            FinanceTrackerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainNavigation(viewModel = viewModel)
                }
            }
        }
    }
}
