package com.financetracker.app.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.financetracker.app.data.SavingsGoal
import com.financetracker.app.data.Transaction
import com.financetracker.app.data.TransactionType
import com.financetracker.app.ui.components.GoalDialog
import com.financetracker.app.ui.components.TransactionDialog
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    savingsGoal: SavingsGoal,
    transactions: List<Transaction>,
    onSetGoal: (Double, String) -> Unit,
    onAddTransaction: (Double, String, TransactionType) -> Unit,
    onDeleteTransaction: (String) -> Unit,
    showGoalDialog: Boolean,
    showTransactionDialog: Boolean,
    onShowGoalDialog: () -> Unit,
    onHideGoalDialog: () -> Unit,
    onShowTransactionDialog: () -> Unit,
    onHideTransactionDialog: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Finance Tracker") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            if (savingsGoal.targetAmount > 0) {
                FloatingActionButton(
                    onClick = onShowTransactionDialog,
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Ajouter transaction")
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                GoalCard(
                    savingsGoal = savingsGoal,
                    onEditGoal = onShowGoalDialog
                )
            }
            
            if (transactions.isNotEmpty()) {
                item {
                    Text(
                        text = "Historique des transactions",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                
                items(transactions, key = { it.id }) { transaction ->
                    TransactionItem(
                        transaction = transaction,
                        onDelete = { onDeleteTransaction(transaction.id) }
                    )
                }
            }
        }
    }
    
    if (showGoalDialog) {
        GoalDialog(
            currentGoal = savingsGoal.targetAmount,
            currentTitle = savingsGoal.title,
            onDismiss = onHideGoalDialog,
            onConfirm = onSetGoal
        )
    }
    
    if (showTransactionDialog) {
        TransactionDialog(
            onDismiss = onHideTransactionDialog,
            onConfirm = onAddTransaction
        )
    }
}

@Composable
fun GoalCard(
    savingsGoal: SavingsGoal,
    onEditGoal: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (savingsGoal.targetAmount > 0) savingsGoal.title else "Aucun objectif défini",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onEditGoal) {
                    Icon(
                        imageVector = if (savingsGoal.targetAmount > 0) Icons.Default.Edit else Icons.Default.Add,
                        contentDescription = "Modifier objectif"
                    )
                }
            }
            
            if (savingsGoal.targetAmount > 0) {
                val animatedProgress by animateFloatAsState(
                    targetValue = savingsGoal.progress,
                    label = "progress"
                )
                
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp),
                    color = if (savingsGoal.isReached) 
                        MaterialTheme.colorScheme.tertiary 
                    else 
                        MaterialTheme.colorScheme.primary,
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Montant actuel",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${String.format("%.2f", savingsGoal.currentAmount)} €",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Objectif",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${String.format("%.2f", savingsGoal.targetAmount)} €",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                if (savingsGoal.isReached) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Objectif atteint !",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.tertiary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Divider()
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Restant",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "${String.format("%.2f", savingsGoal.remaining)} €",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            } else {
                Button(
                    onClick = onEditGoal,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Définir un objectif")
                }
            }
        }
    }
}

@Composable
fun TransactionItem(
    transaction: Transaction,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (transaction.type == TransactionType.DEPOSIT)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.description,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRANCE)
                        .format(Date(transaction.timestamp)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "${if (transaction.type == TransactionType.DEPOSIT) "+" else "-"}${String.format("%.2f", transaction.amount)} €",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Supprimer",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
    
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Supprimer la transaction") },
            text = { Text("Voulez-vous vraiment supprimer cette transaction ?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    }
                ) {
                    Text("Supprimer")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Annuler")
                }
            }
        )
    }
}
