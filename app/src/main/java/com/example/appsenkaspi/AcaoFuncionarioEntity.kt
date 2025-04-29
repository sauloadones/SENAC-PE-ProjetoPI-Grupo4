package com.example.appsenkaspi

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "acoes_funcionarios",
    primaryKeys = ["acaoId", "funcionarioId"],
    foreignKeys = [
        ForeignKey(
            entity = AcaoEntity::class,
            parentColumns = ["id"],
            childColumns = ["acaoId"],
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
data class AcaoFuncionarioEntity(
    val acaoId: Int,
    val funcionarioId: Int
)
