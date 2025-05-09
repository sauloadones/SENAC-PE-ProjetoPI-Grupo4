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
    import java.util.Date

    class NotificacaoViewModel(application: Application) : AndroidViewModel(application) {

        private val requisicaoDao = AppDatabase.getDatabase(application).requisicaoDao()
        private val atividadeDao = AppDatabase.getDatabase(application).atividadeDao()
        private val acaoDao = AppDatabase.getDatabase(application).acaoDao()

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

// Verifica se acaoId e criadoPor existem no banco
                            val acao = acaoDao.getAcaoById(atividadeJson.acaoId)
                            val criador = AppDatabase.getDatabase(context).funcionarioDao().getFuncionarioById(atividadeJson.criadoPor)
                            val funcionarioId = atividadeJson.responsaveis.firstOrNull()

                            if (acao != null && criador != null && funcionarioId != null) {
                                val novaAtividade = AtividadeEntity(
                                    nome = atividadeJson.nome,
                                    descricao = atividadeJson.descricao,
                                    dataInicio = atividadeJson.dataInicio,
                                    dataPrazo = atividadeJson.dataPrazo,
                                    acaoId = atividadeJson.acaoId,
                                    funcionarioId = funcionarioId, // necessário ao menos um
                                    status = atividadeJson.status,
                                    prioridade = atividadeJson.prioridade,
                                    criadoPor = atividadeJson.criadoPor,
                                    dataCriacao = atividadeJson.dataCriacao,

                                )
                                val atividadeId = atividadeDao.insertComRetorno(novaAtividade)

                                atividadeJson.responsaveis.forEach { idResp ->
                                    atividadeDao.inserirRelacaoFuncionario(
                                        AtividadeFuncionarioEntity(atividadeId = atividadeId.toInt(), funcionarioId = idResp)
                                    )
                                }
                            } else {
                                Log.e("Requisicao", "Erro ao aceitar atividade: dados ausentes ou inválidos.")
                            }

                        }
                        TipoRequisicao.EDITAR_ATIVIDADE -> {
                            val atividadeJson = Gson().fromJson(requisicao.atividadeJson, AtividadeJson::class.java)

// Verifica se acaoId e criadoPor existem no banco
                            val acao = acaoDao.getAcaoById(atividadeJson.acaoId)
                            val criador = AppDatabase.getDatabase(context).funcionarioDao().getFuncionarioById(atividadeJson.criadoPor)
                            val funcionarioId = atividadeJson.responsaveis.firstOrNull()

                            val idOriginal = atividadeJson.id
                            if (idOriginal == null) {
                                Log.e("Requisicao", "Erro: atividade sem ID para completar")
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
                                    funcionarioId = funcionarioId, // necessário ao menos um
                                    status = atividadeJson.status,
                                    prioridade = atividadeJson.prioridade,
                                    criadoPor = atividadeJson.criadoPor,
                                    dataCriacao = atividadeJson.dataCriacao,

                                    )
                                val atividadeId = atividadeDao.insertComRetorno(novaAtividade)

                                atividadeJson.responsaveis.forEach { idResp ->
                                    atividadeDao.inserirRelacaoFuncionario(
                                        AtividadeFuncionarioEntity(atividadeId = atividadeId.toInt(), funcionarioId = idResp)
                                    )
                                }
                            } else {
                                Log.e("Requisicao", "Erro ao aceitar atividade: dados ausentes ou inválidos.")
                            }

                        }
                        TipoRequisicao.COMPLETAR_ATIVIDADE -> {


                            val atividadeJson = Gson().fromJson(requisicao.atividadeJson, AtividadeJson::class.java)
                            val idOriginal = atividadeJson.id
                            if (idOriginal == null) {
                                Log.e("Requisicao", "Erro: atividade sem ID para completar")
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

                        }

                        TipoRequisicao.CRIAR_ACAO -> {
                            val acao = Gson().fromJson(requisicao.acaoJson, AcaoEntity::class.java)
                            try {
                                acaoDao.insert(acao)
                                Log.d("Requisicao", "Ação criada com sucesso: ${acao.nome}")
                            } catch (e: Exception) {
                                Log.e("Requisicao", "Erro ao criar ação: ${e.message}", e)
                            }
                        }

                        TipoRequisicao.EDITAR_ACAO -> {
                            val acao = Gson().fromJson(requisicao.acaoJson, AcaoEntity::class.java)

                            val acaoExistente = acaoDao.getAcaoById(acao.id)
                            if (acaoExistente != null) {
                                acaoDao.update(acao)
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


      private fun getFuncionarioLogadoId(context: Context): Int {
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


    }

