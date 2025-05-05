package com.example.appsenkaspi.Notificacao

import DadosRequisicao
import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.Relation
import com.example.appsenkaspi.FuncionarioEntity
import com.example.appsenkaspi.NotificacaoEntity
import com.example.appsenkaspi.RequisicaoEntity


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
