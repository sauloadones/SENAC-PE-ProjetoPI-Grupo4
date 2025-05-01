package com.example.appsenkaspi

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "atividades_funcionarios",
    primaryKeys = ["atividadeId", "funcionarioId"],
    foreignKeys = [
        ForeignKey(
            entity = AtividadeEntity::class,
            parentColumns = ["id"],
            childColumns = ["atividadeId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = FuncionarioEntity::class,
            parentColumns = ["id"],
            childColumns = ["funcionarioId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class AtividadeFuncionarioEntity(
    val atividadeId: Int,
    val funcionarioId: Int
)
