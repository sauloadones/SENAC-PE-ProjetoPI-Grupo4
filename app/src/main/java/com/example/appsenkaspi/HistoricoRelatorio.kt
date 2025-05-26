package com.example.appsenkaspi

data class HistoricoRelatorio(
    val titulo: String,
    val data: String,
    val pilarNome: String? = null,
    val caminhoArquivo: String? = null
)
