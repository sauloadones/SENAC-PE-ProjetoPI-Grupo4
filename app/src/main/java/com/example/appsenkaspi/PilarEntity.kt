package com.example.appsenkaspi

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import com.example.appsenkaspi.Converters.Cargo
import com.example.appsenkaspi.Converters.StatusPilar
import java.util.Date

@Entity(tableName = "pilares")
data class PilarEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nome: String,
    val descricao: String,
    val dataInicio: Date,
    val dataPrazo: Date,
    val dataCriacao: Date,

    @ColumnInfo(name = "status") val status: StatusPilar,


)
