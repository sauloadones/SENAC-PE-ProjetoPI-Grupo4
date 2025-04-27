package com.example.appsenkaspi

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "pilares")
data class PilarEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nome: String,
    val descricao: String,
    val dataPrazo: Date? = null // Agora é Date no banco!
)

