package com.openanki.data

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.openanki.model.Card
import com.openanki.model.Deck
import org.json.JSONObject

object AnkiDbReader {
    private val htmlLineBreakPattern = Regex("""<br\s*/?>""", RegexOption.IGNORE_CASE)
    private val htmlTagPattern = Regex("""<[^>]*>""")
    private val entityMapping = mapOf(
        "&amp;" to "&", "&lt;" to "<", "&gt;" to ">",
        "&nbsp;" to " ", "&quot;" to "\""
    )

    private fun sanitizeCardField(raw: String): String {
        var cleaned = htmlLineBreakPattern.replace(raw, "\n")
        cleaned = htmlTagPattern.replace(cleaned, "")
        for ((entity, replacement) in entityMapping) {
            cleaned = cleaned.replace(entity, replacement)
        }
        return cleaned.trim()
    }

    fun readDecks(dbPath: String): List<Deck> {
        val db = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY)
        db.use {
            val cursor = db.rawQuery("SELECT decks FROM col LIMIT 1", null)
            cursor.use {
                if (!cursor.moveToFirst()) return emptyList()
                val json = cursor.getString(0)
                val decksJson = JSONObject(json)
                val decks = mutableListOf<Deck>()
                val keys = decksJson.keys()
                while (keys.hasNext()) {
                    val key = keys.next()
                    val deckId = key.toLongOrNull() ?: continue
                    val deckObj = decksJson.getJSONObject(key)
                    val name = deckObj.optString("name", "Deck")
                    val count = queryCount(db, deckId)
                    decks.add(Deck(deckId, name, count, dbPath))
                }
                return decks
            }
        }
    }

    fun readCards(dbPath: String, deckId: Long, limit: Int): List<Card> {
        val db = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY)
        db.use {
            val cursor = db.rawQuery(
                "SELECT c.*, n.flds, n.tags, n.sfld FROM cards c JOIN notes n ON c.nid = n.id WHERE c.did = ? ORDER BY c.id LIMIT ?",
                arrayOf(deckId.toString(), limit.toString())
            )
            cursor.use {
                val cards = mutableListOf<Card>()
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(cursor.getColumnIndex("id"))
                    val flds = cursor.getString(cursor.getColumnIndex("flds"))
                    val parts = flds.split('\u001F')
                    val front = sanitizeCardField(parts.getOrNull(0).orEmpty())
                    val back = sanitizeCardField(parts.getOrNull(1).orEmpty())
                    val oaExtraFields = parts.filterIndexed { idx, _ -> idx >= 2 }
                        .map { seg -> sanitizeCardField(seg) }
                    val oaRawProps = buildMap {
                        repeat(cursor.columnCount) { pos ->
                            put(cursor.getColumnName(pos), cursor.getString(pos) ?: "")
                        }
                    }
                    cards.add(Card(id, front, back, oaExtraFields, oaRawProps))
                }
                return cards
            }
        }
    }

    private fun queryCount(db: SQLiteDatabase, deckId: Long): Int {
        val cursor: Cursor = db.rawQuery("SELECT COUNT(*) FROM cards WHERE did = ?", arrayOf(deckId.toString()))
        cursor.use {
            return if (cursor.moveToFirst()) cursor.getInt(0) else 0
        }
    }
}
