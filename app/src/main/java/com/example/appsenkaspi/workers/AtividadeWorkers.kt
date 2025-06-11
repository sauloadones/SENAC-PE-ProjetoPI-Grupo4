package com.example.appsenkaspi.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.appsenkaspi.data.local.database.AppDatabase
import com.example.appsenkaspi.ui.atividade.AtividadeRepository

class VerificarAtividadesVencidasWorker(
  context: Context,
  workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

  override suspend fun doWork(): Result {
    val db = AppDatabase.getDatabase(applicationContext)
    val repository = AtividadeRepository(
      context = applicationContext,
      atividadeDao = db.atividadeDao(),
      atividadeFuncionarioDao = db.atividadeFuncionarioDao(),
      requisicaoDao = db.requisicaoDao()
    )

    repository.verificarAtividadesVencidas()
    return Result.success()
  }
}
