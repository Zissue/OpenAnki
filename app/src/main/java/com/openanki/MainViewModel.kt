package com.openanki

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.openanki.data.AnkiRepository
import com.openanki.model.Card
import com.openanki.model.Deck
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class Grade { AGAIN, HARD, GOOD, EASY }

data class UiState(
    val decks: List<Deck> = emptyList(),
    val selectedDeck: Deck? = null,
    val cards: List<Card> = emptyList(),
    val index: Int = 0,
    val revealAnswer: Boolean = false,
    val isLoading: Boolean = false,
    val message: String? = null,
    val deckSearchTerm: String = "",
    val gradeCounts: Map<Grade, Int> = emptyMap(),
    val reviewDone: Boolean = false,
)

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AnkiRepository(application)

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    fun refreshDecks() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true, message = null) }
            try {
                val decks = repository.listDecks()
                _uiState.update { it.copy(isLoading = false, decks = decks) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, message = e.message ?: "Failed to load decks")
                }
            }
        }
    }

    fun importDeck(context: Context, uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true, message = null) }
            try {
                repository.importApkg(context, uri)
                val decks = repository.listDecks()
                _uiState.update { it.copy(isLoading = false, decks = decks) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, message = e.message ?: "Import failed")
                }
            }
        }
    }

    fun startStudy(deck: Deck) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true, message = null) }
            try {
                val cards = repository.loadCards(deck)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        selectedDeck = deck,
                        cards = cards.shuffled(),
                        index = 0,
                        revealAnswer = false,
                        gradeCounts = emptyMap(),
                        reviewDone = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, message = e.message ?: "Unable to load cards")
                }
            }
        }
    }

    fun flipCard() {
        _uiState.update { it.copy(revealAnswer = !it.revealAnswer) }
    }

    fun gradeCard(grade: Grade) {
        _uiState.update { state ->
            val updatedGradeCounts = state.gradeCounts.toMutableMap()
            updatedGradeCounts[grade] = (updatedGradeCounts[grade] ?: 0) + 1

            val mutableCards = state.cards.toMutableList()
            val remaining = mutableCards.size - state.index - 1

            when (grade) {
                Grade.AGAIN -> {
                    val reinsertOffset = maxOf((remaining * 0.2).toInt(), 1)
                    val card = mutableCards[state.index]
                    val safePos = (state.index + 1 + reinsertOffset).coerceAtMost(mutableCards.size)
                    mutableCards.add(safePos, card)
                }
                Grade.HARD -> {
                    val reinsertOffset = maxOf((remaining * 0.45).toInt(), 1)
                    val card = mutableCards[state.index]
                    val safePos = (state.index + 1 + reinsertOffset).coerceAtMost(mutableCards.size)
                    mutableCards.add(safePos, card)
                }
                Grade.GOOD, Grade.EASY -> { /* card graduates, no reinsertion */ }
            }

            val nextIdx = state.index + 1
            val sessionFinished = nextIdx >= mutableCards.size

            state.copy(
                cards = mutableCards,
                index = nextIdx,
                revealAnswer = false,
                gradeCounts = updatedGradeCounts,
                reviewDone = sessionFinished,
                message = if (sessionFinished) "Session complete" else state.message
            )
        }
    }

    fun clearStudySession() {
        _uiState.update {
            it.copy(
                selectedDeck = null,
                cards = emptyList(),
                index = 0,
                revealAnswer = false,
                gradeCounts = emptyMap(),
                reviewDone = false,
                message = null
            )
        }
    }

    fun setDeckSearch(term: String) {
        _uiState.update { it.copy(deckSearchTerm = term) }
    }

    fun removeDeck(deck: Deck) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true, message = null) }
            try {
                repository.eraseDeck(deck)
                val decks = repository.listDecks()
                _uiState.update { it.copy(isLoading = false, decks = decks) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, message = e.message ?: "Deck removal failed")
                }
            }
        }
    }
}
