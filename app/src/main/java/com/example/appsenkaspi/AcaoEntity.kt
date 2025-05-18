package com.example.appsenkaspi

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date


@Entity(
    tableName = "acoes",
    foreignKeys = [
        ForeignKey(
            entity = PilarEntity::class,
            parentColumns = ["id"],
            childColumns = ["pilarId"],
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
        Index(value = ["pilarId"]),
        Index(value = ["criado_por"])
    ]
)
data class AcaoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int? = 0,
    val nome: String,
    val descricao: String,
    val dataInicio: Date,
    val dataPrazo: Date,
    val pilarId: Int,
    @ColumnInfo(name = "status") val status: StatusAcao,

    @ColumnInfo(name = "criado_por")
    val criadoPor: Int,
    @ColumnInfo(name = "data_criacao")
    val dataCriacao: Date



)
