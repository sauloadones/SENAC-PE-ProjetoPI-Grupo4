package com.example.appsenkaspi

import android.content.Context

import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

class AtividadeRepository(
  private val context: Context,
  private val atividadeDao: AtividadeDao,
  private val atividadeFuncionarioDao: AtividadeFuncionarioDao,
  private val requisicaoDao: RequisicaoDao
) {

  private fun truncarData(data: Date): Date {
    return Calendar.getInstance().apply {
      time = data
      set(Calendar.HOUR_OF_DAY, 0)
      set(Calendar.MINUTE, 0)
      set(Calendar.SECOND, 0)
      set(Calendar.MILLISECOND, 0)
    }.time
  }

  fun formatarData(data: Date): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
    return sdf.format(data)
  }

  suspend fun verificarNotificacoesDePrazo() {
    val hoje = truncarData(Date())
    val atividades = atividadeDao.getTodasAtividadesComDataPrazo()

    for (atividade in atividades) {
      val id = atividade.id ?: continue
      val prazo = atividade.dataPrazo ?: continue
      if (atividade.status == StatusAtividade.CONCLUIDA) continue

      val prazoTruncado = truncarData(prazo)
      val diasRestantes = ((prazoTruncado.time - hoje.time) / (1000 * 60 * 60 * 24)).toInt()
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
          NotificationUtils.mostrarNotificacao(
            context,
            "Prazo se aproximando",
            mensagem,
            id * 100 + responsavel.id
          )
        }
      }
    }
  }

  suspend fun verificarAtividadesVencidas() {
    val hoje = truncarData(Date())
    val atividades = atividadeDao.getTodasAtividadesComDataPrazo()

    for (atividade in atividades) {
      val id = atividade.id ?: continue
      val prazo = atividade.dataPrazo ?: continue
      if (atividade.status == StatusAtividade.CONCLUIDA || atividade.status == StatusAtividade.VENCIDA) continue
      if (!prazo.before(hoje)) continue

      atividadeDao.update(atividade.copy(status = StatusAtividade.VENCIDA))

      val responsaveis = atividadeFuncionarioDao.getResponsaveisDaAtividade(id)
      requisicaoDao.deletarRequisicoesDeTipoPorAtividade(id, TipoRequisicao.ATIVIDADE_VENCIDA)

      for (responsavel in responsaveis) {
        val mensagem = "A atividade '${atividade.nome}' venceu e não pode mais ser concluída."
        requisicaoDao.inserir(
          RequisicaoEntity(
            tipo = TipoRequisicao.ATIVIDADE_VENCIDA,
            atividadeId = id,
            solicitanteId = responsavel.id,
            status = StatusRequisicao.ACEITA,
            dataSolicitacao = Date(),
            mensagemResposta = mensagem,
            foiVista = false
          )
        )
        NotificationUtils.mostrarNotificacao(
          context,
          "Atividade vencida",
          mensagem,
          id * 100 + responsavel.id
        )
      }
    }
  }

  suspend fun tratarConclusaoAtividade(atividade: AtividadeEntity) {
    val id = atividade.id ?: return
    atividadeDao.update(atividade)

    val responsaveis = atividadeFuncionarioDao.getResponsaveisDaAtividade(id)
    val dataConclusao = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("pt", "BR")).format(Date())

    for (responsavel in responsaveis) {
      val mensagem = "A atividade '${atividade.nome}' foi concluída com sucesso em $dataConclusao."
      requisicaoDao.inserir(
        RequisicaoEntity(
          tipo = TipoRequisicao.ATIVIDADE_CONCLUIDA,
          atividadeId = id,
          solicitanteId = responsavel.id,
          status = StatusRequisicao.ACEITA,
          dataSolicitacao = Date(),
          mensagemResposta = mensagem,
          foiVista = false
        )
      )
      NotificationUtils.mostrarNotificacao(
        context,
        "Atividade concluída",
        mensagem,
        id * 100 + responsavel.id
      )
    }
  }

  suspend fun tratarAlteracaoPrazo(atividadeNova: AtividadeEntity, atividadeAntiga: AtividadeEntity) {
    val id = atividadeNova.id ?: return
    val novaData = atividadeNova.dataPrazo ?: return
    val antigaData = atividadeAntiga.dataPrazo ?: return
    val novaTruncada = truncarData(novaData)
    val antigaTruncada = truncarData(antigaData)
    val hoje = truncarData(Date())

    if (atividadeNova.status == StatusAtividade.CONCLUIDA) return

    if (atividadeNova.status == StatusAtividade.VENCIDA && novaTruncada.after(hoje)) {
      atividadeDao.update(atividadeNova.copy(status = StatusAtividade.PENDENTE))
    }

    if (novaTruncada != antigaTruncada) {
      val dataFormatada = formatarData(novaTruncada)
      val mensagemAlteracao = "O prazo da atividade '${atividadeNova.nome}' foi alterado para $dataFormatada."

      val responsaveis = atividadeFuncionarioDao.getResponsaveisDaAtividade(id)
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
          NotificationUtils.mostrarNotificacao(
            context,
            "Prazo alterado",
            mensagemAlteracao,
            id * 100 + responsavel.id
          )
        }
      }

      val diasRestantes = ((novaTruncada.time - hoje.time) / (1000 * 60 * 60 * 24)).toInt()
      val diasPermitidos = listOf(30, 15, 7) + (6 downTo 1)

      requisicaoDao.deletarRequisicoesDeTipoPorAtividade(id, TipoRequisicao.ATIVIDADE_PARA_VENCER)

      if (diasRestantes in diasPermitidos) {
        val mensagem = if (diasRestantes in listOf(30, 15, 7))
          "A atividade '${atividadeNova.nome}' está a $diasRestantes dias do prazo final."
        else
          "Faltam $diasRestantes dias para o fim da atividade '${atividadeNova.nome}'."

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
            NotificationUtils.mostrarNotificacao(
              context,
              "Prazo se aproximando",
              mensagem,
              id * 100 + responsavel.id
            )
          }
        }
      }
    }
  }

  suspend fun notificarMudancaResponsaveis(
    atividade: AtividadeEntity,
    adicionados: List<FuncionarioEntity>,
    removidos: List<FuncionarioEntity>
  ) {
    val data = Date()
    val nomeAtividade = atividade.nome

    for (novo in adicionados) {
      val mensagem = "Você foi atribuído como responsável pela atividade '$nomeAtividade'."
      val jaExiste = requisicaoDao.existeMensagemExata(
        atividadeId = atividade.id ?: continue,
        responsavelId = novo.id,
        tipo = TipoRequisicao.RESPONSAVEL_ADICIONADO,
        mensagem = mensagem
      )
      if (!jaExiste) {
        requisicaoDao.inserir(
          RequisicaoEntity(
            tipo = TipoRequisicao.RESPONSAVEL_ADICIONADO,
            atividadeId = atividade.id,
            solicitanteId = novo.id,
            status = StatusRequisicao.ACEITA,
            dataSolicitacao = data,
            mensagemResposta = mensagem,
            foiVista = false
          )
        )
        NotificationUtils.mostrarNotificacao(
          context,
          "Você foi atribuído",
          mensagem,
          (atividade.id ?: 0) * 10 + novo.id
        )
      }
    }

    for (removido in removidos) {
      val mensagem = "Você foi removido da responsabilidade pela atividade '$nomeAtividade'."
      val jaExiste = requisicaoDao.existeMensagemExata(
        atividadeId = atividade.id ?: continue,
        responsavelId = removido.id,
        tipo = TipoRequisicao.RESPONSAVEL_REMOVIDO,
        mensagem = mensagem
      )
      if (!jaExiste) {
        requisicaoDao.inserir(
          RequisicaoEntity(
            tipo = TipoRequisicao.RESPONSAVEL_REMOVIDO,
            atividadeId = atividade.id,
            solicitanteId = removido.id,
            status = StatusRequisicao.ACEITA,
            dataSolicitacao = data,
            mensagemResposta = mensagem,
            foiVista = false
          )
        )
        NotificationUtils.mostrarNotificacao(
          context,
          "Responsabilidade removida",
          mensagem,
          (atividade.id ?: 0) * 10 + removido.id
        )
      }
    }
  }
}
