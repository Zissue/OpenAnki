package com.openanki.data

import android.app.Application
import android.content.Context
import android.net.Uri
import com.openanki.model.Card
import com.openanki.model.Deck
import java.io.File

class AnkiRepository(private val application: Application) {
    private val decksDir = File(application.filesDir, "decks")

    fun importApkg(context: Context, uri: Uri): File {
        return AnkiImporter.importApkg(context, uri, decksDir)
    }

    fun listDecks(): List<Deck> {
        if (!decksDir.exists()) {
            return emptyList()
        }
        val deckDirs = decksDir.listFiles()?.filter { it.isDirectory } ?: return emptyList()
        return deckDirs.flatMap { dir ->
            val dbFile = dir.listFiles()?.firstOrNull { file ->
                file.isFile && (file.name.endsWith(".anki2") || file.name.endsWith(".anki21"))
            }
            if (dbFile != null) {
                AnkiDbReader.readDecks(dbFile.absolutePath)
            } else {
                emptyList()
            }
        }.sortedBy { it.name.lowercase() }
    }

    fun loadCards(deck: Deck, limit: Int = 200): List<Card> {
        return AnkiDbReader.readCards(deck.dbPath, deck.id, limit)
    }

    fun eraseDeck(deck: Deck) {
        val deckDbFile = File(deck.dbPath)
        val enclosingDir = deckDbFile.parentFile ?: return
        // Only allow deletion within the app-managed decks directory
        if (enclosingDir.isDirectory && enclosingDir.absolutePath.startsWith(decksDir.absolutePath)) {
            enclosingDir.deleteRecursively()
        }
    }
}
