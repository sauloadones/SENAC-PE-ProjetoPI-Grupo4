package com.example.appsenkaspi

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class AtividadeFuncionarioViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getDatabase(application).atividadeFuncionarioDao()

    fun listarFuncionariosPorAtividade(atividadeId: Int): LiveData<List<Int>> {
        return dao.listarFuncionariosPorAtividade(atividadeId)
    }

    fun listarAtividadesPorFuncionario(funcionarioId: Int): LiveData<List<Int>> {
        return dao.listarAtividadesPorFuncionario(funcionarioId)
    }

    fun inserir(atividadeFuncionario: AtividadeFuncionarioEntity) = viewModelScope.launch {
        dao.inserirAtividadeFuncionario(atividadeFuncionario)
    }

    fun deletar(atividadeFuncionario: AtividadeFuncionarioEntity) = viewModelScope.launch {
        dao.deletarAtividadeFuncionario(atividadeFuncionario)
    }
}
