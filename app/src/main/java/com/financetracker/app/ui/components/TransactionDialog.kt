package com.financetracker.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.financetracker.app.data.TransactionType

@Composable
fun TransactionDialog(
    onDismiss: () -> Unit,
    onConfirm: (Double, String, TransactionType) -> Unit
) {
    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isDeposit by remember { mutableStateOf(true) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nouvelle transaction") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = isDeposit,
                        onClick = { isDeposit = true },
                        label = { Text("Épargner") },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = !isDeposit,
                        onClick = { isDeposit = false },
                        label = { Text("Retirer") },
                        modifier = Modifier.weight(1f)
                    )
                }
                
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Montant (€)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val amt = amount.toDoubleOrNull()
                    if (amt != null && amt > 0) {
                        onConfirm(
                            amt,
                            description.ifBlank { if (isDeposit) "Épargne" else "Retrait" },
                            if (isDeposit) TransactionType.DEPOSIT else TransactionType.WITHDRAWAL
                        )
                    }
                },
                enabled = amount.toDoubleOrNull()?.let { it > 0 } == true
            ) {
                Text("Enregistrer")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )
}
