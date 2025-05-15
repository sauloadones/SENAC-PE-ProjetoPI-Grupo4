package com.example.appsenkaspi

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.util.*

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

  // ✅ Gera notificações de prazo apenas para os responsáveis ainda não notificados
  fun verificarAtividadesComPrazoProximo() {
    viewModelScope.launch {
      val context = getApplication<Application>().applicationContext
      val notificationManager = NotificationManagerCompat.from(context)

      val hoje = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
      }.time

      val atividades = atividadeDao.getTodasAtividadesComDataPrazo()

      for (atividade in atividades) {
        val prazo = atividade.dataPrazo ?: continue

        // 1. Se a atividade estiver concluída → marcar notificações como resolvidas
        if (atividade.status == StatusAtividade.CONCLUIDA) {
          val antigas = requisicaoDao.getRequisicoesDePrazoPorAtividade(atividade.id)
          for (req in antigas) {
            notificationManager.cancel(req.id) // <- agora está definido corretamente
            requisicaoDao.marcarComoResolvida(req.id)
          }
          continue
        }

        val responsaveis = atividadeFuncionarioDao.getResponsaveisDaAtividade(atividade.id)
        val diasRestantes = ((prazo.time - hoje.time) / (1000 * 60 * 60 * 24)).toInt()

        // 2. Se já venceu
        if (prazo.before(hoje)) {
          val mensagem = "A atividade '${atividade.nome}' está vencida. O prazo foi $prazo."
          for (responsavel in responsaveis) {
            val jaExiste = requisicaoDao.existeRequisicaoDeVencida(atividade.id, responsavel.id)
            if (!jaExiste) {
              val requisicao = RequisicaoEntity(
                tipo = TipoRequisicao.ATIVIDADE_VENCIDA,
                atividadeId = atividade.id,
                solicitanteId = responsavel.id,
                status = StatusRequisicao.ACEITA,
                dataSolicitacao = Date(),
                mensagemResposta = mensagem,
                foiVista = false
              )
              val id = requisicaoDao.inserir(requisicao).toInt()
              enviarNotificacaoDePrazo(getApplication(), id, responsavel.nomeCompleto, mensagem)
            }
          }
          continue
        }

        // 3. Se ainda não venceu, avaliamos os marcos de notificação
        val marcos = listOf(30, 15, 7)
        val notificarHoje = diasRestantes in marcos || diasRestantes in 1..6

        if (notificarHoje) {
          val mensagem = if (diasRestantes in marcos) {
            "A atividade '${atividade.nome}' está a $diasRestantes dias do prazo final."
          } else {
            "Faltam $diasRestantes dias para o fim da atividade '${atividade.nome}'."
          }

          for (responsavel in responsaveis) {
            val jaExisteHoje = requisicaoDao.existeRequisicaoHojeParaAtividade(
              atividade.id, responsavel.id, TipoRequisicao.ATIVIDADE_PARA_VENCER
            )

            if (!jaExisteHoje) {
              val requisicao = RequisicaoEntity(
                tipo = TipoRequisicao.ATIVIDADE_PARA_VENCER,
                atividadeId = atividade.id,
                solicitanteId = responsavel.id,
                status = StatusRequisicao.ACEITA,
                dataSolicitacao = Date(),
                mensagemResposta = mensagem,
                foiVista = false
              )
              val id = requisicaoDao.inserir(requisicao).toInt()
              enviarNotificacaoDePrazo(getApplication(), id, responsavel.nomeCompleto, mensagem)
            }
          }
        }
      }
    }
  }



  fun cancelarNotificacoesDePrazo(context: Context, atividadeId: Int) = viewModelScope.launch {
    val requisicoes = requisicaoDao.getRequisicoesDePrazoPorAtividade(atividadeId)
    val notificationManager = NotificationManagerCompat.from(context)

    for (req in requisicoes) {
      notificationManager.cancel(req.id)
      requisicaoDao.marcarComoResolvida(req.id)   // ✅ agora define resolvida = 1
    }
  }



}
