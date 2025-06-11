package com.example.appsenkaspi.domain.model

import com.example.appsenkaspi.ui.requisicao.DadosRequisicao
import androidx.room.Embedded
import androidx.room.Relation
import com.example.appsenkaspi.data.local.entity.NotificacaoEntity
import com.example.appsenkaspi.data.local.entity.RequisicaoEntity
import com.example.appsenkaspi.data.local.entity.FuncionarioEntity

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
