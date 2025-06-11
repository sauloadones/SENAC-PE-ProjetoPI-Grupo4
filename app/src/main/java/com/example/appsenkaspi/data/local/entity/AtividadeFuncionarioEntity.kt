package com.example.appsenkaspi.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import com.example.appsenkaspi.data.local.entity.FuncionarioEntity

@Entity(
    tableName = "atividades_funcionarios",
    primaryKeys = ["atividadeId", "funcionarioId"],
    foreignKeys = [
        ForeignKey(
            entity = AtividadeEntity::class,
            parentColumns = ["id"],
            childColumns = ["atividadeId"],
            onDelete = ForeignKey.Companion.CASCADE
        ),
        ForeignKey(
            entity = FuncionarioEntity::class,
            parentColumns = ["id"],
            childColumns = ["funcionarioId"],
            onDelete = ForeignKey.Companion.CASCADE
        )
    ]
)
data class AtividadeFuncionarioEntity(
    val atividadeId: Int,
    val funcionarioId: Int
)
