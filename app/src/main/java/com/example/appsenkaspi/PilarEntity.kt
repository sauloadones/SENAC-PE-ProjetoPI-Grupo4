package com.example.appsenkaspi

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.ForeignKey
import androidx.room.Index
import com.example.appsenkaspi.Converters.StatusPilar
import java.util.Date

@Entity(tableName = "pilares",
        foreignKeys = [
            ForeignKey(
                entity = FuncionarioEntity::class,
                parentColumns = ["id"],
                childColumns = ["criadoPor"],
                onDelete = ForeignKey.CASCADE
            )
            ],
        indices = [Index(value = ["criadoPor"])]
)
data class PilarEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nome: String,
    val descricao: String,
    val dataInicio: Date,
    val dataPrazo: Date,
    val dataCriacao: Date,
    val criadoPor: Int.Companion,

    @ColumnInfo(name = "status") val status: StatusPilar,


    )
