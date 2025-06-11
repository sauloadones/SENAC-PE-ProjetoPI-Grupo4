package com.example.appsenkaspi.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.appsenkaspi.ui.atividade.AtividadeRepository
import com.example.appsenkaspi.data.local.entity.RequisicaoEntity
import com.example.appsenkaspi.data.local.enums.StatusAtividade
import com.example.appsenkaspi.data.local.database.AppDatabase
import com.example.appsenkaspi.data.local.entity.AtividadeEntity
import com.example.appsenkaspi.data.local.entity.AtividadeFuncionarioEntity
import com.example.appsenkaspi.domain.model.AtividadeComFuncionarios
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

class AtividadeViewModel(application: Application) : AndroidViewModel(application) {
  val context = getApplication<Application>().applicationContext

  private val atividadeDao = AppDatabase.Companion.getDatabase(application).atividadeDao()
  private val atividadeFuncionarioDao = AppDatabase.Companion.getDatabase(application).atividadeFuncionarioDao()
  private val requisicaoDao = AppDatabase.Companion.getDatabase(application).requisicaoDao()
  val repository = AtividadeRepository(
      context,
      atividadeDao,
      atividadeFuncionarioDao,
      requisicaoDao
  )

  fun deletarAtividadePorId(id: Int) = viewModelScope.launch {
    atividadeDao.deletarPorId(id)
  }



  fun inserir(atividade: AtividadeEntity, onComplete: (Int) -> Unit = {}) = viewModelScope.launch {
    val id = atividadeDao.inserirComRetorno(atividade).toInt()
    onComplete(id)
  }

  fun listarAtividadesComFuncionariosPorAcao(acaoId: Int): LiveData<List<AtividadeComFuncionarios>> =
    atividadeDao.listarAtividadesComFuncionariosPorAcao(acaoId)



  fun getAtividadeComFuncionariosById(id: Int): LiveData<AtividadeComFuncionarios> =
    atividadeDao.getAtividadeComFuncionariosPorId(id)

  fun getAtividadeById(id: Int): LiveData<AtividadeEntity> =
    atividadeDao.getAtividadeById(id)



  fun inserirRelacaoFuncionario(relacao: AtividadeFuncionarioEntity) = viewModelScope.launch {
    atividadeFuncionarioDao.inserirAtividadeFuncionario(relacao)
  }

  fun atualizar(atividade: AtividadeEntity) = viewModelScope.launch {
    atividadeDao.atualizarAtividade(atividade)
  }

  fun deletar(atividade: AtividadeEntity) = viewModelScope.launch {
    atividadeDao.deletarAtividade(atividade)
  }

  suspend fun deletarRelacoesPorAtividade(atividadeId: Int) {
    atividadeFuncionarioDao.deletarPorAtividade(atividadeId)
  }

  fun salvarEdicaoAtividade(
      atividadeEditada: AtividadeEntity,
      atividadeAntiga: AtividadeEntity
  ) = viewModelScope.launch {
    atividadeDao.update(atividadeEditada)
    repository.tratarAlteracaoPrazo(atividadeEditada, atividadeAntiga)
    repository.verificarAtividadesVencidas()
  }


  fun checarPrazos() = viewModelScope.launch {
    repository.verificarNotificacoesDePrazo()
  }

  fun concluirAtividade(atividade: AtividadeEntity) = viewModelScope.launch {
    val atual = atividadeDao.getAtividadePorIdDireto(atividade.id ?: return@launch)

    if (atual.status == StatusAtividade.CONCLUIDA) return@launch

    val prazo = atual.dataPrazo
    if (prazo != null && isPrazoVencidoOuHoje(prazo)) {
      Log.w("ATIVIDADE", "Tentativa de concluir atividade vencida ou no dia do prazo: ${atual.nome}")
      return@launch
    }

    val atualizado = atual.copy(status = StatusAtividade.CONCLUIDA)
    repository.tratarConclusaoAtividade(atualizado)
  }




  fun checarAtividadesVencidas() = viewModelScope.launch {
    repository.verificarAtividadesVencidas()
    repository.verificarNotificacoesDeAtividadesVencidasJaMarcadas()

  }



  val notificacoes: LiveData<List<RequisicaoEntity>> = requisicaoDao.getTodasNotificacoes()

  fun verificarVencimentos() {
    viewModelScope.launch {
      val repo = AtividadeRepository(
          context,
          AppDatabase.Companion.getDatabase(getApplication()).atividadeDao(),
          AppDatabase.Companion.getDatabase(getApplication()).atividadeFuncionarioDao(),
          AppDatabase.Companion.getDatabase(getApplication()).requisicaoDao()
      )
      repo.verificarAtividadesVencidas()
    }
  }

  fun listarAtividadesComFuncionariosPorFuncionario(idFuncionario: Int): LiveData<List<AtividadeComFuncionarios>> {
    return atividadeDao.listarAtividadesComResponsaveis(idFuncionario)
  }
  fun isPrazoVencidoOuHoje(prazo: Date): Boolean {
    val formato = java.text.SimpleDateFormat("yyyy-MM-dd")
    val hojeStr = formato.format(Date())
    val prazoStr = formato.format(prazo)

    return prazoStr <= hojeStr
  }


  // Agora retorna true se a data do prazo for igual ou anterior a hoje






}
