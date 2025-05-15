package com.example.appsenkaspi

data class RelatorioRequest(
    val pilares: List<PilarDTO>
)

data class PilarDTO(
    val nome: String,
    val descricao: String,
    val dataInicio: String, // formato: "yyyy-MM-dd"
    val dataPrazo: String, // formato: "yyyy-MM-dd"
    val status: String,
    val criadoPor: String
)