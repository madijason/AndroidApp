package com.financetracker.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.financetracker.app.data.SavingsProject
import com.financetracker.app.data.SavingsRepository
import com.financetracker.app.data.Transaction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProjectsViewModel(private val repository: SavingsRepository) : ViewModel() {
    
    val projects: StateFlow<List<SavingsProject>> = repository.projects
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    
    private val _selectedProject = MutableStateFlow<SavingsProject?>(null)
    val selectedProject: StateFlow<SavingsProject?> = _selectedProject.asStateFlow()
    
    private val _showProjectDialog = MutableStateFlow(false)
    val showProjectDialog: StateFlow<Boolean> = _showProjectDialog.asStateFlow()
    
    private val _showTransactionDialog = MutableStateFlow(false)
    val showTransactionDialog: StateFlow<Boolean> = _showTransactionDialog.asStateFlow()
    
    private val _showDeleteConfirmation = MutableStateFlow<String?>(null)
    val showDeleteConfirmation: StateFlow<String?> = _showDeleteConfirmation.asStateFlow()
    
    fun selectProject(project: SavingsProject?) {
        _selectedProject.value = project
    }
    
    fun addProject(title: String, targetAmount: Double, colorHex: String) {
        viewModelScope.launch {
            val project = SavingsProject(
                title = title,
                targetAmount = targetAmount,
                colorHex = colorHex
            )
            repository.addProject(project)
            hideProjectDialog()
        }
    }
    
    fun deleteProject(projectId: String) {
        viewModelScope.launch {
            repository.deleteProject(projectId)
            _showDeleteConfirmation.value = null
            if (_selectedProject.value?.id == projectId) {
                _selectedProject.value = null
            }
        }
    }
    
    fun addTransaction(projectId: String, description: String, amount: Double) {
        viewModelScope.launch {
            val transaction = Transaction(
                description = description,
                amount = amount
            )
            repository.addTransaction(projectId, transaction)
            hideTransactionDialog()
        }
    }
    
    fun deleteTransaction(projectId: String, transactionId: String) {
        viewModelScope.launch {
            repository.deleteTransaction(projectId, transactionId)
        }
    }
    
    fun showProjectDialog() {
        _showProjectDialog.value = true
    }
    
    fun hideProjectDialog() {
        _showProjectDialog.value = false
    }
    
    fun showTransactionDialog() {
        _showTransactionDialog.value = true
    }
    
    fun hideTransactionDialog() {
        _showTransactionDialog.value = false
    }
    
    fun showDeleteConfirmation(projectId: String) {
        _showDeleteConfirmation.value = projectId
    }
    
    fun hideDeleteConfirmation() {
        _showDeleteConfirmation.value = null
    }
}
