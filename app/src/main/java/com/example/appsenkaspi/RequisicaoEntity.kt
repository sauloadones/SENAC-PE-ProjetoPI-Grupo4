package com.example.appsenkaspi
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date



@Entity(tableName = "requisicoes")
data class RequisicaoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val tipo: TipoRequisicao,
    val atividadeJson: String? = null,
    val acaoJson: String? = null,
    val atividadeId: Int? = null,
    val acaoId: Int? = null,
    val solicitanteId: Int,
    val status: StatusRequisicao = StatusRequisicao.PENDENTE,
    val dataSolicitacao: Date = Date(),
    val dataResposta: Date? = null,
    val coordenadorId: Int? = null,
    val mensagemResposta: String? = null
)

