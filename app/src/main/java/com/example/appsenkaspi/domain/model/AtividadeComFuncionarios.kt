package com.example.appsenkaspi.domain.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.appsenkaspi.data.local.entity.AtividadeFuncionarioEntity
import com.example.appsenkaspi.data.local.entity.FuncionarioEntity
import com.example.appsenkaspi.data.local.entity.AtividadeEntity

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
