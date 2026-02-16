package com.financetracker.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "savings_prefs")

class SavingsRepository(private val context: Context) {
    
    private val gson = Gson()
    
    companion object {
        private val PROJECTS_KEY = stringPreferencesKey("savings_projects")
    }
    
    val projects: Flow<List<SavingsProject>> = context.dataStore.data.map { preferences ->
        val json = preferences[PROJECTS_KEY] ?: "[]"
        val type = object : TypeToken<List<SavingsProject>>() {}.type
        gson.fromJson(json, type) ?: emptyList()
    }
    
    suspend fun addProject(project: SavingsProject) {
        context.dataStore.edit { preferences ->
            val currentProjects = getCurrentProjects(preferences)
            val updatedProjects = currentProjects + project
            preferences[PROJECTS_KEY] = gson.toJson(updatedProjects)
        }
    }
    
    suspend fun updateProject(updatedProject: SavingsProject) {
        context.dataStore.edit { preferences ->
            val currentProjects = getCurrentProjects(preferences)
            val updatedProjects = currentProjects.map { project ->
                if (project.id == updatedProject.id) updatedProject else project
            }
            preferences[PROJECTS_KEY] = gson.toJson(updatedProjects)
        }
    }
    
    suspend fun deleteProject(projectId: String) {
        context.dataStore.edit { preferences ->
            val currentProjects = getCurrentProjects(preferences)
            val updatedProjects = currentProjects.filter { it.id != projectId }
            preferences[PROJECTS_KEY] = gson.toJson(updatedProjects)
        }
    }
    
    suspend fun addTransaction(projectId: String, transaction: Transaction) {
        context.dataStore.edit { preferences ->
            val currentProjects = getCurrentProjects(preferences)
            val updatedProjects = currentProjects.map { project ->
                if (project.id == projectId) {
                    val newTransactions = project.transactions + transaction
                    // Dépôt = augmente, Retrait = diminue
                    val amountChange = if (transaction.type == TransactionType.DEPOSIT) {
                        transaction.amount
                    } else {
                        -transaction.amount
                    }
                    val newAmount = (project.currentAmount + amountChange).coerceAtLeast(0.0)
                    project.copy(
                        transactions = newTransactions,
                        currentAmount = newAmount
                    )
                } else project
            }
            preferences[PROJECTS_KEY] = gson.toJson(updatedProjects)
        }
    }
    
    suspend fun deleteTransaction(projectId: String, transactionId: String) {
        context.dataStore.edit { preferences ->
            val currentProjects = getCurrentProjects(preferences)
            val updatedProjects = currentProjects.map { project ->
                if (project.id == projectId) {
                    val transactionToDelete = project.transactions.find { it.id == transactionId }
                    val newTransactions = project.transactions.filter { it.id != transactionId }
                    val newAmount = if (transactionToDelete != null) {
                        // Annuler l'effet de la transaction supprimée
                        val amountChange = if (transactionToDelete.type == TransactionType.DEPOSIT) {
                            -transactionToDelete.amount
                        } else {
                            transactionToDelete.amount
                        }
                        (project.currentAmount + amountChange).coerceAtLeast(0.0)
                    } else project.currentAmount
                    project.copy(
                        transactions = newTransactions,
                        currentAmount = newAmount
                    )
                } else project
            }
            preferences[PROJECTS_KEY] = gson.toJson(updatedProjects)
        }
    }
    
    private fun getCurrentProjects(preferences: Preferences): List<SavingsProject> {
        val json = preferences[PROJECTS_KEY] ?: "[]"
        val type = object : TypeToken<List<SavingsProject>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }
}
