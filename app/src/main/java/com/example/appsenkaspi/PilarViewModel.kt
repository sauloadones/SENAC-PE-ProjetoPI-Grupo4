package com.example.appsenkaspi

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlin.collections.*

class PilarViewModel(application: Application) : AndroidViewModel(application) {

    private val pilarDao = AppDatabase.getDatabase(application).pilarDao()
    private val acaoDao = AppDatabase.getDatabase(application).acaoDao()
    private val subpilarDao = AppDatabase.getDatabase(application).subpilarDao()

  fun getPilarById(id: Int): LiveData<PilarEntity?> = pilarDao.getPilarById(id)

    fun listarTodosPilares(): LiveData<List<PilarEntity>> = pilarDao.listarTodosPilares()

    fun inserir(pilar: PilarEntity) = viewModelScope.launch {
        pilarDao.inserirPilar(pilar)
    }

    fun atualizar(pilar: PilarEntity) = viewModelScope.launch {
        pilarDao.atualizarPilar(pilar)
    }

    fun deletar(pilar: PilarEntity) = viewModelScope.launch {
        pilarDao.deletarPilar(pilar)
    }

    suspend fun inserirRetornandoId(pilar: PilarEntity): Long = pilarDao.inserirPilar(pilar)

    fun calcularProgressoDoPilar(pilarId: Int, callback: (Float) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val progresso = calcularProgressoInterno(pilarId)
            withContext(Dispatchers.Main) {
                callback(progresso)
            }
        }
    }
    suspend fun temSubpilares(pilarId: Int): Boolean {
      return subpilarDao.contarSubpilaresDoPilar(pilarId) > 0
    }

  suspend fun temSubpilaresDireto(pilarId: Int): Boolean {
    return AppDatabase.getDatabase(getApplication()).subpilarDao().getQuantidadePorPilar(pilarId) > 0
  }



  // Função suspensa reutilizável
    suspend fun calcularProgressoInterno(pilarId: Int): Float = coroutineScope {
      val subpilares = subpilarDao.listarSubpilaresPorTelaPilar(pilarId)

      if (subpilares.isNotEmpty()) {
        val subProgressoList = subpilares.map { subpilar ->
          async {
            ProgressoSubpilar.calcularProgressoDoSubpilarInterno(subpilar.id, acaoDao)
          }
        }.awaitAll()

        subProgressoList.average().toFloat()
      } else {
        val lista = acaoDao.listarProgressoPorPilar(pilarId)
        val (somaPesos, somaTotalAtividades) = lista.fold(0f to 0) { acc, item ->
          val pesoAtual = item.progresso * item.totalAtividades
          (acc.first + pesoAtual) to (acc.second + item.totalAtividades)
        }
        if (somaTotalAtividades > 0) somaPesos / somaTotalAtividades else 0f
      }
    }


  suspend fun getTodosPilares(): List<PilarEntity> = withContext(Dispatchers.IO) {
        pilarDao.getTodosPilares()
    }

}
