package com.example.appsenkaspi

import DadosRequisicao
import androidx.room.Embedded

import androidx.room.Relation



data class NotificacaoComRequisicaoEFuncionario(
    @Embedded val notificacao: NotificacaoEntity,

    @Relation(
        parentColumn = "requisicaoId",
        entityColumn = "id"
    )
    val requisicao: RequisicaoEntity,

    @Relation(
        parentColumn = "remetenteId",
        entityColumn = "id"
    )
    val remetente: FuncionarioEntity,

    @Relation(
        parentColumn = "destinatarioId",
        entityColumn = "id"
    )
    val destinatario: FuncionarioEntity? = null, // opcional

    @Transient
    var dadosAcaoOuAtividade: DadosRequisicao? = null
)
