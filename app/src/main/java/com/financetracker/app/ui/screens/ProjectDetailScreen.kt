package com.financetracker.app.ui.screens

import android.graphics.Color as AndroidColor
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.financetracker.app.data.SavingsProject
import com.financetracker.app.data.Transaction
import com.financetracker.app.data.TransactionType
import com.financetracker.app.ui.ProjectsViewModel
import com.financetracker.app.ui.components.TransactionDialog
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStartAxis
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetailScreen(
    project: SavingsProject,
    viewModel: ProjectsViewModel,
    onBack: () -> Unit
) {
    val showTransactionDialog by viewModel.showTransactionDialog.collectAsState()
    val projectColor = Color(AndroidColor.parseColor(project.colorHex))
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        project.title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = projectColor.copy(alpha = 0.1f)
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.showTransactionDialog() },
                containerColor = projectColor,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nouvelle transaction")
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
                ProjectProgressCard(project = project, projectColor = projectColor)
            }
            
            if (project.transactions.isNotEmpty()) {
                item {
                    TransactionsChart(project = project, projectColor = projectColor)
                }
                
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "📄 Historique",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "${project.transactions.size} transaction${if (project.transactions.size > 1) "s" else ""}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                items(
                    items = project.transactions.sortedByDescending { it.timestamp },
                    key = { it.id }
                ) { transaction ->
                    TransactionItem(
                        transaction = transaction,
                        projectColor = projectColor,
                        onDelete = { 
                            viewModel.deleteTransaction(project.id, transaction.id) 
                        },
                        modifier = Modifier.animateItemPlacement()
                    )
                }
            } else {
                item {
                    EmptyTransactionsState()
                }
            }
        }
    }
    
    if (showTransactionDialog) {
        TransactionDialog(
            onDismiss = { viewModel.hideTransactionDialog() },
            onConfirm = { amount, description, type ->
                viewModel.addTransaction(project.id, amount, description, type)
            }
        )
    }
}

@Composable
fun ProjectProgressCard(
    project: SavingsProject,
    projectColor: Color
) {
    val progressAnimation by animateFloatAsState(
        targetValue = project.progress,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "progress"
    )
    
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(
                            projectColor.copy(alpha = 0.1f),
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = "Montant actuel",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = formatCurrency(project.currentAmount),
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        color = projectColor
                    )
                }
                
                Surface(
                    shape = CircleShape,
                    color = if (project.isReached) 
                        MaterialTheme.colorScheme.primaryContainer 
                    else 
                        projectColor.copy(alpha = 0.2f),
                    modifier = Modifier.size(64.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = if (project.isReached) "✅" else "${project.progressPercentage}%",
                            style = if (project.isReached) 
                                MaterialTheme.typography.headlineMedium 
                            else 
                                MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (project.isReached) 
                                MaterialTheme.colorScheme.onPrimaryContainer 
                            else 
                                projectColor
                        )
                    }
                }
            }
            
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                LinearProgressIndicator(
                    progress = { progressAnimation },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    color = projectColor,
                    trackColor = projectColor.copy(alpha = 0.2f)
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Objectif",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = formatCurrency(project.targetAmount),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = if (project.isReached) "Dépassé de" else "Reste",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = if (project.isReached) 
                                formatCurrency(project.currentAmount - project.targetAmount)
                            else 
                                formatCurrency(project.remaining),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = if (project.isReached) 
                                MaterialTheme.colorScheme.primary 
                            else 
                                MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionsChart(
    project: SavingsProject,
    projectColor: Color
) {
    val modelProducer = remember { CartesianChartModelProducer.build() }
    val recentTransactions = project.transactions.takeLast(10).sortedBy { it.timestamp }
    
    LaunchedEffect(recentTransactions) {
        modelProducer.tryRunTransaction {
            columnSeries {
                series(recentTransactions.map { 
                    if (it.type == TransactionType.DEPOSIT) it.amount else -it.amount 
                })
            }
        }
    }
    
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "📈 Dernières transactions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            CartesianChartHost(
                chart = rememberCartesianChart(
                    rememberColumnCartesianLayer(),
                    startAxis = rememberStartAxis(label = null),
                    bottomAxis = rememberBottomAxis(label = null)
                ),
                modelProducer = modelProducer,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ChartLegendItem(
                    color = MaterialTheme.colorScheme.primary,
                    label = "Dépôts",
                    modifier = Modifier.weight(1f)
                )
                ChartLegendItem(
                    color = MaterialTheme.colorScheme.error,
                    label = "Retraits",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun ChartLegendItem(
    color: Color,
    label: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun TransactionItem(
    transaction: Transaction,
    projectColor: Color,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDeposit = transaction.type == TransactionType.DEPOSIT
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.FRANCE) }
    
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    shape = CircleShape,
                    color = if (isDeposit) 
                        projectColor.copy(alpha = 0.2f) 
                    else 
                        MaterialTheme.colorScheme.errorContainer,
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = if (isDeposit) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                            contentDescription = null,
                            tint = if (isDeposit) projectColor else MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                
                Column {
                    Text(
                        text = transaction.description,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = dateFormat.format(Date(transaction.timestamp)),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${if (isDeposit) "+" else "-"}${formatCurrency(transaction.amount)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isDeposit) projectColor else MaterialTheme.colorScheme.error
                )
                
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Supprimer",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyTransactionsState() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                Icons.Default.Receipt,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Text(
                text = "Aucune transaction",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            Text(
                text = "Ajoutez votre première transaction pour commencer à suivre vos économies",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
