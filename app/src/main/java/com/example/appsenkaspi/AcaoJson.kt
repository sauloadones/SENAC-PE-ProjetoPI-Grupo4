package com.example.appsenkaspi

import java.util.Date

data class AcaoJson(
    val id: Int? = null,  // <- obrigatório para update
    val responsaveis: List<Int>,
    val nome: String,
    val descricao: String,
    val dataInicio: Date,
    val dataPrazo: Date,
    val status: StatusAcao,
    val criadoPor: Int,
    val dataCriacao: Date,
    val nomePilar: String,
    val pilarId: Int? = null,        // ← aqui está o ponto importante
    val subpilarId: Int? = null

)
