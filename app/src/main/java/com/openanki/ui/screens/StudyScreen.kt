package com.openanki.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.openanki.Grade
import com.openanki.UiState

@Composable
fun StudyScreen(
    uiState: UiState,
    onFlip: () -> Unit,
    onGrade: (Grade) -> Unit,
    onBack: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 20.dp)
    ) {
        TopBar(uiState.selectedDeck?.name ?: "Study", onBack)
        Spacer(modifier = Modifier.height(16.dp))

        val card = uiState.cards.getOrNull(uiState.index)
        if (card == null) {
            EmptyStudyState(onBack)
            return
        }

        val progress = (uiState.index + 1).coerceAtMost(uiState.cards.size)
        Text(
            text = "$progress / ${uiState.cards.size}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(12.dp))

        val lift by animateFloatAsState(if (uiState.revealAnswer) 12f else 4f, label = "lift")
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .shadow(lift.dp, RoundedCornerShape(28.dp))
                .clickable { onFlip() },
            shape = RoundedCornerShape(28.dp)
        ) {
            val gradient = Brush.verticalGradient(
                listOf(
                    MaterialTheme.colorScheme.surface,
                    MaterialTheme.colorScheme.surfaceVariant
                )
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(gradient)
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = card.front.ifBlank { "(Empty front)" },
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(20.dp))
                AnimatedVisibility(
                    visible = uiState.revealAnswer,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Column {
                        Text(
                            text = "Answer",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = card.back.ifBlank { "(Empty back)" },
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        GradeRow(onGrade)
        Spacer(modifier = Modifier.height(8.dp))
        uiState.message?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))
        }
        Text(
            text = "Tap the card to reveal the answer.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun TopBar(title: String, onBack: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(onClick = onBack) {
            Icon(Icons.Outlined.ArrowBack, contentDescription = null)
            Spacer(modifier = Modifier.size(6.dp))
            Text("Decks")
        }
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge
        )
    }
}

@Composable
private fun GradeRow(onGrade: (Grade) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedButton(
            onClick = { onGrade(Grade.AGAIN) },
            modifier = Modifier.weight(1f)
        ) {
            Text("Again")
        }
        OutlinedButton(
            onClick = { onGrade(Grade.HARD) },
            modifier = Modifier.weight(1f)
        ) {
            Text("Hard")
        }
        Button(
            onClick = { onGrade(Grade.GOOD) },
            modifier = Modifier.weight(1f)
        ) {
            Text("Good")
        }
        Button(
            onClick = { onGrade(Grade.EASY) },
            modifier = Modifier.weight(1f)
        ) {
            Text("Easy")
        }
    }
}

@Composable
private fun EmptyStudyState(onBack: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "No cards ready yet.",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Import a deck or pick another one to study.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(12.dp))
        TextButton(onClick = onBack) {
            Text("Back to decks")
        }
    }
}
