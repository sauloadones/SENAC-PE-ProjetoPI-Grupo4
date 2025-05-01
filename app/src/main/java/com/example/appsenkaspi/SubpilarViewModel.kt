package com.example.appsenkaspi

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class SubpilarViewModel(application: Application) : AndroidViewModel(application) {

    private val subpilarDao = AppDatabase.getDatabase(application).subpilarDao()

    fun listarSubpilaresPorPilar(pilarId: Int): LiveData<List<SubpilarEntity>> {
        return subpilarDao.listarSubpilaresPorPilar(pilarId)
    }

    fun inserir(subpilar: SubpilarEntity) = viewModelScope.launch {
        subpilarDao.inserirSubpilar(subpilar)
    }

    fun atualizar(subpilar: SubpilarEntity) = viewModelScope.launch {
        subpilarDao.atualizarSubpilar(subpilar)
    }

    fun deletar(subpilar: SubpilarEntity) = viewModelScope.launch {
        subpilarDao.deletarSubpilar(subpilar)
    }
}
