package com.example.appsenkaspi

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "funcionarios")
data class FuncionarioEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nomeCompleto: String,
    val email: String,
    val cargo: String,
    val fotoPerfil: String,
    val nomeUsuario: String,
    val senha: String
) : Parcelable