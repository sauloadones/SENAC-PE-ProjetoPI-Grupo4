package com.example.appsenkaspi

import java.util.Date

data class AcaoJson(
    val nome: String,
    val descricao: String,
    val dataInicio: Date,
    val dataPrazo: Date,
    val status: StatusAcao,
    val criadoPor: Int,
    val dataCriacao: Date,
    val responsaveis: List<Int>
)
