package com.example.appsenkaspi

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.appsenkaspi.Converters.Cargo
import kotlinx.coroutines.launch


class FuncionarioViewModel(application: Application) : AndroidViewModel(application) {
    // Em qualquer lugar do seu código (por ex no ViewModel ou num objeto utilitário)

    private val funcionarioDao = AppDatabase.getDatabase(application).funcionarioDao()
    val listaFuncionarios: LiveData<List<FuncionarioEntity>> =
        funcionarioDao.listarTodosFuncionarios()



    private val _funcionarioLogado = MutableLiveData<FuncionarioEntity?>()
    val funcionarioLogado: LiveData<FuncionarioEntity?> get() = _funcionarioLogado

    fun logarFuncionario(funcionario: FuncionarioEntity) {
        _funcionarioLogado.value = funcionario
    }

    fun deslogarFuncionario() {
        _funcionarioLogado.value = null
    }





    val listasFuncionarios = funcionarioDao.listarTodosFuncionarios()
    fun inserir(funcionario: FuncionarioEntity) = viewModelScope.launch {
        funcionarioDao.inserirFuncionario(funcionario)
    }

    fun atualizar(funcionario: FuncionarioEntity) = viewModelScope.launch {
        funcionarioDao.atualizarFuncionario(funcionario)
    }

    fun deletar(funcionario: FuncionarioEntity) = viewModelScope.launch {
        funcionarioDao.deletarFuncionario(funcionario)
    }
}

