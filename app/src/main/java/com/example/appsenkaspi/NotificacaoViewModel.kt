package com.example.appsenkaspi

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import kotlin.getValue

class NotificacaoViewModel(application: Application) : AndroidViewModel(application) {

  private val requisicaoDao = AppDatabase.getDatabase(application).requisicaoDao()
  private val atividadeDao = AppDatabase.getDatabase(application).atividadeDao()
  private val acaoDao = AppDatabase.getDatabase(application).acaoDao()
  private val acaoFuncionarioDao = AppDatabase.getDatabase(application).acaoFuncionarioDao()

  fun getRequisicoesPendentes(): LiveData<List<RequisicaoEntity>> {
    return requisicaoDao.getRequisicoesPendentes()
  }

  fun getRequisicoesDoUsuario(userId: Int): LiveData<List<RequisicaoEntity>> {
    return requisicaoDao.getRequisicoesDoUsuario(userId)
  }

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

      if (aceitar) {
        when (requisicao.tipo) {
          TipoRequisicao.CRIAR_ATIVIDADE -> {
            val atividadeJson = Gson().fromJson(requisicao.atividadeJson, AtividadeJson::class.java)
            val acao = acaoDao.getAcaoById(atividadeJson.acaoId)
            val criador = AppDatabase.getDatabase(context).funcionarioDao()
              .getFuncionarioById(atividadeJson.criadoPor)
            val funcionarioId = atividadeJson.responsaveis.firstOrNull()

            if (acao != null && criador != null && funcionarioId != null) {
              val novaAtividade = AtividadeEntity(
                nome = atividadeJson.nome,
                descricao = atividadeJson.descricao,
                dataInicio = atividadeJson.dataInicio,
                dataPrazo = atividadeJson.dataPrazo,
                acaoId = atividadeJson.acaoId,
                funcionarioId = funcionarioId,
                status = atividadeJson.status,
                prioridade = atividadeJson.prioridade,
                criadoPor = atividadeJson.criadoPor,
                dataCriacao = atividadeJson.dataCriacao
              )
              val atividadeId = atividadeDao.insertComRetorno(novaAtividade).toInt()

              // ✅ Primeiro insere os responsáveis
              atividadeJson.responsaveis.forEach { idResp ->
                atividadeDao.inserirRelacaoFuncionario(
                  AtividadeFuncionarioEntity(
                    atividadeId = atividadeId,
                    funcionarioId = idResp
                  )
                )
              }

              // ✅ Depois gera as notificações (agora os responsáveis já estão no banco)
              val atividadeViewModel = AtividadeViewModel(getApplication())
              atividadeViewModel.verificarAtividadesComPrazoProximo()

            } else {
              Log.e("Requisicao", "Erro ao aceitar atividade: dados ausentes ou inválidos.")
            }
          }


          TipoRequisicao.EDITAR_ATIVIDADE -> {
            val atividadeJson = Gson().fromJson(requisicao.atividadeJson, AtividadeJson::class.java)
            val acao = acaoDao.getAcaoById(atividadeJson.acaoId)
            val criador = AppDatabase.getDatabase(context).funcionarioDao()
              .getFuncionarioById(atividadeJson.criadoPor)
            val funcionarioId = atividadeJson.responsaveis.firstOrNull()
            val idOriginal = atividadeJson.id

            if (idOriginal == null) {
              Log.e("Requisicao", "Erro: atividade sem ID para editar")
              return@launch
            }

            if (acao != null && criador != null && funcionarioId != null) {
              val novaAtividade = AtividadeEntity(
                id = idOriginal,
                nome = atividadeJson.nome,
                descricao = atividadeJson.descricao,
                dataInicio = atividadeJson.dataInicio,
                dataPrazo = atividadeJson.dataPrazo,
                acaoId = atividadeJson.acaoId,
                funcionarioId = funcionarioId,
                status = atividadeJson.status,
                prioridade = atividadeJson.prioridade,
                criadoPor = atividadeJson.criadoPor,
                dataCriacao = atividadeJson.dataCriacao
              )

              atividadeDao.update(novaAtividade)

              atividadeDao.deletarRelacoesPorAtividade(idOriginal)
              atividadeJson.responsaveis.forEach { idResp ->
                atividadeDao.inserirRelacaoFuncionario(
                  AtividadeFuncionarioEntity(
                    atividadeId = idOriginal,
                    funcionarioId = idResp
                  )
                )
              }

              // ✅ Cancelar notificações antigas
              val atividadeViewModel = AtividadeViewModel(getApplication())
              atividadeViewModel.cancelarNotificacoesDePrazo(context, idOriginal)

              // ✅ Gerar novas notificações conforme novo prazo
              atividadeViewModel.verificarAtividadesComPrazoProximo()
            } else {
              Log.e("Requisicao", "Erro ao aceitar edição: dados inválidos")
            }
          }

          TipoRequisicao.COMPLETAR_ATIVIDADE -> {
            val hoje = Calendar.getInstance().apply {
              set(Calendar.HOUR_OF_DAY, 0)
              set(Calendar.MINUTE, 0)
              set(Calendar.SECOND, 0)
              set(Calendar.MILLISECOND, 0)
            }.time


            val atividadeJson = Gson().fromJson(requisicao.atividadeJson, AtividadeJson::class.java)
            val idOriginal = atividadeJson.id
            if (idOriginal == null) {
              Log.e("Requisicao", "Erro: atividade sem ID para completar")
              return@launch
            }
            if (atividadeJson.dataPrazo.before(hoje)) {
              val requisicaoNegada = requisicao.copy(
                status = StatusRequisicao.RECUSADA,
                dataResposta = Date(),
                mensagemResposta = "Atividade está vencida. Ajuste o prazo antes de marcar como concluída.",
                coordenadorId = getFuncionarioLogadoId(context)
              )
              requisicaoDao.update(requisicaoNegada)
              return@launch
            }
            val atividadeConcluida = AtividadeEntity(
              id = idOriginal,
              nome = atividadeJson.nome,
              descricao = atividadeJson.descricao,
              dataInicio = atividadeJson.dataInicio,
              dataPrazo = atividadeJson.dataPrazo,
              acaoId = atividadeJson.acaoId,
              funcionarioId = atividadeJson.responsaveis.firstOrNull() ?: return@launch,
              status = StatusAtividade.CONCLUIDA,
              prioridade = atividadeJson.prioridade,
              criadoPor = atividadeJson.criadoPor,
              dataCriacao = atividadeJson.dataCriacao
            )
            atividadeDao.update(atividadeConcluida)
            val atividadeViewModel = AtividadeViewModel(getApplication())
            atividadeViewModel.verificarAtividadesComPrazoProximo()
          }

          TipoRequisicao.CRIAR_ACAO -> {
            val acaoJson = Gson().fromJson(requisicao.acaoJson, AcaoJson::class.java)
            val novaAcao = AcaoEntity(
              nome = acaoJson.nome,
              descricao = acaoJson.descricao,
              dataInicio = acaoJson.dataInicio,
              dataPrazo = acaoJson.dataPrazo,
              status = acaoJson.status,
              criadoPor = acaoJson.criadoPor,
              dataCriacao = acaoJson.dataCriacao,
              pilarId = acaoJson.pilarId
            )


            val idAcao = acaoDao.inserirComRetorno(novaAcao)


            acaoJson.responsaveis.forEach { idResp ->
              acaoFuncionarioDao.inserirAcaoFuncionario(
                AcaoFuncionarioEntity(acaoId = idAcao.toInt(), funcionarioId = idResp)
              )
            }

            Log.d("Requisicao", "Ação criada com sucesso: ${novaAcao.nome}")
          }


          TipoRequisicao.EDITAR_ACAO -> {
            val acaoJson = Gson().fromJson(requisicao.acaoJson, AcaoJson::class.java)
            val acaoExistente = acaoDao.getAcaoById(acaoJson.id!!)

            if (acaoExistente != null) {
              val novaAcao = AcaoEntity(
                id = acaoJson.id!!,
                nome = acaoJson.nome,
                descricao = acaoJson.descricao,
                dataInicio = acaoJson.dataInicio,
                dataPrazo = acaoJson.dataPrazo,
                status = acaoJson.status,
                criadoPor = acaoJson.criadoPor,
                dataCriacao = acaoJson.dataCriacao,
                pilarId = acaoJson.pilarId
              )

              acaoDao.update(novaAcao)

              // Atualizar responsáveis
              acaoFuncionarioDao.deletarResponsaveisPorAcao(novaAcao.id)
              acaoJson.responsaveis.forEach { idResp ->
                acaoFuncionarioDao.inserirAcaoFuncionario(
                  AcaoFuncionarioEntity(acaoId = novaAcao.id, funcionarioId = idResp)
                )
              }

              // ❗ Aqui não há notificações automáticas associadas a ações,
              // mas se quiser implementar lógica de vencimento para ações, adicione aqui futuramente.

              Log.d("Requisicao", "Ação editada com sucesso: ${novaAcao.nome}")
            } else {
              Log.e("Requisicao", "Erro: ação não encontrada para edição")
            }
          }



          else -> {}
        }
      }
    }
  }

    fun getQuantidadeNaoVistas(usuarioId: Int): LiveData<Int> {
      return requisicaoDao.getQuantidadeNaoVistas(usuarioId)
    }

    fun getFuncionarioLogadoId(context: Context): Int {
      val prefs = context.getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
      return prefs.getInt("funcionarioId", -1)
    }

    fun inserirRequisicao(requisicao: RequisicaoEntity) {
      viewModelScope.launch {
        AppDatabase.getDatabase(getApplication()).requisicaoDao().inserir(requisicao)
      }
    }

    fun getNotificacoesDoApoio(usuarioId: Int): LiveData<List<RequisicaoEntity>> {
      return requisicaoDao.getNotificacoesDoApoio(usuarioId)
    }

    fun marcarTodasComoVistas(usuarioId: Int) {
      viewModelScope.launch {
        requisicaoDao.marcarComoVista(usuarioId)
      }
    }

    fun getQuantidadePendentesParaCoordenador(): LiveData<Int> {
      return requisicaoDao.getQuantidadePendentesParaCoordenador()
    }

    fun getQuantidadeNotificacoesPrazoNaoVistas(usuarioId: Int): LiveData<Int> {
      return requisicaoDao.getQuantidadeNotificacoesPrazoNaoVistas(usuarioId)
    }

    fun marcarNotificacoesDePrazoComoVistas(usuarioId: Int) {
      viewModelScope.launch {
        requisicaoDao.marcarNotificacoesDePrazoComoVistas(usuarioId)
      }
    }
  }

