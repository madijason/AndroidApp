package com.financetracker.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.financetracker.app.data.SavingsGoal
import com.financetracker.app.data.SavingsRepository
import com.financetracker.app.data.Transaction
import com.financetracker.app.data.TransactionType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SavingsViewModel(private val repository: SavingsRepository) : ViewModel() {
    
    val savingsGoal: StateFlow<SavingsGoal> = repository.savingsGoal
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SavingsGoal(0.0)
        )
    
    val transactions: StateFlow<List<Transaction>> = repository.transactions
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    private val _showGoalDialog = MutableStateFlow(false)
    val showGoalDialog: StateFlow<Boolean> = _showGoalDialog.asStateFlow()
    
    private val _showTransactionDialog = MutableStateFlow(false)
    val showTransactionDialog: StateFlow<Boolean> = _showTransactionDialog.asStateFlow()
    
    fun setGoal(targetAmount: Double, title: String) {
        viewModelScope.launch {
            repository.setGoal(targetAmount, title)
            _showGoalDialog.value = false
        }
    }
    
    fun addTransaction(amount: Double, description: String, type: TransactionType) {
        viewModelScope.launch {
            repository.addTransaction(amount, description, type)
            _showTransactionDialog.value = false
        }
    }
    
    fun deleteTransaction(transactionId: String) {
        viewModelScope.launch {
            repository.deleteTransaction(transactionId)
        }
    }
    
    fun showGoalDialog() {
        _showGoalDialog.value = true
    }
    
    fun hideGoalDialog() {
        _showGoalDialog.value = false
    }
    
    fun showTransactionDialog() {
        _showTransactionDialog.value = true
    }
    
    fun hideTransactionDialog() {
        _showTransactionDialog.value = false
    }
}
