import com.example.appsenkaspi.FuncionarioEntity
import com.example.appsenkaspi.PrioridadeAtividade
import com.example.appsenkaspi.StatusAcao
import com.example.appsenkaspi.StatusAtividade
import java.util.Date

sealed class DadosRequisicao {
    data class DadosAcao(
        val nome: String,
        val descricao: String,
        val prazoFormatado: String,
        val nomePilar: String,
        val responsaveis: List<FuncionarioEntity>
    ) : DadosRequisicao()

    data class DadosAtividade(
        val nome: String,
        val descricao: String,
        val prazoFormatado: String,
        val nomePilar: String,
        val responsaveis: List<FuncionarioEntity>,
        val prioridade: PrioridadeAtividade,  // ✅ <-- Adicione isso aqui
        val dataInicio: Date,
        val dataPrazo: Date
    ) : DadosRequisicao()

}
