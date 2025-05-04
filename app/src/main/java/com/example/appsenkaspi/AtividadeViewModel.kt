package com.example.appsenkaspi

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class AtividadeViewModel(application: Application) : AndroidViewModel(application) {

    private val atividadeDao = AppDatabase.getDatabase(application).atividadeDao()
    private val atividadeFuncionarioDao = AppDatabase.getDatabase(application).atividadeFuncionarioDao()

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
}
