package com.example.appsenkaspi.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.appsenkaspi.data.local.enums.StatusSubPilar
import java.util.Date

@Entity(
    tableName = "subpilares",
    foreignKeys = [
        ForeignKey(
            entity = PilarEntity::class,
            parentColumns = ["id"],
            childColumns = ["pilarId"],
            onDelete = ForeignKey.Companion.CASCADE
        )
    ],
    indices = [Index("pilarId")]
)
data class SubpilarEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nome: String,
    val descricao: String?,
    val dataInicio: Date,
    val dataPrazo: Date,
    val pilarId: Int,
    val dataCriacao: Date,

    @ColumnInfo(name = "status") val status: StatusSubPilar,
    @ColumnInfo(name = "criado_por")
    val criadoPor: Int
)
