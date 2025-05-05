package com.example.appsenkaspi

import java.util.Date

data class AtividadeJson(
    val nome: String,
    val descricao: String,
    val dataInicio: Date,
    val dataPrazo: Date,
    val acaoId: Int,
    val status: StatusAtividade,
    val prioridade: PrioridadeAtividade,
    val criadoPor: Int,
    val dataCriacao: Date,
    val responsaveis: List<Int>
)
