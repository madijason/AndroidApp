package com.financetracker.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.financetracker.app.data.SavingsRepository
import com.financetracker.app.ui.MainScreen
import com.financetracker.app.ui.SavingsViewModel
import com.financetracker.app.ui.theme.FinanceTrackerTheme

class MainActivity : ComponentActivity() {
    
    private lateinit var viewModel: SavingsViewModel
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val repository = SavingsRepository(applicationContext)
        viewModel = SavingsViewModel(repository)
        
        setContent {
            FinanceTrackerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val savingsGoal by viewModel.savingsGoal.collectAsState()
                    val transactions by viewModel.transactions.collectAsState()
                    val showGoalDialog by viewModel.showGoalDialog.collectAsState()
                    val showTransactionDialog by viewModel.showTransactionDialog.collectAsState()
                    
                    MainScreen(
                        savingsGoal = savingsGoal,
                        transactions = transactions,
                        onSetGoal = viewModel::setGoal,
                        onAddTransaction = viewModel::addTransaction,
                        onDeleteTransaction = viewModel::deleteTransaction,
                        showGoalDialog = showGoalDialog,
                        showTransactionDialog = showTransactionDialog,
                        onShowGoalDialog = viewModel::showGoalDialog,
                        onHideGoalDialog = viewModel::hideGoalDialog,
                        onShowTransactionDialog = viewModel::showTransactionDialog,
                        onHideTransactionDialog = viewModel::hideTransactionDialog
                    )
                }
            }
        }
    }
}
