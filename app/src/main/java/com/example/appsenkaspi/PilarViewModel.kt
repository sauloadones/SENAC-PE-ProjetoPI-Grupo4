package com.example.appsenkaspi

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class PilarViewModel(application: Application) : AndroidViewModel(application) {

    private val pilarDao = AppDatabase.getDatabase(application).pilarDao()

    fun getPilarById(id: Int): LiveData<PilarEntity?> =
        pilarDao.getPilarById(id)



    fun listarTodosPilares(): LiveData<List<PilarEntity>> {
        return pilarDao.listarTodosPilares()  // ✅ Corrigido o nome aqui
    }

    fun inserir(pilar: PilarEntity) = viewModelScope.launch {
        pilarDao.inserirPilar(pilar)
    }

    fun atualizar(pilar: PilarEntity) = viewModelScope.launch {
        pilarDao.atualizarPilar(pilar)
    }

    fun deletar(pilar: PilarEntity) = viewModelScope.launch {
        pilarDao.deletarPilar(pilar)
    }

    suspend fun inserirRetornandoId(pilar: PilarEntity): Long =
        pilarDao.inserirPilar(pilar)







}
