package com.financetracker.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

val projectColors = listOf(
    "#4CAF50" to "Vert",
    "#2196F3" to "Bleu",
    "#FF9800" to "Orange",
    "#E91E63" to "Rose",
    "#9C27B0" to "Violet",
    "#F44336" to "Rouge",
    "#00BCD4" to "Cyan",
    "#FFEB3B" to "Jaune"
)

@Composable
fun ProjectDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Double, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var targetAmount by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf(projectColors[0].first) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Text(
                "Nouveau projet d'épargne",
                style = MaterialTheme.typography.headlineSmall
            ) 
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("🎯 Nom du projet") },
                    placeholder = { Text("Ex: Vacances, Voiture, Études...") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                OutlinedTextField(
                    value = targetAmount,
                    onValueChange = { targetAmount = it },
                    label = { Text("💰 Objectif (€)") },
                    placeholder = { Text("Montant à atteindre") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    prefix = { Text("€ ") }
                )
                
                Text(
                    "Couleur du projet",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    projectColors.take(4).forEach { (colorHex, name) ->
                        ColorOption(
                            colorHex = colorHex,
                            selected = selectedColor == colorHex,
                            onClick = { selectedColor = colorHex },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    projectColors.drop(4).forEach { (colorHex, name) ->
                        ColorOption(
                            colorHex = colorHex,
                            selected = selectedColor == colorHex,
                            onClick = { selectedColor = colorHex },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        },
        confirmButton = {
            FilledTonalButton(
                onClick = {
                    val amount = targetAmount.toDoubleOrNull()
                    if (title.isNotBlank() && amount != null && amount > 0) {
                        onConfirm(title, amount, selectedColor)
                    }
                },
                enabled = title.isNotBlank() && targetAmount.toDoubleOrNull()?.let { it > 0 } == true
            ) {
                Text("✅ Créer")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )
}

@Composable
fun ColorOption(
    colorHex: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val color = Color(android.graphics.Color.parseColor(colorHex))
    
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(CircleShape)
            .background(color)
            .then(
                if (selected) {
                    Modifier.border(
                        width = 3.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    )
                } else Modifier
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (selected) {
            Text(
                text = "✓",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}
