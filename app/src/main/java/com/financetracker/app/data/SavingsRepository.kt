package com.financetracker.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "savings_prefs")

class SavingsRepository(private val context: Context) {
    
    private val targetAmountKey = doublePreferencesKey("target_amount")
    private val currentAmountKey = doublePreferencesKey("current_amount")
    private val goalTitleKey = stringPreferencesKey("goal_title")
    private val transactionsKey = stringPreferencesKey("transactions")
    
    val savingsGoal: Flow<SavingsGoal> = context.dataStore.data.map { preferences ->
        SavingsGoal(
            targetAmount = preferences[targetAmountKey] ?: 0.0,
            currentAmount = preferences[currentAmountKey] ?: 0.0,
            title = preferences[goalTitleKey] ?: "Mon objectif d'épargne"
        )
    }
    
    val transactions: Flow<List<Transaction>> = context.dataStore.data.map { preferences ->
        val json = preferences[transactionsKey] ?: "[]"
        parseTransactions(json)
    }
    
    suspend fun setGoal(targetAmount: Double, title: String) {
        context.dataStore.edit { preferences ->
            preferences[targetAmountKey] = targetAmount
            preferences[goalTitleKey] = title
        }
    }
    
    suspend fun addTransaction(amount: Double, description: String, type: TransactionType) {
        val currentGoal = savingsGoal.first()
        val newAmount = when (type) {
            TransactionType.DEPOSIT -> currentGoal.currentAmount + amount
            TransactionType.WITHDRAWAL -> (currentGoal.currentAmount - amount).coerceAtLeast(0.0)
        }
        
        val newTransaction = Transaction(amount = amount, description = description, type = type)
        val currentTransactions = transactions.first()
        val updatedTransactions = listOf(newTransaction) + currentTransactions
        
        context.dataStore.edit { preferences ->
            preferences[currentAmountKey] = newAmount
            preferences[transactionsKey] = serializeTransactions(updatedTransactions)
        }
    }
    
    suspend fun deleteTransaction(transactionId: String) {
        val currentTransactions = transactions.first()
        val transaction = currentTransactions.find { it.id == transactionId } ?: return
        val currentGoal = savingsGoal.first()
        
        val newAmount = when (transaction.type) {
            TransactionType.DEPOSIT -> (currentGoal.currentAmount - transaction.amount).coerceAtLeast(0.0)
            TransactionType.WITHDRAWAL -> currentGoal.currentAmount + transaction.amount
        }
        
        val updatedTransactions = currentTransactions.filter { it.id != transactionId }
        
        context.dataStore.edit { preferences ->
            preferences[currentAmountKey] = newAmount
            preferences[transactionsKey] = serializeTransactions(updatedTransactions)
        }
    }
    
    private fun serializeTransactions(transactions: List<Transaction>): String {
        val jsonArray = JSONArray()
        transactions.forEach { transaction ->
            val jsonObject = JSONObject()
            jsonObject.put("id", transaction.id)
            jsonObject.put("amount", transaction.amount)
            jsonObject.put("description", transaction.description)
            jsonObject.put("type", transaction.type.name)
            jsonObject.put("timestamp", transaction.timestamp)
            jsonArray.put(jsonObject)
        }
        return jsonArray.toString()
    }
    
    private fun parseTransactions(json: String): List<Transaction> {
        return try {
            val jsonArray = JSONArray(json)
            List(jsonArray.length()) { i ->
                val obj = jsonArray.getJSONObject(i)
                Transaction(
                    id = obj.getString("id"),
                    amount = obj.getDouble("amount"),
                    description = obj.getString("description"),
                    type = TransactionType.valueOf(obj.getString("type")),
                    timestamp = obj.getLong("timestamp")
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
