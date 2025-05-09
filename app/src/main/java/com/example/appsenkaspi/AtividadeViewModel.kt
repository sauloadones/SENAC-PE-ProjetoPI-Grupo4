package com.example.appsenkaspi

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

class AtividadeViewModel(application: Application) : AndroidViewModel(application) {

  private val atividadeDao = AppDatabase.getDatabase(application).atividadeDao()
  private val atividadeFuncionarioDao =
    AppDatabase.getDatabase(application).atividadeFuncionarioDao()
  private val requisicaoDao = AppDatabase.getDatabase(application).requisicaoDao()

  fun deletarAtividadePorId(id: Int) = viewModelScope.launch {
    atividadeDao.deletarPorId(id)
  }

  fun listarAtividadesPorAcao(acaoId: Int): LiveData<List<AtividadeEntity>> {
    return atividadeDao.listarAtividadesPorAcao(acaoId)
  }

  fun inserir(atividade: AtividadeEntity, onComplete: (Int) -> Unit = {}) = viewModelScope.launch {
    val id = atividadeDao.inserirComRetorno(atividade).toInt()
    onComplete(id)
  }

  fun listarAtividadesComFuncionariosPorAcao(acaoId: Int): LiveData<List<AtividadeComFuncionarios>> {
    return atividadeDao.listarAtividadesComFuncionariosPorAcao(acaoId)
  }

  suspend fun inserirComRetorno(atividade: AtividadeEntity): Int {
    return atividadeDao.inserirComRetorno(atividade).toInt()
  }

  fun getAtividadeComFuncionariosById(id: Int): LiveData<AtividadeComFuncionarios> {
    return atividadeDao.getAtividadeComFuncionariosPorId(id)
  }

  fun getAtividadeById(id: Int): LiveData<AtividadeEntity> {
    return atividadeDao.getAtividadeById(id)
  }

  fun contarTotalPorAcao(acaoId: Int): LiveData<Int> {
    return atividadeDao.contarTotalPorAcao(acaoId)
  }

  fun contarConcluidasPorAcao(acaoId: Int): LiveData<Int> {
    return atividadeDao.contarConcluidasPorAcao(acaoId)
  }

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

  fun listarAtividadesParaFuncionario(funcionarioId: Int): LiveData<List<AtividadeEntity>> {
    return atividadeDao.listarAtividadesPorFuncionario(funcionarioId)
  }

  fun listarAtividadesPorResponsavel(funcionarioId: Int): LiveData<List<AtividadeEntity>> {
    return atividadeDao.listarAtividadesDoFuncionario(funcionarioId)
  }

  fun getAtividadesDoFuncionarioComResponsaveis(funcionarioId: Int): LiveData<List<AtividadeComFuncionarios>> {
    return atividadeDao.listarAtividadesComResponsaveis(funcionarioId)
  }

  fun getAtividadesDoFuncionario(funcionarioId: Int): LiveData<List<AtividadeComFuncionarios>> {
    return atividadeDao.listarAtividadesComResponsaveis(funcionarioId)
  }

  fun verificarAtividadesComPrazoProximo() {
    viewModelScope.launch {
      val hoje = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
      }.time

      val seteDias = Calendar.getInstance().apply {
        time = hoje
        add(Calendar.DAY_OF_YEAR, 7)
      }.time

      val atividades = atividadeDao.getTodasAtividadesComDataPrazo()

      for (atividade in atividades) {
        val prazo = atividade.dataPrazo ?: continue

        if (
          atividade.status != StatusAtividade.CONCLUIDA &&
          (prazo == hoje || (prazo.after(hoje) && prazo.before(seteDias)))
        ) {
          val diasRestantes = ((prazo.time - hoje.time) / (1000 * 60 * 60 * 24)).toInt()
          val mensagem =
            "A atividade está a $diasRestantes dia${if (diasRestantes != 1) "s" else ""} do prazo final e aguarda sua atenção."

          val responsaveis = atividadeFuncionarioDao.getResponsaveisDaAtividade(atividade.id)

          for (responsavel in responsaveis) {
            val jaExiste = requisicaoDao.getRequisicaoPorAtividade(
              atividadeId = atividade.id,
              solicitanteId = responsavel.id,
              tipo = TipoRequisicao.ATIVIDADE_PARA_VENCER
            )

            if (jaExiste == null) {
              requisicaoDao.insert(
                RequisicaoEntity(
                  tipo = TipoRequisicao.ATIVIDADE_PARA_VENCER,
                  atividadeId = atividade.id,
                  solicitanteId = responsavel.id,
                  status = StatusRequisicao.PENDENTE,
                  dataSolicitacao = Date(),
                  mensagemResposta = mensagem
                )
              )
            }
          }
        }
      }
    }
  }
}
