package com.example.appsenkaspi

import android.util.Log
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AtividadeRepository(
  private val atividadeDao: AtividadeDao,
  private val atividadeFuncionarioDao: AtividadeFuncionarioDao,
  private val requisicaoDao: RequisicaoDao
) {

  suspend fun verificarNotificacoesDePrazo() {
    val hoje = Calendar.getInstance().apply {
      set(Calendar.HOUR_OF_DAY, 0)
      set(Calendar.MINUTE, 0)
      set(Calendar.SECOND, 0)
      set(Calendar.MILLISECOND, 0)
    }.time

    val atividades = atividadeDao.getTodasAtividadesComDataPrazo()

    for (atividade in atividades) {
      val id = atividade.id ?: continue
      val prazo = atividade.dataPrazo ?: continue
      if (atividade.status == StatusAtividade.CONCLUIDA) continue

      val dataTruncada = Calendar.getInstance().apply {
        time = prazo
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
      }.time
      val diasRestantes = ((dataTruncada.time - hoje.time) / (1000 * 60 * 60 * 24)).toInt()
      if (diasRestantes !in 1..6 && diasRestantes !in listOf(30, 15, 7)) continue

      val mensagem = if (diasRestantes in listOf(30, 15, 7))
        "A atividade '${atividade.nome}' está a $diasRestantes dias do prazo final."
      else
        "Faltam $diasRestantes dias para o fim da atividade '${atividade.nome}'."


      val responsaveis = atividadeFuncionarioDao.getResponsaveisDaAtividade(id)

      for (responsavel in responsaveis) {
        val jaExiste = requisicaoDao.existeMensagemExata(
          atividadeId = id,
          responsavelId = responsavel.id,
          tipo = TipoRequisicao.ATIVIDADE_PARA_VENCER,
          mensagem = mensagem
        )

        if (!jaExiste) {
          requisicaoDao.inserir(
            RequisicaoEntity(
              tipo = TipoRequisicao.ATIVIDADE_PARA_VENCER,
              atividadeId = id,
              solicitanteId = responsavel.id,
              status = StatusRequisicao.ACEITA,
              dataSolicitacao = Date(),
              mensagemResposta = mensagem,
              foiVista = false
            )
          )
        }
      }
    }
  }


  suspend fun tratarConclusaoAtividade(atividade: AtividadeEntity) {
    val id = atividade.id ?: return

    // Atualiza a atividade antes de notificar
    atividadeDao.update(atividade)

    val responsaveis = atividadeFuncionarioDao.getResponsaveisDaAtividade(id)

    for (responsavel in responsaveis) {
      requisicaoDao.inserir(
        RequisicaoEntity(
          tipo = TipoRequisicao.ATIVIDADE_CONCLUIDA,
          atividadeId = id,
          solicitanteId = responsavel.id,
          status = StatusRequisicao.ACEITA,
          dataSolicitacao = Date(),
          mensagemResposta = "A atividade '${atividade.nome}' foi concluída com sucesso no dia '${Date()}'.",
          foiVista = false
        )
      )
    }
  }


  suspend fun verificarAtividadesVencidas() {
    val hoje = Date()
    val atividades = atividadeDao.getTodasAtividadesComDataPrazo()

    for (atividade in atividades) {
      val id = atividade.id ?: continue
      val dataPrazo = atividade.dataPrazo ?: continue
      if (atividade.status == StatusAtividade.CONCLUIDA || atividade.status == StatusAtividade.VENCIDA) continue
      if (!dataPrazo.before(hoje)) continue

      // 🔁 Atualiza o status no banco
      atividadeDao.update(atividade.copy(status = StatusAtividade.VENCIDA))

      // 🔔 Notifica os responsáveis
      val responsaveis = atividadeFuncionarioDao.getResponsaveisDaAtividade(id)
      requisicaoDao.deletarRequisicoesDeTipoPorAtividade(id, TipoRequisicao.ATIVIDADE_VENCIDA)

      for (responsavel in responsaveis) {
        requisicaoDao.inserir(
          RequisicaoEntity(
            tipo = TipoRequisicao.ATIVIDADE_VENCIDA,
            atividadeId = id,
            solicitanteId = responsavel.id,
            status = StatusRequisicao.ACEITA,
            dataSolicitacao = Date(),
            mensagemResposta = "A atividade '${atividade.nome}' venceu e não pode mais ser concluída.",
            foiVista = false
          )
        )
      }
    }
  }



  suspend fun tratarAlteracaoPrazo(atividadeNova: AtividadeEntity, atividadeAntiga: AtividadeEntity) {
    Log.d("DEBUG_PRAZO", "Função tratarAlteracaoPrazo chamada para atividade ${atividadeNova.id}")

    val id = atividadeNova.id ?: return
    val novaData = atividadeNova.dataPrazo ?: return
    val antigaData = atividadeAntiga.dataPrazo ?: return
    if (atividadeNova.status == StatusAtividade.CONCLUIDA) {
      Log.d("DEBUG_PRAZO", "Atividade ${atividadeNova.id} já concluída — ignorando notificação de alteração de prazo.")
      return
    }
    val truncarData: (Date) -> Date = {
      Calendar.getInstance().apply {
        time = it
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
      }.time
    }

    val novaDataTruncada = truncarData(novaData)
    val antigaDataTruncada = truncarData(antigaData)

    val hoje = truncarData(Date())
    if (atividadeNova.status == StatusAtividade.VENCIDA && novaDataTruncada.after(hoje)) {
      val revertida = atividadeNova.copy(status = StatusAtividade.PENDENTE)
      atividadeDao.update(revertida)
      Log.d("DEBUG_PRAZO", "Status da atividade ${atividadeNova.id} revertido para PENDENTE.")
    }

    if (novaDataTruncada != antigaDataTruncada) {
      val responsaveis = atividadeFuncionarioDao.getResponsaveisDaAtividade(id)

      // 🔹 Notificação 1: alerta direto de que o prazo foi alterado
      val dataFormatada = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR")).format(novaDataTruncada)
      val mensagemAlteracao = "O prazo da atividade '${atividadeNova.nome}' foi alterado para $dataFormatada."

      for (responsavel in responsaveis) {
        val jaExisteAlteracao = requisicaoDao.existeMensagemExata(
          atividadeId = id,
          responsavelId = responsavel.id,
          tipo = TipoRequisicao.PRAZO_ALTERADO,
          mensagem = mensagemAlteracao
        )

        if (!jaExisteAlteracao) {
          requisicaoDao.inserir(
            RequisicaoEntity(
              tipo = TipoRequisicao.PRAZO_ALTERADO,
              atividadeId = id,
              solicitanteId = responsavel.id,
              status = StatusRequisicao.ACEITA,
              dataSolicitacao = Date(),
              mensagemResposta = mensagemAlteracao,
              foiVista = false
            )
          )
        }
      }

      // 🔹 Notificação 2: alerta de prazo restante (somente se dentro da janela crítica)
      val hoje = truncarData(Date())
      val diasRestantes = ((novaDataTruncada.time - hoje.time) / (1000 * 60 * 60 * 24)).toInt()
      val diasPermitidos = listOf(30, 15, 7) + (6 downTo 1)

      requisicaoDao.deletarRequisicoesDeTipoPorAtividade(id, TipoRequisicao.ATIVIDADE_PARA_VENCER)

      if (diasRestantes in diasPermitidos) {
        val mensagem = if (diasRestantes in listOf(30, 15, 7))
          "A atividade '${atividadeNova.nome}' está a $diasRestantes dias do prazo final."
        else
          "Faltam $diasRestantes dias para o fim da atividade '${atividadeNova.nome}'."

        for (responsavel in responsaveis) {
          requisicaoDao.inserir(
            RequisicaoEntity(
              tipo = TipoRequisicao.ATIVIDADE_PARA_VENCER,
              atividadeId = id,
              solicitanteId = responsavel.id,
              status = StatusRequisicao.ACEITA,
              dataSolicitacao = Date(),
              mensagemResposta = mensagem,
              foiVista = false
            )
          )
        }
      }
    }
  }




  fun formatarData(data: Date): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
    return sdf.format(data)
  }


}
