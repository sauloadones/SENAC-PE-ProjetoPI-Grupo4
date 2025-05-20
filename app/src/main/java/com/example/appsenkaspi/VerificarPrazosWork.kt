
package com.example.appsenkaspi


import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters


class VerificacaoDePrazosWorker(
  context: Context,
  workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

  override suspend fun doWork(): Result {
    val db = AppDatabase.getDatabase(applicationContext)

    val atividadeRepository = AtividadeRepository(
      context = applicationContext,
      atividadeDao = db.atividadeDao(),
      atividadeFuncionarioDao = db.atividadeFuncionarioDao(),
      requisicaoDao = db.requisicaoDao()
    )

    try {
      atividadeRepository.verificarNotificacoesDePrazo()
      atividadeRepository.verificarAtividadesVencidas()
    } catch (e: Exception) {
      Log.e("Worker", "Erro ao verificar prazos", e)
      return Result.failure()
    }

    return Result.success()
  }
}

