package com.example.appsenkaspi

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.room.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SubpilarViewModel(application: Application) : AndroidViewModel(application) {
  private val acaoDao = AppDatabase.getDatabase(application).acaoDao()

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
  fun calcularProgressoDoSubpilar(pilarId: Int, callback: (Float) -> Unit) {
    viewModelScope.launch(Dispatchers.IO) {
      val progresso = calcularProgressoInterno(pilarId)
      withContext(Dispatchers.Main) {
        callback(progresso)
      }
    }
  }

  fun getSubpilarById(id: Int): LiveData<SubpilarEntity> {
    return subpilarDao.getSubpilarById(id)
  }

  suspend fun calcularProgressoInterno(subpilarId: Int): Float = withContext(Dispatchers.IO) {
    val lista = acaoDao.listarProgressoPorSubpilar(subpilarId)
    val (somaPesos, somaTotalAtividades) = lista.fold(0f to 0) { acc, item ->
      val pesoAtual = item.progresso * item.totalAtividades
      (acc.first + pesoAtual) to (acc.second + item.totalAtividades)
    }
    if (somaTotalAtividades > 0) (somaPesos / somaTotalAtividades).coerceIn(0f, 1f) else 0f
  }
  fun calcularProgressoDoPilarComSubpilares(pilarId: Int, callback: (Float) -> Unit) {
    viewModelScope.launch(Dispatchers.IO) {
      val lista = acaoDao.listarProgressoPorSubpilaresDoPilar(pilarId)

      val (somaPesos, somaTotalAcoes) = lista.fold(0f to 0) { acc, item ->
        val pesoAtual = item.progresso * item.totalAcoes
        (acc.first + pesoAtual) to (acc.second + item.totalAcoes)
      }

      val progresso = if (somaTotalAcoes > 0) {
        (somaPesos / somaTotalAcoes).coerceIn(0f, 1f)
      } else {
        0f
      }

      withContext(Dispatchers.Main) {
        callback(progresso)
      }
    }
  }





  suspend fun inserirRetornandoId(subpilar: SubpilarEntity): Long {
    return AppDatabase.getDatabase(getApplication()).subpilarDao().inserirRetornandoId(subpilar)
  }



}
