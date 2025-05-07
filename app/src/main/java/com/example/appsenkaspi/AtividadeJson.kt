package com.example.appsenkaspi

import java.util.Date

data class AtividadeJson(

    val id: Int? = null,  // <- obrigatório para update

    val nome: String,
    val descricao: String,
    val dataInicio: Date,
    val dataPrazo: Date,
    val acaoId: Int,
    val status: StatusAtividade,
    val prioridade: PrioridadeAtividade,
    val criadoPor: Int,
    val dataCriacao: Date,
    val nomePilar: String,

    val responsaveis: List<Int>
)
