package com.example.appsenkaspi

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class AcaoViewModel(application: Application) : AndroidViewModel(application) {

    private val acaoDao = AppDatabase.getDatabase(application).acaoDao()

    fun listarAcoesPorPilar(pilarId: Int): LiveData<List<AcaoEntity>> {
        return acaoDao.listarAcoesPorPilar(pilarId)
    }

    fun getAcaoById(id: Int): LiveData<AcaoEntity?> =
        acaoDao.getAcaoById(id)

    suspend fun getAcaoByIdNow(id: Int): AcaoEntity? {
        return acaoDao.getAcaoByIdNow(id)
    }

    fun inserir(acao: AcaoEntity) = viewModelScope.launch {
        acaoDao.inserirAcao(acao)
    }

    fun atualizar(acao: AcaoEntity) = viewModelScope.launch {
        acaoDao.atualizarAcao(acao)
    }

    fun deletar(acao: AcaoEntity) = viewModelScope.launch {
        acaoDao.deletarAcao(acao)
    }
    suspend fun inserirRetornandoId(acao: AcaoEntity): Int {
        return acaoDao.inserirAcao(acao).toInt()
    }

    fun listarPorPilar(pilarId: Int) = acaoDao.listarPorPilar(pilarId)


}
