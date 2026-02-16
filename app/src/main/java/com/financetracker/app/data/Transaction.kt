package com.financetracker.app.data

import java.util.Date
import java.util.UUID

data class Transaction(
    val id: String = UUID.randomUUID().toString(),
    val amount: Double,
    val description: String,
    val type: TransactionType,
    val timestamp: Long = System.currentTimeMillis()
)

enum class TransactionType {
    DEPOSIT,
    WITHDRAWAL
}
