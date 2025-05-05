package com.example.appsenkaspi

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class RequisicaoViewModel(application: Application) : AndroidViewModel(application) {

    private val requisicaoDao = AppDatabase.getDatabase(application).requisicaoDao()

    val todasRequisicoes: LiveData<List<RequisicaoEntity>> = requisicaoDao.listarTodas()

    fun listarPorStatus(status: StatusRequisicao): LiveData<List<RequisicaoEntity>> =
        requisicaoDao.listarPorStatus(status)

    fun listarPorTipo(tipo: TipoRequisicao): LiveData<List<RequisicaoEntity>> =
        requisicaoDao.listarPorTipo(tipo)

    fun listarPorSolicitante(funcionarioId: Int): LiveData<List<RequisicaoEntity>> =
        requisicaoDao.listarPorSolicitante(funcionarioId)

    fun inserir(requisicao: RequisicaoEntity) = viewModelScope.launch {
        requisicaoDao.inserir(requisicao)
    }

    fun atualizar(requisicao: RequisicaoEntity) = viewModelScope.launch {
        requisicaoDao.atualizar(requisicao)
    }

    suspend fun buscarPorId(id: Int): RequisicaoEntity? {
        return requisicaoDao.buscarPorId(id)
    }

    fun responderRequisicao(
        id: Int,
        status: StatusRequisicao,
        coordenadorId: Int,
        mensagem: String?
    ) = viewModelScope.launch {
        val requisicao = requisicaoDao.buscarPorId(id)
        requisicao?.let {
            val resposta = it.copy(
                status = status,
                coordenadorId = coordenadorId,
                mensagemRetorno = mensagem,
                dataResposta = java.util.Date()
            )
            requisicaoDao.atualizar(resposta)
        }
    }
}
