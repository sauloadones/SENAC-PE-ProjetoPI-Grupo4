package com.example.appsenkaspi.ui.subpilares

import com.example.appsenkaspi.data.local.dao.AcaoDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ProgressoSubpilar {


  /**
   * Calcula o progresso de um subpilar com base nas ações e atividades vinculadas.
   * Retorna um valor entre 0f e 1f.
   */
  suspend fun calcularProgressoDoSubpilarInterno(
    subpilarId: Int,
    acaoDao: AcaoDao
  ): Float = withContext(Dispatchers.IO) {
      val lista = acaoDao.listarProgressoPorSubpilar(subpilarId)

      val (somaPesos, somaTotalAtividades) = lista.fold(0f to 0) { acc, item ->
          val pesoAtual = item.progresso * item.totalAtividades
          (acc.first + pesoAtual) to (acc.second + item.totalAtividades)
      }

      if (somaTotalAtividades > 0) {
          (somaPesos / somaTotalAtividades).coerceIn(0f, 1f)
      } else {
          0f
      }
  }
}
