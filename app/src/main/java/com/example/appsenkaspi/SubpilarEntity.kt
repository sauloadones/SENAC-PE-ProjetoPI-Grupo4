package com.example.appsenkaspi

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "subpilares",
    foreignKeys = [
        ForeignKey(
            entity = PilarEntity::class,
            parentColumns = ["id"],
            childColumns = ["pilarId"],
            onDelete = ForeignKey.CASCADE
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
    val pilarId: Int
)
