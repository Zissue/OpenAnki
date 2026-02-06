package com.openanki.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.FileOpen
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Button
import androidx.compose.foundation.shape.RoundedCornerShape as RoundedButtonCorners
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.openanki.UiState
import com.openanki.model.Deck

@Composable
fun DeckListScreen(
    uiState: UiState,
    onImport: () -> Unit,
    onOpenDeck: (Deck) -> Unit,
    onDeleteDeck: (Deck) -> Unit,
    onSearchChanged: (String) -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        ZenBackdrop()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            Header()
            Spacer(modifier = Modifier.height(16.dp))
            StatsRow(uiState)
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = uiState.deckSearchTerm,
                onValueChange = onSearchChanged,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Filter decks\u2026") },
                leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = "Search decks") },
                singleLine = true
            )
            Spacer(modifier = Modifier.height(12.dp))
            uiState.message?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (uiState.decks.isEmpty() && !uiState.isLoading) {
                EmptyState(onImport)
            } else {
                val visibleDecks = if (uiState.deckSearchTerm.isBlank()) {
                    uiState.decks
                } else {
                    uiState.decks.filter { d ->
                        d.name.contains(uiState.deckSearchTerm, ignoreCase = true)
                    }
                }
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(visibleDecks, key = { it.id }) { deck ->
                        DeckCard(
                            deck = deck,
                            onOpen = { onOpenDeck(deck) },
                            onRemove = { onDeleteDeck(deck) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onImport,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedButtonCorners(16.dp)
            ) {
                Icon(Icons.Outlined.FileOpen, contentDescription = null)
                Spacer(modifier = Modifier.size(8.dp))
                Text("Import .apkg deck")
            }
        }
    }
}

@Composable
private fun Header() {
    Column {
        Text(
            text = "OpenAnki",
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Quiet focus, steady recall.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun StatsRow(uiState: UiState) {
    val cardCount = uiState.decks.sumOf { it.cardCount }
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        StatPill(label = "Decks", value = uiState.decks.size.toString())
        StatPill(label = "Cards", value = cardCount.toString())
    }
}

@Composable
private fun StatPill(label: String, value: String) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        shape = CircleShape
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DeckCard(deck: Deck, onOpen: () -> Unit, onRemove: () -> Unit) {
    val gradient = Brush.linearGradient(
        listOf(
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f),
            MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
        )
    )
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onOpen
    ) {
        Row(
            modifier = Modifier
                .background(gradient)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.PlayArrow,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.size(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(deck.name, style = MaterialTheme.typography.titleLarge)
                Text(
                    text = "${deck.cardCount} cards",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            TextButton(onClick = onOpen) {
                Text("Study")
            }
            IconButton(onClick = onRemove) {
                Icon(
                    Icons.Outlined.Delete,
                    contentDescription = "Remove deck",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun EmptyState(onImport: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "No decks yet.",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Import a community .apkg file and begin a calm review flow.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(12.dp))
        TextButton(onClick = onImport) {
            Text("Find a deck to import")
        }
    }
}

@Composable
private fun ZenBackdrop() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val ink = Color(0x1A5E6F64)
        val sand = Color(0x2AECE4D6)
        val centerX = size.width * 0.75f
        val centerY = size.height * 0.2f

        drawCircle(
            color = sand,
            radius = size.minDimension * 0.55f,
            center = androidx.compose.ui.geometry.Offset(x = centerX, y = centerY)
        )
        drawCircle(
            color = ink,
            radius = size.minDimension * 0.35f,
            center = androidx.compose.ui.geometry.Offset(x = size.width * 0.2f, y = size.height * 0.85f)
        )

        val stroke = 1.2f
        val gap = size.height / 9f
        var y = gap * 1.5f
        while (y < size.height) {
            drawLine(
                color = Color(0x1A4A4F45),
                start = androidx.compose.ui.geometry.Offset(x = size.width * 0.05f, y = y),
                end = androidx.compose.ui.geometry.Offset(x = size.width * 0.95f, y = y),
                strokeWidth = stroke
            )
            y += gap
        }
    }
}
