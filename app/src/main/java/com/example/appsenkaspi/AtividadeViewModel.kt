package com.example.appsenkaspi

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class AtividadeViewModel(application: Application) : AndroidViewModel(application) {



    private val atividadeDao = AppDatabase.getDatabase(application).atividadeDao()

    fun listarAtividadesPorAcao(acaoId: Int): LiveData<List<AtividadeEntity>> {
        return atividadeDao.listarAtividadesPorAcao(acaoId)
    }

    fun inserir(atividade: AtividadeEntity) = viewModelScope.launch {
        atividadeDao.inserirAtividade(atividade)
    }

    fun atualizar(atividade: AtividadeEntity) = viewModelScope.launch {
        atividadeDao.atualizarAtividade(atividade)
    }

    fun deletar(atividade: AtividadeEntity) = viewModelScope.launch {
        atividadeDao.deletarAtividade(atividade)
    }

}
