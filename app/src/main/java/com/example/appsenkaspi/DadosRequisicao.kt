import com.example.appsenkaspi.AcaoEntity
import com.example.appsenkaspi.AtividadeEntity

sealed class DadosRequisicao {
    data class CriarAtividade(val atividade: AtividadeEntity) : DadosRequisicao()
    data class EditarAtividade(val atividade: AtividadeEntity) : DadosRequisicao()
    data class CompletarAtividade(val atividade: AtividadeEntity) : DadosRequisicao()
    data class CriarAcao(val acao: AcaoEntity) : DadosRequisicao()
    data class EditarAcao(val acao: AcaoEntity) : DadosRequisicao()
}
