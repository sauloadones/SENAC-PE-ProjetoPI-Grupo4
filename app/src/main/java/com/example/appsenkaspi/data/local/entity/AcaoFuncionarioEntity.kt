package com.example.appsenkaspi.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import com.example.appsenkaspi.data.local.entity.FuncionarioEntity

@Entity(
    tableName = "acoes_funcionarios",
    primaryKeys = ["acaoId", "funcionarioId"],
    foreignKeys = [
        ForeignKey(
            entity = AcaoEntity::class,
            parentColumns = ["id"],
            childColumns = ["acaoId"],
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
data class AcaoFuncionarioEntity(
    val acaoId: Long,
    val funcionarioId: Int
)
