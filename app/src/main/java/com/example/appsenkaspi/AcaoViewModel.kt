package com.example.appsenkaspi

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.appsenkaspi.Converters.StatusAcao
import com.example.appsenkaspi.data.AcaoComStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class AcaoViewModel(application: Application) : AndroidViewModel(application) {

    private val acaoDao = AppDatabase.getDatabase(application).acaoDao()
    private val atividadeDao = AppDatabase.getDatabase(application).atividadeDao()

    fun getAcaoById(id: Int): LiveData<AcaoEntity?> {
        return acaoDao.getAcaoById(id)
    }

    fun listarAcoesPorPilar(pilarId: Int): LiveData<List<AcaoComStatus>> {
        return acaoDao.listarPorPilar(pilarId)
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
    suspend fun inserirRetornandoId(acao: AcaoEntity): Long {
        return acaoDao.inserirComRetorno(acao)
    }


    fun atualizarStatusAcaoAutomaticamente(acaoId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val total = atividadeDao.contarTotalPorAcaoValor(acaoId)
            val concluidas = atividadeDao.contarConcluidasPorAcaoValor(acaoId)

            val acao = acaoDao.getAcaoPorIdDireto(acaoId) ?: return@launch
            val hoje = Calendar.getInstance().time

            val novoStatus = when {
                total == 0 -> StatusAcao.PLANEJADA
                concluidas == total && hoje.before(acao.dataPrazo) -> StatusAcao.CONCLUIDA
                hoje.after(acao.dataPrazo) -> StatusAcao.VENCIDA
                else -> StatusAcao.EM_ANDAMENTO
            }

            if (acao.status != novoStatus) {
                val acaoAtualizada = acao.copy(status = novoStatus)
                acaoDao.atualizarAcao(acaoAtualizada)
            }
        }
    }
}
