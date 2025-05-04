package com.example.appsenkaspi

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.appsenkaspi.Converters.StatusRequisicao
import com.example.appsenkaspi.Converters.TipoRequisicao
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

    fun deletar(requisicao: RequisicaoEntity) = viewModelScope.launch {
        requisicaoDao.deletar(requisicao)
    }
}
