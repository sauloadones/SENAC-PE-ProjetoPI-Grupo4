package com.example.appsenkaspi

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class AcaoComFuncionarios(
    @Embedded val acao: AcaoEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = AcaoFuncionarioEntity::class,
            parentColumn = "acaoId",
            entityColumn = "funcionarioId"
        )
    )
    val funcionarios: List<FuncionarioEntity>
)
