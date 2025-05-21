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
        ),
      ForeignKey(
        entity = SubpilarEntity::class,
        parentColumns = ["id"],
        childColumns = ["subpilarId"],
        onDelete = ForeignKey.CASCADE
      )
    ],
    indices = [
        Index(value = ["pilarId"]),
        Index(value = ["criado_por"]),
        Index(value = ["subpilarId"])
    ]
)
data class AcaoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    val nome: String,
    val descricao: String,
    val dataInicio: Date,
    val dataPrazo: Date,
    val pilarId: Int? = null,
    val subpilarId: Int? = null,
    @ColumnInfo(name = "status") val status: StatusAcao,

    @ColumnInfo(name = "criado_por")
    val criadoPor: Int,
    @ColumnInfo(name = "data_criacao")
    val dataCriacao: Date



)
