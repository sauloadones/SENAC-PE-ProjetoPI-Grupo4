package com.example.appsenkaspi.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File
import android.webkit.MimeTypeMap

fun abrirArquivo(context: Context, caminhoArquivo: String) {
    try {
        val file = File(caminhoArquivo)
        val uri: Uri = FileProvider.getUriForFile(
            context,
            context.packageName + ".fileprovider",
            file
        )

        val mime = MimeTypeMap.getSingleton()
            .getMimeTypeFromExtension(file.extension.lowercase()) ?: "*/*"

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, mime)
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
        }

        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "Não foi possível abrir o arquivo.", Toast.LENGTH_SHORT).show()
        e.printStackTrace()
    }
}
