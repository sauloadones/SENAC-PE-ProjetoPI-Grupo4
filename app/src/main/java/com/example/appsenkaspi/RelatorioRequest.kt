package com.example.appsenkaspi

data class RelatorioRequest(
    val tipoRelatorio: String,
    val pilarId: Int? = null,
    val pilares: List<PilarDTO>
)

data class PilarDTO(
    val nome: String,
    val descricao: String,
    val dataInicio: String,
    val dataPrazo: String,
    val status: String,
    val criadoPor: String,
    val acoes: List<AcaoDTO>
)

data class AcaoDTO(
    val nome: String,
    val descricao: String,
    val status: String,
    val atividades: List<AtividadeDTO>
)

data class AtividadeDTO(
    val nome: String,
    val descricao: String,
    val status: String,
    val responsavel: String
)