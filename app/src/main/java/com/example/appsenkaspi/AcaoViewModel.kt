package com.example.appsenkaspi


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope


import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class AcaoViewModel(application: Application) : AndroidViewModel(application) {
    private val pilarDao = AppDatabase.getDatabase(application).pilarDao()
    private val dao = AppDatabase.getDatabase(application).acaoDao()

    private val acaoDao = AppDatabase.getDatabase(application).acaoDao()
    private val atividadeDao = AppDatabase.getDatabase(application).atividadeDao()
    private val subpilarDao = AppDatabase.getDatabase(application).subpilarDao()

  fun getAcaoById(id: Int): LiveData<AcaoEntity?> {
        return acaoDao.getAcaoById(id)
    }
    suspend fun getAcaoByIdNow(id: Int): AcaoEntity? {
        return acaoDao.getAcaoByIdNow(id)
    }


    fun listarAcoesPorPilar(pilarId: Int): LiveData<List<AcaoComStatus>> {
        return acaoDao.listarPorPilar(pilarId)
    }

  fun listarAcoesPorSubpilar(subpilarId: Int): LiveData<List<AcaoComStatus>> {
    return acaoDao.listarPorSubpilar(subpilarId)
  }


  fun inserir(acao: AcaoEntity) = viewModelScope.launch {
        acaoDao.inserirAcao(acao)
    }

    fun atualizar(acao: AcaoEntity) = viewModelScope.launch {
        acaoDao.atualizarAcao(acao)
    }

    fun deletar(acao: AcaoEntity) = viewModelScope.launch {
        acaoDao.deletarAcao(acao)
    }
    suspend fun inserirRetornandoId(acao: AcaoEntity): Int {
        return acaoDao.inserirComRetorno(acao).toInt()
    }

  suspend fun gerarResumoDashboardDireto(pilarId: Int): ResumoDashboard = withContext(Dispatchers.IO) {
    val acoes = acaoDao.listarAcoesComStatusPorPilarNow(pilarId)

    val totalAcoes = acoes.size
    val totalAtividades = acoes.sumOf { it.totalAtividades }
    val concluidas = acoes.sumOf { it.ativasConcluidas }

    val vencidas = AppDatabase.getDatabase(getApplication())
      .atividadeDao()
      .contarVencidasPorPilar(pilarId)

    val andamento = totalAtividades - concluidas - vencidas

    return@withContext ResumoDashboard(
      totalAcoes = totalAcoes,
      totalAtividades = totalAtividades,
      atividadesConcluidas = concluidas,
      atividadesAndamento = andamento,
      atividadesAtraso = vencidas
    )
  }




  fun atualizarStatusAcaoAutomaticamente(acaoId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val total = atividadeDao.contarTotalPorAcaoValor(acaoId)
            val concluidas = atividadeDao.contarConcluidasPorAcaoValor(acaoId)

            val acao = acaoDao.getAcaoPorIdDireto(acaoId) ?: return@launch
            val hoje = Calendar.getInstance().time

            val novoStatus = when {
                total == 0 -> StatusAcao.PLANEJADA
                concluidas == total && hoje.before(acao.dataPrazo) -> StatusAcao.CONCLUIDA
                hoje.after(acao.dataPrazo) -> StatusAcao.VENCIDA
                else -> StatusAcao.EM_ANDAMENTO
            }

            if (acao.status != novoStatus) {
                val acaoAtualizada = acao.copy(status = novoStatus)
                acaoDao.atualizarAcao(acaoAtualizada)

            }
        }
    }

  suspend fun criarAcaoSegura(acao: AcaoEntity): Long {
    val valido = (acao.pilarId != null) xor (acao.subpilarId != null)
    if (!valido) {
      throw IllegalArgumentException("A ação deve estar ligada a um pilar OU subpilar, nunca ambos ou nenhum.")
    }
    return dao.inserirRetornandoId(acao)
  }
  suspend fun buscarAcaoPorId(id: Int): AcaoEntity? {
        return acaoDao.getAcaoPorId(id)
    }

  suspend fun buscarNomeSubpilarPorId(subpilarId: Int): String? {
    return subpilarDao.buscarNomeSubpilarPorId(subpilarId)
  }


    suspend fun buscarPilarPorId(id: Int): PilarEntity? {
        return pilarDao.getPilarPorId(id)
    }

  fun gerarResumoDashboard(pilarId: Int?, callback: (ResumoDashboard) -> Unit) {
    viewModelScope.launch(Dispatchers.IO) {
      val acoes = if (pilarId == null) {
        val statusValidos = listOf(StatusPilar.PLANEJADO, StatusPilar.EM_ANDAMENTO, StatusPilar.CONCLUIDO)
        val pilaresValidos = pilarDao.getPilaresPorStatus(statusValidos)
        val idsValidos = pilaresValidos.map { it.id }
        acaoDao.listarAcoesComStatusPorPilares(idsValidos)
      } else {
        acaoDao.listarAcoesComStatusPorPilarNow(pilarId)
      }

      val totalAcoes = acoes.size
      val totalAtividades = acoes.sumOf { it.totalAtividades }
      val concluidas = acoes.sumOf { it.ativasConcluidas }

      val vencidas = if (pilarId == null) {
        AppDatabase.getDatabase(getApplication()).atividadeDao().contarVencidasGeral()
      } else {
        AppDatabase.getDatabase(getApplication()).atividadeDao().contarVencidasPorPilar(pilarId)
      }

      val andamento = totalAtividades - concluidas - vencidas

      withContext(Dispatchers.Main) {
        callback(
          ResumoDashboard(
            totalAcoes = totalAcoes,
            totalAtividades = totalAtividades,
            atividadesConcluidas = concluidas,
            atividadesAndamento = andamento,
            atividadesAtraso = vencidas
          )
        )
      }
    }
  }
  suspend fun buscarSubpilarPorId(id: Int): SubpilarEntity? {
    return subpilarDao.getSubpilarPorId(id)
  }




}
