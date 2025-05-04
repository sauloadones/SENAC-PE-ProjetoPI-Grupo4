package com.example.appsenkaspi

import androidx.room.*
import com.example.appsenkaspi.Converters.StatusRequisicao
import com.example.appsenkaspi.Converters.TipoRequisicao
import java.util.*

@Entity(
    tableName = "requisicoes",
    foreignKeys = [
        ForeignKey(
            entity = FuncionarioEntity::class,
            parentColumns = ["id"],
            childColumns = ["idSolicitante"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["idSolicitante"])]
)
data class RequisicaoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val tipo: TipoRequisicao,
    val status: StatusRequisicao = StatusRequisicao.PENDENTE,
    val idSolicitante: Int,
    val idReferencia: Int?, // pode referenciar pilar, ação, atividade etc.
    val dataCriacao: Date = Date()
)
