package com.example.appsenkaspi.utils

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.app.DownloadManager
import android.os.Environment
import android.widget.Toast
import android.provider.MediaStore
import androidx.core.content.FileProvider
import java.io.File
import android.os.Build
import android.content.ContentValues
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

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
        path.endsWith(".xlsx", ignoreCase = true) -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        path.endsWith(".docx", ignoreCase = true) -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
        else -> "*/*"
    }
}

fun baixarArquivo(context: Context, url: String, nomeArquivo: String, mimeType: String) {
    try {
        val request = DownloadManager.Request(Uri.parse(url))
            .setTitle("Baixando relatório")
            .setDescription("Relatório está sendo baixado novamente...")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, nomeArquivo)
            .setMimeType(mimeType)
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)

        Toast.makeText(context, "Download iniciado...", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        Toast.makeText(context, "Erro ao baixar o arquivo.", Toast.LENGTH_SHORT).show()
        e.printStackTrace()
    }
}

// 🔥 Função para baixar com OkHttp (usada como alternativa ao DownloadManager)
suspend fun baixarArquivoComOkHttp(context: Context, url: String, nomeArquivo: String, mimeType: String): String? {
    return withContext(Dispatchers.IO) {
        try {
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()

            println("DEBUG: Código da resposta: ${response.code}")
            println("DEBUG: Mensagem da resposta: ${response.message}")

            if (response.isSuccessful) {
                val body = response.body
                val resolver = context.contentResolver

                val contentValues = ContentValues().apply {
                    put(MediaStore.Downloads.DISPLAY_NAME, nomeArquivo)
                    put(MediaStore.Downloads.MIME_TYPE, mimeType)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        put(MediaStore.Downloads.IS_PENDING, 1)
                    }
                }

                val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                } else {
                    MediaStore.Files.getContentUri("external")
                }

                val itemUri = resolver.insert(collection, contentValues)
                itemUri?.let { uri ->
                    resolver.openOutputStream(uri).use { outputStream ->
                        body?.byteStream()?.use { inputStream ->
                            val buffer = ByteArray(4096)
                            var bytesRead: Int
                            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                                outputStream?.write(buffer, 0, bytesRead)
                            }
                            outputStream?.flush()
                        }
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        contentValues.clear()
                        contentValues.put(MediaStore.Downloads.IS_PENDING, 0)
                        resolver.update(uri, contentValues, null, null)
                    }
                    println("DEBUG: Download concluído: ${uri.toString()}")
                    return@withContext uri.toString()
                }
            } else {
                println("DEBUG: Falha na requisição - Código: ${response.code}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        println("DEBUG: Falha ao baixar o arquivo.")
        return@withContext null
    }
}