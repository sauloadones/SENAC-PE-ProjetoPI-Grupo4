package com.example.appsenkaspi

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.time.LocalDate
import java.util.Calendar
import java.util.Date
import kotlin.collections.*

class PilarViewModel(application: Application) : AndroidViewModel(application) {

    private val pilarDao = AppDatabase.getDatabase(application).pilarDao()
    private val acaoDao = AppDatabase.getDatabase(application).acaoDao()
    private val subpilarDao = AppDatabase.getDatabase(application).subpilarDao()
    val statusAtivos = listOf(StatusPilar.EM_ANDAMENTO, StatusPilar.PLANEJADO)
  val statusHistorico = listOf(StatusPilar.CONCLUIDO, StatusPilar.VENCIDO, StatusPilar.EXCLUIDO)
  val statusParaDashboard = listOf(
    StatusPilar.PLANEJADO,
    StatusPilar.EM_ANDAMENTO,
    StatusPilar.CONCLUIDO
  )


  fun getPilarById(id: Int): LiveData<PilarEntity?> = pilarDao.getPilarById(id)

    fun listarTodosPilares(): LiveData<List<PilarEntity>> = pilarDao.listarTodosPilares()

    fun inserir(pilar: PilarEntity) = viewModelScope.launch {
        pilarDao.inserirPilar(pilar)
    }

    fun atualizar(pilar: PilarEntity) = viewModelScope.launch {
        pilarDao.atualizarPilar(pilar)
    }

    fun deletar(pilar: PilarEntity) = viewModelScope.launch {
        pilarDao.deletarPilar(pilar)
    }

  fun excluirPilar(pilar: PilarEntity) = viewModelScope.launch(Dispatchers.IO) {
    val hoje = Calendar.getInstance().time
    Log.d("ExcluirPilar", "Enviando para DAO → id=${pilar.id}, status=${StatusPilar.EXCLUIDO.name}, data=$hoje")


    val linhasAfetadas = pilarDao.excluirPilarPorId(pilar.id, StatusPilar.EXCLUIDO, hoje)

    Log.d("ExcluirPilar", "Linhas afetadas: $linhasAfetadas")

  }




  suspend fun inserirRetornandoId(pilar: PilarEntity): Long = pilarDao.inserirPilar(pilar)

    fun calcularProgressoDoPilar(pilarId: Int, callback: (Float) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val progresso = calcularProgressoInterno(pilarId)
            withContext(Dispatchers.Main) {
                callback(progresso)
            }
        }
    }
    suspend fun temSubpilares(pilarId: Int): Boolean {
      return subpilarDao.contarSubpilaresDoPilar(pilarId) > 0
    }

  suspend fun temSubpilaresDireto(pilarId: Int): Boolean {
    return AppDatabase.getDatabase(getApplication()).subpilarDao().getQuantidadePorPilar(pilarId) > 0
  }



  // Função suspensa reutilizável
    suspend fun calcularProgressoInterno(pilarId: Int): Float = coroutineScope {
      val subpilares = subpilarDao.listarSubpilaresPorTelaPilar(pilarId)

      if (subpilares.isNotEmpty()) {
        val subProgressoList = subpilares.map { subpilar ->
          async {
            ProgressoSubpilar.calcularProgressoDoSubpilarInterno(subpilar.id, acaoDao)
          }
        }.awaitAll()

        subProgressoList.average().toFloat()
      } else {
        val lista = acaoDao.listarProgressoPorPilar(pilarId)
        val (somaPesos, somaTotalAtividades) = lista.fold(0f to 0) { acc, item ->
          val pesoAtual = item.progresso * item.totalAtividades
          (acc.first + pesoAtual) to (acc.second + item.totalAtividades)
        }
        if (somaTotalAtividades > 0) somaPesos / somaTotalAtividades else 0f
      }
    }
  suspend fun atualizarStatusAutomaticamente(pilarId: Int) {
    val pilar = pilarDao.getById(pilarId) ?: return
    val progresso = calcularProgressoInterno(pilarId)
    val hoje = Calendar.getInstance().time

    if (pilar.status == StatusPilar.EXCLUIDO) return

    // Se for CONCLUIDO e progresso ainda >= 1 → mantém concluído
    if (pilar.status == StatusPilar.CONCLUIDO && progresso >= 1f) {
      Log.d("StatusAuto", "Mantendo CONCLUIDO pois foi definido manualmente.")
      return
    }

    val novoStatus = when {
      progresso >= 1f && hoje.after(pilar.dataPrazo) -> StatusPilar.CONCLUIDO
      progresso < 1f && pilar.status == StatusPilar.CONCLUIDO -> StatusPilar.EM_ANDAMENTO
      hoje.after(pilar.dataPrazo) -> StatusPilar.VENCIDO
      progresso == 0f -> StatusPilar.PLANEJADO
      else -> StatusPilar.EM_ANDAMENTO
    }

    if (novoStatus != pilar.status) {
      val novoPilar = if (novoStatus == StatusPilar.CONCLUIDO) {
        pilar.copy(status = novoStatus, dataConclusao = hoje)
      } else {
        pilar.copy(status = novoStatus)
      }
      pilarDao.atualizarPilar(novoPilar)
    }
  }






  fun atualizarStatusDeTodosOsPilares() = viewModelScope.launch(Dispatchers.IO) {
    val todosPilares = pilarDao.getTodosPilares()
    todosPilares.forEach { pilar ->
      atualizarStatusAutomaticamente(pilar.id)
    }
  }

  suspend fun podeConcluirPilar(pilarId: Int, dataVencimento: LocalDate): Boolean {
    val progresso = calcularProgressoInterno(pilarId)
    val hoje = LocalDate.now()

    return progresso >= 1f && (hoje.isBefore(dataVencimento) || hoje.isEqual(dataVencimento))
  }

  fun concluirPilar(pilarId: Int) = viewModelScope.launch(Dispatchers.IO) {
    val hoje = Calendar.getInstance().time

    Log.d("ConcluirPilar", "Atualizando status para CONCLUIDO com dataConclusao = $hoje para pilarId=$pilarId")

    val linhasAfetadas = pilarDao.atualizarStatusEDataConclusao(
      pilarId,
      StatusPilar.CONCLUIDO, // ✅ enum diretamente, sem .name nem .lowercase()
      hoje
    )

    Log.d("ConcluirPilar", "Linhas afetadas na atualização: $linhasAfetadas")
  }







  fun listarPilaresAtivos() = pilarDao.listarPilaresPorStatus(statusAtivos)
  fun listarPilaresHistorico() = pilarDao.listarPilaresPorStatus(statusHistorico)

  fun listarIdsENomes(): LiveData<List<PilarNomeDTO>> {
    return AppDatabase.getDatabase(getApplication()).pilarDao().listarIdsENomesPorStatus(statusParaDashboard)
  }

  fun listarAcaoIdsENomes(): LiveData<List<PilarNomeDTO>> {
    return AppDatabase.getDatabase(getApplication()).pilarDao().listarIdsENomesPorStatus(statusParaDashboard)
  }

  suspend fun getPilaresParaDashboard(): List<PilarEntity> {
    return pilarDao.getPilaresPorStatus(statusParaDashboard)
  }

  suspend fun getTodosPilares(): List<PilarEntity> = withContext(Dispatchers.IO) {
        pilarDao.getTodosPilares()
    }

    fun listarPilaresPorStatusEData(
        status: StatusPilar,
        dataExclusao: String? = null
    ): LiveData<List<PilarEntity>> {
        return if (dataExclusao.isNullOrEmpty()) {
            pilarDao.listarPilaresPorStatus(status)
        } else {
            pilarDao.listarPilaresPorStatusEData(status, dataExclusao)
        }
    }
  suspend fun calcularProgressoDosSubpilares(pilarId: Int): List<Pair<String, Float>> {
    val subpilares = AppDatabase.getDatabase(getApplication()).subpilarDao().listarPorPilar(pilarId)
    val acaoDao = AppDatabase.getDatabase(getApplication()).acaoDao()
    val atividadeDao = AppDatabase.getDatabase(getApplication()).atividadeDao()

    val resultado = mutableListOf<Pair<String, Float>>()

    for (subpilar in subpilares) {
      val acoes = acaoDao.listarPorSubpilares(subpilar.id)
      val atividades = acoes.flatMap { acao ->
        acao.id?.let { atividadeDao.listarPorAcao(it) } ?: emptyList()
      }


      val total = atividades.size
      val concluidas = atividades.count { it.status == StatusAtividade.CONCLUIDA }

      val progresso = if (total > 0) concluidas.toFloat() / total else 0f
      resultado.add(subpilar.nome to progresso)
    }

    return resultado
  }
  fun gerarResumoPorSubpilares(pilarId: Int, callback: (ResumoDashboard) -> Unit) {
    viewModelScope.launch(Dispatchers.IO) {
      val db = AppDatabase.getDatabase(getApplication())
      val subpilares = db.subpilarDao().listarPorPilarDireto(pilarId)
      val acaoDao = db.acaoDao()
      val atividadeDao = db.atividadeDao()

      var totalAcoes = 0
      var totalAtividades = 0
      var atividadesConcluidas = 0
      var atividadesAndamento = 0
      var atividadesAtraso = 0

      for (sub in subpilares) {
        val acoes = acaoDao.listarPorSubpilares(sub.id)
          totalAcoes += acoes.size

        for (acao in acoes) {
          val atividades = acao.id?.let { atividadeDao.listarPorAcao(it) } ?: emptyList()
          totalAtividades += atividades.size
          atividadesConcluidas += atividades.count { it.status == StatusAtividade.CONCLUIDA }
          atividadesAndamento += atividades.count { it.status == StatusAtividade.EM_ANDAMENTO }
          atividadesAtraso += atividades.count { it.status == StatusAtividade.VENCIDA }
        }
      }

      val resumo = ResumoDashboard(
        totalAcoes = totalAcoes,
        totalAtividades = totalAtividades,
        atividadesConcluidas = atividadesConcluidas,
        atividadesAndamento = atividadesAndamento,
        atividadesAtraso = atividadesAtraso
      )

      withContext(Dispatchers.Main) {
        callback(resumo)
      }
    }
  }
  suspend fun gerarResumoPorSubpilaresDireto(pilarId: Int): ResumoDashboard {
    val db = AppDatabase.getDatabase(getApplication())
    val subpilares = db.subpilarDao().listarPorPilarDireto(pilarId)
    val acaoDao = db.acaoDao()
    val atividadeDao = db.atividadeDao()

    var totalAcoes = 0
    var totalAtividades = 0
    var atividadesConcluidas = 0
    var atividadesAndamento = 0
    var atividadesAtraso = 0

    for (sub in subpilares) {
      val acoes = acaoDao.listarPorSubpilares(sub.id)
      totalAcoes += acoes.size

      for (acao in acoes) {
        val atividades = acao.id?.let { atividadeDao.listarPorAcao(it) } ?: emptyList()
        totalAtividades += atividades.size
        atividadesConcluidas += atividades.count { it.status == StatusAtividade.CONCLUIDA }
        atividadesAndamento += atividades.count { it.status == StatusAtividade.PENDENTE }
        atividadesAtraso += atividades.count { it.status == StatusAtividade.VENCIDA }
      }
    }

    return ResumoDashboard(
      totalAcoes,
      totalAtividades,
      atividadesConcluidas,
      atividadesAndamento,
      atividadesAtraso
    )
  }




}
