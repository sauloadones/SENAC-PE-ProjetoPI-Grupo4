package com.example.appsenkaspi

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PilarViewModel(application: Application) : AndroidViewModel(application) {

    private val pilarDao = AppDatabase.getDatabase(application).pilarDao()
    private val acaoDao = AppDatabase.getDatabase(application).acaoDao()

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

    fun calcularProgressoDoPilar(pilarId: Int, callback: (Float) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val lista = acaoDao.listarProgressoPorPilar(pilarId)

            val (somaPesos, somaTotalAtividades) = lista.fold(0f to 0) { acc, item ->
                val pesoAtual = item.progresso * item.totalAtividades
                (acc.first + pesoAtual) to (acc.second + item.totalAtividades)
            }

            val progressoFinal = if (somaTotalAtividades > 0) somaPesos / somaTotalAtividades else 0f

            withContext(Dispatchers.Main) {
                callback(progressoFinal)
            }
        }
    }







}
