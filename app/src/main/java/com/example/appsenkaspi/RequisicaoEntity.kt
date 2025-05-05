package com.example.appsenkaspi

import androidx.room.*
import java.util.Date

@Entity(
    tableName = "requisicoes",
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
            childColumns = ["solicitanteId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = FuncionarioEntity::class,
            parentColumns = ["id"],
            childColumns = ["coordenadorId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("pilarId"), Index("solicitanteId"), Index("coordenadorId")]
)
data class RequisicaoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,

    val tipo: TipoRequisicao,
    val status: StatusRequisicao = StatusRequisicao.PENDENTE,

    val pilarId: Int,

    val atividadeJson: String? = null,
    val acaoJson: String? = null,

    val solicitanteId: Int,
    val coordenadorId: Int? = null,

    val mensagemRetorno: String? = null,

    val dataCriacao: Date = Date(),
    val dataResposta: Date? = null
)
