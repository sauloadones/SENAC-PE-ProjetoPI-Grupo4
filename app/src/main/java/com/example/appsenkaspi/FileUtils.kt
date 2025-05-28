package com.example.appsenkaspi.utils

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import androidx.core.content.FileProvider
import java.io.File

fun getRealPathFromURI(context: Context, contentUri: Uri): String? {
    val proj = arrayOf(MediaStore.Images.Media.DATA)
    val cursor: Cursor? = context.contentResolver.query(contentUri, proj, null, null, null)
    cursor?.use {
        val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        if (it.moveToFirst()) {
            return it.getString(columnIndex)
        }
    }
    return null
}

fun openFile(context: Context, contentUri: Uri) {
    val realPath = getRealPathFromURI(context, contentUri)
    if (realPath != null) {
        val file = File(realPath)
        if (file.exists()) {
            val fileUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(fileUri, "application/pdf") // Ajuste o MIME type conforme necessário
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, "Abrir com"))
        }
    }
}

fun getMimeType(path: String): String {
        return when {
            path.endsWith(".pdf", ignoreCase = true) -> "application/pdf"
            path.endsWith(
                ".xlsx",
                ignoreCase = true
            ) -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"

            path.endsWith(
                ".docx",
                ignoreCase = true
            ) -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document"

            else -> "*/*"
        }
    }
