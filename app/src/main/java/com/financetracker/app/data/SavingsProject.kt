package com.financetracker.app.data

import java.util.UUID

data class SavingsProject(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val targetAmount: Double,
    val currentAmount: Double = 0.0,
    val colorHex: String = "#4CAF50", // Green by default
    val createdAt: Long = System.currentTimeMillis(),
    val transactions: List<Transaction> = emptyList()
) {
    val progress: Float
        get() = if (targetAmount > 0) {
            (currentAmount / targetAmount).coerceIn(0.0, 1.0).toFloat()
        } else 0f
    
    val remaining: Double
        get() = (targetAmount - currentAmount).coerceAtLeast(0.0)
    
    val isReached: Boolean
        get() = currentAmount >= targetAmount && targetAmount > 0
    
    val progressPercentage: Int
        get() = (progress * 100).toInt()
}
