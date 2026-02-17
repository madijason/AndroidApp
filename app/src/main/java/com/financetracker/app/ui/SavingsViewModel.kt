package com.financetracker.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.financetracker.app.data.SavingsProject
import com.financetracker.app.data.SavingsRepository
import com.financetracker.app.data.Transaction
import com.financetracker.app.data.TransactionType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

// Ce ViewModel est désormais obsolète - utiliser ProjectsViewModel à la place
// Conservé pour compatibilité arrière uniquement
class SavingsViewModel(private val repository: SavingsRepository) : ViewModel() {
    
    val projects: StateFlow<List<SavingsProject>> = repository.projects
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
            val project = SavingsProject(
                id = UUID.randomUUID().toString(),
                title = title,
                targetAmount = targetAmount,
                currentAmount = 0.0,
                colorHex = "#6200EE",
                transactions = emptyList()
            )
            repository.addProject(project)
            _showGoalDialog.value = false
        }
    }
    
    fun addTransaction(projectId: String, amount: Double, description: String, type: TransactionType) {
        viewModelScope.launch {
            val transaction = Transaction(
                id = UUID.randomUUID().toString(),
                amount = amount,
                description = description,
                type = type,
                timestamp = System.currentTimeMillis()
            )
            repository.addTransaction(projectId, transaction)
            _showTransactionDialog.value = false
        }
    }
    
    fun deleteTransaction(projectId: String, transactionId: String) {
        viewModelScope.launch {
            repository.deleteTransaction(projectId, transactionId)
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
