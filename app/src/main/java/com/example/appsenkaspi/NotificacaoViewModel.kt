package com.example.appsenkaspi

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import androidx.lifecycle.distinctUntilChanged


class NotificacaoViewModel(application: Application) : AndroidViewModel(application) {

  private val db = AppDatabase.getDatabase(application)
  private val requisicaoDao = db.requisicaoDao()
  private val atividadeDao = db.atividadeDao()
  private val acaoDao = db.acaoDao()
  private val acaoFuncionarioDao = db.acaoFuncionarioDao()
  private val atividadeFuncionarioDao = db.atividadeFuncionarioDao()

  fun getRequisicoesPendentes(): LiveData<List<RequisicaoEntity>> = requisicaoDao.getRequisicoesPendentes()

  fun getRequisicaoPorId(id: Int): LiveData<RequisicaoEntity> {
    val result = MutableLiveData<RequisicaoEntity>()
    viewModelScope.launch {
      result.postValue(requisicaoDao.getRequisicaoById(id))
    }
    return result
  }

  fun responderRequisicao(context: Context, requisicao: RequisicaoEntity, aceitar: Boolean) {
    viewModelScope.launch {
      val status = if (aceitar) StatusRequisicao.ACEITA else StatusRequisicao.RECUSADA
      val novaRequisicao = requisicao.copy(
        status = status,
        dataResposta = Date(),
        coordenadorId = getFuncionarioLogadoId(context),
        mensagemResposta = if (aceitar) "Requisição aceita." else "Requisição recusada."
      )
      requisicaoDao.update(novaRequisicao)
      val tipoDescricao = when (requisicao.tipo) {
        TipoRequisicao.CRIAR_ATIVIDADE -> "criação de atividade"
        TipoRequisicao.EDITAR_ATIVIDADE -> "edição de atividade"
        TipoRequisicao.COMPLETAR_ATIVIDADE -> "conclusão de atividade"
        TipoRequisicao.CRIAR_ACAO -> "criação de ação"
        TipoRequisicao.EDITAR_ACAO -> "edição de ação"
        else -> "requisição"
      }

      NotificationUtils.mostrarNotificacao(
        context,
        "Requisição ${if (aceitar) "aceita" else "recusada"}",
        "Sua solicitação de $tipoDescricao foi ${if (aceitar) "aceita" else "recusada"}.",
        requisicao.id * 100
      )



      if (!aceitar) return@launch

      when (requisicao.tipo) {
        TipoRequisicao.CRIAR_ATIVIDADE -> salvarAtividade(context, requisicao.atividadeJson!!, isEdicao = false)
        TipoRequisicao.EDITAR_ATIVIDADE -> salvarAtividade(context, requisicao.atividadeJson!!, isEdicao = true)
        TipoRequisicao.COMPLETAR_ATIVIDADE -> completarAtividade(context, requisicao)
        TipoRequisicao.CRIAR_ACAO -> salvarAcao(requisicao.acaoJson!!, isEdicao = false)
        TipoRequisicao.EDITAR_ACAO -> salvarAcao(requisicao.acaoJson!!, isEdicao = true)
        else -> {}
      }
    }
  }

  private suspend fun salvarAtividade(context: Context, json: String, isEdicao: Boolean) {
    val atividadeJson = Gson().fromJson(json, AtividadeJson::class.java)
    val responsaveis = atividadeJson.responsaveis ?: emptyList()
    val funcionarioId = responsaveis.firstOrNull()

    if (funcionarioId == null) {
      Log.e("Requisicao", "Erro: nenhum responsável encontrado para a atividade")
      return
    }

    val novaAtividade = AtividadeEntity(
      id = if (isEdicao) atividadeJson.id
        ?: throw IllegalStateException("ID da atividade ausente para edição") else null,
      nome = atividadeJson.nome,
      descricao = atividadeJson.descricao,
      dataInicio = atividadeJson.dataInicio,
      dataPrazo = atividadeJson.dataPrazo,
      acaoId = atividadeJson.acaoId,
      funcionarioId = funcionarioId,
      status = if (isEdicao) atividadeJson.status else StatusAtividade.PENDENTE,
      prioridade = atividadeJson.prioridade,
      criadoPor = atividadeJson.criadoPor,
      dataCriacao = atividadeJson.dataCriacao
    )

    if (!isEdicao) {
      novaAtividade.id = null // Garante que Room vai gerar o ID
      val idAtividade = atividadeDao.insertComRetorno(novaAtividade).toInt()
      novaAtividade.id = idAtividade
    } else {
      val id = atividadeJson.id!!
      val antigaAtividade = atividadeDao.getAtividadePorIdDireto(id)
      val antigosResponsaveis = atividadeFuncionarioDao.getResponsaveisDaAtividade(id)
      val antigosIds = antigosResponsaveis.map { it.id }
      val novosIds = responsaveis

      val adicionados = novosIds - antigosIds
      val removidos = antigosIds - novosIds

      atividadeDao.update(novaAtividade)
      atividadeDao.deletarRelacoesPorAtividade(id)

// ⏩ Primeiro insere os responsáveis atualizados
      responsaveis.forEach { idResp ->
        atividadeDao.inserirRelacaoFuncionario(
          AtividadeFuncionarioEntity(
            atividadeId = id,
            funcionarioId = idResp
          )
        )
      }

// ✅ Agora sim: chama método de alteração de prazo
      val atualizada = atividadeDao.getAtividadePorIdDireto(id)
      val atividadeRepository = AtividadeRepository(
        context,
        atividadeDao,
        db.atividadeFuncionarioDao(),
        requisicaoDao
      )
      atividadeRepository.tratarAlteracaoPrazo(atualizada, antigaAtividade)

// 🆕 Notifica adição/remoção de responsáveis
      val funcionarioDao = db.funcionarioDao()
      val adicionadosEntities = funcionarioDao.getByIds(adicionados)
      val removidosEntities = funcionarioDao.getByIds(removidos)


      atividadeRepository.notificarMudancaResponsaveis(atualizada, adicionadosEntities, removidosEntities)

    }
  }



  private suspend fun completarAtividade(context: Context, requisicao: RequisicaoEntity) {
    val hoje = Calendar.getInstance().apply {
      set(Calendar.HOUR_OF_DAY, 0)
      set(Calendar.MINUTE, 0)
      set(Calendar.SECOND, 0)
      set(Calendar.MILLISECOND, 0)
    }.time

    val atividadeJson = Gson().fromJson(requisicao.atividadeJson, AtividadeJson::class.java)
    val responsaveis = atividadeJson.responsaveis ?: emptyList()
    val funcionarioId = responsaveis.firstOrNull()

    if (atividadeJson.id == null || atividadeJson.dataPrazo.before(hoje) || funcionarioId == null) {
      requisicaoDao.update(
        requisicao.copy(
          status = StatusRequisicao.RECUSADA,
          dataResposta = Date(),
          mensagemResposta = "Atividade vencida ou sem responsável. Ajuste antes de concluir.",
          coordenadorId = getFuncionarioLogadoId(context)
        )
      )
      return
    }

    val concluida = AtividadeEntity(
      id = atividadeJson.id,
      nome = atividadeJson.nome,
      descricao = atividadeJson.descricao,
      dataInicio = atividadeJson.dataInicio,
      dataPrazo = atividadeJson.dataPrazo,
      acaoId = atividadeJson.acaoId,
      funcionarioId = funcionarioId,
      status = StatusAtividade.CONCLUIDA,
      prioridade = atividadeJson.prioridade,
      criadoPor = atividadeJson.criadoPor,
      dataCriacao = atividadeJson.dataCriacao
    )
    val atividadeRepository = AtividadeRepository(
      context,
      atividadeDao,
      db.atividadeFuncionarioDao(),
      requisicaoDao
    )
    atividadeRepository.tratarConclusaoAtividade(concluida)


  }

  private suspend fun salvarAcao(json: String, isEdicao: Boolean) {
    val acaoJson = Gson().fromJson(json, AcaoJson::class.java)
    val acao = AcaoEntity(
      id = if (isEdicao) acaoJson.id ?: throw IllegalStateException("ID da atividade ausente para edição") else null,
      nome = acaoJson.nome,
      descricao = acaoJson.descricao,
      dataInicio = acaoJson.dataInicio,
      dataPrazo = acaoJson.dataPrazo,
      status = acaoJson.status,
      criadoPor = acaoJson.criadoPor,
      dataCriacao = acaoJson.dataCriacao,
      pilarId = acaoJson.pilarId
    )

    val idAcao = if (isEdicao) {
      acaoDao.update(acao)
      acaoJson.id!!
    } else {
      acaoDao.inserirComRetorno(acao).toInt()
    }

    acaoFuncionarioDao.deletarResponsaveisPorAcao(idAcao)
    acaoJson.responsaveis.forEach { idResp ->
      acaoFuncionarioDao.inserirAcaoFuncionario(
        AcaoFuncionarioEntity(acaoId = idAcao, funcionarioId = idResp)
      )
    }
  }

  fun getFuncionarioLogadoId(context: Context): Int {
    val prefs = context.getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
    return prefs.getInt("funcionarioId", -1)
  }

  fun inserirRequisicao(requisicao: RequisicaoEntity) {
    viewModelScope.launch {
      requisicaoDao.inserir(requisicao)
    }
  }

  fun getNotificacoesDoApoio(usuarioId: Int): LiveData<List<RequisicaoEntity>> =
    requisicaoDao.getNotificacoesDoApoio(usuarioId)

  fun marcarTodasComoVistas(usuarioId: Int) {
    viewModelScope.launch {
      requisicaoDao.marcarComoVista(usuarioId)
    }
  }

  fun getQuantidadeNaoVistas(usuarioId: Int): LiveData<Int> =
    requisicaoDao.getQuantidadeNaoVistas(usuarioId)

  fun getQuantidadePendentesParaCoordenador(): LiveData<Int> =
    requisicaoDao.getQuantidadePendentesParaCoordenador()

  fun getQuantidadeNotificacoesPrazoNaoVistas(usuarioId: Int): LiveData<Int> =
    requisicaoDao.getQuantidadeNotificacoesPrazoNaoVistas(usuarioId)

  fun marcarNotificacoesDePrazoComoVistas(usuarioId: Int) {
    viewModelScope.launch {
      requisicaoDao.marcarNotificacoesDePrazoComoVistas(usuarioId)
    }
    fun getNotificacoesDoApoio(id: Int): LiveData<List<RequisicaoEntity>> {
      return requisicaoDao.getNotificacoesDoFuncionario(id)
        .distinctUntilChanged()  // <- força atualização mesmo se for a "mesma lista"
    }
  }
}
