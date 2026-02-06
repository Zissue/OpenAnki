package com.openanki.model

data class Card(
    val id: Long,
    val front: String,
    val back: String,
    val additionalFields: List<String> = emptyList(),
    val apkgProperties: Map<String, String> = emptyMap(),
)
