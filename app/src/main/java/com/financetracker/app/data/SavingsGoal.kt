package com.financetracker.app.data

data class SavingsGoal(
    val targetAmount: Double,
    val currentAmount: Double = 0.0,
    val title: String = "Mon objectif d'épargne"
) {
    val progress: Float
        get() = if (targetAmount > 0) {
            (currentAmount / targetAmount).coerceIn(0f, 1f).toFloat()
        } else 0f
    
    val remaining: Double
        get() = (targetAmount - currentAmount).coerceAtLeast(0.0)
    
    val isReached: Boolean
        get() = currentAmount >= targetAmount && targetAmount > 0
}
