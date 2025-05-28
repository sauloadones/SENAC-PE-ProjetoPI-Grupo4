package com.example.appsenkaspi

data class PilarRelatorioDto(
    val nome: String,
    val descricao: String,
    val dataInicio: String,
    val dataPrazo: String,
    val status: String,
    val criadoPor: String
)