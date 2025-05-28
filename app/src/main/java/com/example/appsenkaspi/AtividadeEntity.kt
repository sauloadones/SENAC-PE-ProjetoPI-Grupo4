package com.example.appsenkaspi

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

import java.util.Date

@Entity(
    tableName = "atividades",
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
        ),
        ForeignKey(
            entity = FuncionarioEntity::class,
            parentColumns = ["id"],
            childColumns = ["criado_por"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["acaoId"]),
        Index(value = ["funcionarioId"]),
        Index(value = ["criado_por"])
    ]
)
data class AtividadeEntity(
    @PrimaryKey(autoGenerate = true) var id: Int? = null,
    val nome: String,
    val descricao: String,
    val dataInicio: Date,
    val dataPrazo: Date,
    val acaoId: Int,
    val funcionarioId: Int,
    @ColumnInfo(name = "status") val status: StatusAtividade,
    @ColumnInfo(name = "prioridade") val prioridade: PrioridadeAtividade,
    @ColumnInfo(name = "criado_por") val criadoPor: Int,
    @ColumnInfo(name = "data_criacao") val dataCriacao: Date,
)
