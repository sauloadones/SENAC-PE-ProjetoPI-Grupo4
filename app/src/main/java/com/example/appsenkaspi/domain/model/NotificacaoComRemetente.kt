package com.example.appsenkaspi.domain.model

import androidx.room.Embedded
import androidx.room.Relation
import com.example.appsenkaspi.data.local.entity.NotificacaoEntity
import com.example.appsenkaspi.data.local.entity.FuncionarioEntity

data class NotificacaoComRemetente(
    @Embedded val notificacao: NotificacaoEntity,

    @Relation(
        parentColumn = "remetenteId",
        entityColumn = "id"
    )
    val remetente: FuncionarioEntity
)
