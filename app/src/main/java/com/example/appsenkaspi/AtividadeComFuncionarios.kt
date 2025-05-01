package com.example.appsenkaspi

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class AtividadeComFuncionarios(
    @Embedded val atividade: AtividadeEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = AtividadeFuncionarioEntity::class,
            parentColumn = "atividadeId",
            entityColumn = "funcionarioId"
        )
    )
    val funcionarios: List<FuncionarioEntity>
)
