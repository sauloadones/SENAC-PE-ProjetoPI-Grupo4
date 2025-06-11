package com.example.appsenkaspi.data.local.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.appsenkaspi.data.local.enums.Cargo
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(
    tableName = "funcionarios",

)
data class FuncionarioEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val nomeCompleto: String,

    val email: String,

    @ColumnInfo(name = "cargo")
    val cargo: Cargo,

    val fotoPerfil: String,

    val nomeUsuario: String,

    val senha: String,

    @ColumnInfo(name = "id_acesso")
    val idAcesso: Int = 0,

    val numeroTel: String,

    val fotoBanner: String? = null


) : Parcelable
