package com.example.appsenkaspi

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.*

class AtividadeViewModel(application: Application) : AndroidViewModel(application) {

  private val atividadeDao = AppDatabase.getDatabase(application).atividadeDao()
  private val atividadeFuncionarioDao = AppDatabase.getDatabase(application).atividadeFuncionarioDao()
  private val requisicaoDao = AppDatabase.getDatabase(application).requisicaoDao()
  val repository = AtividadeRepository(atividadeDao, atividadeFuncionarioDao, requisicaoDao)

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
  }


  fun checarPrazos() = viewModelScope.launch {
    repository.verificarNotificacoesDePrazo()
  }

  fun concluirAtividade(atividade: AtividadeEntity) = viewModelScope.launch {
    val atual = atividadeDao.getAtividadePorIdDireto(atividade.id ?: return@launch)

    if (atual.status == StatusAtividade.CONCLUIDA) return@launch

    val hoje = Date()
    val prazo = atual.dataPrazo

    if (prazo != null && prazo.before(hoje)) {
      Log.w("ATIVIDADE", "Tentativa de concluir atividade vencida: ${atual.nome}")
      return@launch
    }

    val atualizado = atual.copy(status = StatusAtividade.CONCLUIDA)
    repository.tratarConclusaoAtividade(atualizado)
  }



  fun checarAtividadesVencidas() = viewModelScope.launch {
    repository.verificarAtividadesVencidas()
  }



  val notificacoes: LiveData<List<RequisicaoEntity>> = requisicaoDao.getTodasNotificacoes()

  fun verificarVencimentos() {
    viewModelScope.launch {
      val repo = AtividadeRepository(
        AppDatabase.getDatabase(getApplication()).atividadeDao(),
        AppDatabase.getDatabase(getApplication()).atividadeFuncionarioDao(),
        AppDatabase.getDatabase(getApplication()).requisicaoDao()
      )
      repo.verificarAtividadesVencidas()
    }
  }



}
