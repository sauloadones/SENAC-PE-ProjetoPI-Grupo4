package com.example.appsenkaspi

import androidx.room.Embedded
import androidx.room.Relation

data class NotificacaoComRemetente(
    @Embedded val notificacao: NotificacaoEntity,

    @Relation(
        parentColumn = "remetenteId",
        entityColumn = "id"
    )
    val remetente: FuncionarioEntity
)
