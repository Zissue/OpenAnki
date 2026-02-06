package com.openanki.data

import android.content.Context
import android.net.Uri
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipInputStream

object AnkiImporter {
    fun importApkg(context: Context, uri: Uri, targetRoot: File): File {
        if (!targetRoot.exists()) {
            targetRoot.mkdirs()
        }
        val deckDir = File(targetRoot, "deck_${System.currentTimeMillis()}")
        deckDir.mkdirs()

        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            ZipInputStream(BufferedInputStream(inputStream)).use { zipStream ->
                var entry = zipStream.nextEntry
                while (entry != null) {
                    val outFile = File(deckDir, entry.name)
                    if (entry.isDirectory) {
                        outFile.mkdirs()
                    } else {
                        outFile.parentFile?.mkdirs()
                        FileOutputStream(outFile).use { output ->
                            zipStream.copyTo(output)
                        }
                    }
                    zipStream.closeEntry()
                    entry = zipStream.nextEntry
                }
            }
        } ?: error("Unable to read deck file")

        return deckDir
    }
}
