package com.openanki.model

data class Deck(
    val id: Long,
    val name: String,
    val cardCount: Int,
    val dbPath: String,
)
