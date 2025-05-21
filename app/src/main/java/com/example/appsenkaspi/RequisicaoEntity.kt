package com.example.appsenkaspi

import androidx.room.*
import java.util.Date

@Entity(
  tableName = "requisicoes",
  foreignKeys = [
    ForeignKey(
      entity = AtividadeEntity::class,
      parentColumns = ["id"],
      childColumns = ["atividadeId"],
      onDelete = ForeignKey.CASCADE
    )
  ],
  indices = [Index("atividadeId")]
)
data class RequisicaoEntity(
  @PrimaryKey(autoGenerate = true) val id: Int = 0,
  val tipo: TipoRequisicao,
  val atividadeJson: String? = null,
  val acaoJson: String? = null,
  val atividadeId: Int? = null,
  val acaoId: Long? = null,
  val solicitanteId: Int,
  val status: StatusRequisicao = StatusRequisicao.PENDENTE,
  val dataSolicitacao: Date = Date(),
  val dataResposta: Date? = null,
  val coordenadorId: Int? = null,
  val mensagemResposta: String? = null,
  val foiVista: Boolean = false,
  val resolvida: Boolean = false
)
