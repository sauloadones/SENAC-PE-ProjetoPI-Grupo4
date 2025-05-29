package com.example.appsenkaspi

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.ForeignKey
import androidx.room.Index

import java.util.Date

@Entity(
    tableName = "pilares",
    foreignKeys = [
        ForeignKey(
            entity = FuncionarioEntity::class,
            parentColumns = ["id"],
            childColumns = ["criado_por"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["criado_por"])]
)
data class PilarEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nome: String,
    val descricao: String,
    val dataInicio: Date,
    val dataPrazo: Date,
    val dataCriacao: Date,
    val dataConclusao: Date? = null, // ✅ DEVE existir
    val dataExcluido: Date? = null,
    @ColumnInfo(name = "criado_por")
    val criadoPor: Int,

    @ColumnInfo(name = "status") val status: StatusPilar,

    val dataExclusao: Date? = null

    )
