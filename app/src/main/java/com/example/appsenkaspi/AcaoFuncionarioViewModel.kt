package com.example.appsenkaspi

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class AcaoFuncionarioViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getDatabase(application).acaoFuncionarioDao()

    fun listarFuncionariosPorAcao(acaoId: Int): LiveData<List<Int>> {
        return dao.listarFuncionariosPorAcao(acaoId)
    }

    fun listarAcoesPorFuncionario(funcionarioId: Int): LiveData<List<Int>> {
        return dao.listarAcoesPorFuncionario(funcionarioId)
    }

    fun inserir(acaoFuncionario: AcaoFuncionarioEntity) = viewModelScope.launch {
        dao.inserirAcaoFuncionario(acaoFuncionario)
    }

    fun deletar(acaoFuncionario: AcaoFuncionarioEntity) = viewModelScope.launch {
        dao.deletarAcaoFuncionario(acaoFuncionario)
    }
}
