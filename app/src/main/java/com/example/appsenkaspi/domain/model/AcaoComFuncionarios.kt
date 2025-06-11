package com.example.appsenkaspi.domain.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.appsenkaspi.data.local.entity.AcaoFuncionarioEntity
import com.example.appsenkaspi.data.local.entity.FuncionarioEntity
import com.example.appsenkaspi.data.local.entity.AcaoEntity

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
