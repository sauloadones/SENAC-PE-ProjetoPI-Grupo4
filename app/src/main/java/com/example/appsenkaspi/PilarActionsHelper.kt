package com.example.appsenkaspi

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object PilarActionsHelper {

    fun excluirPilar(context: Context, viewModel: PilarViewModel, pilar: PilarEntity) {
        val pilarAtualizado = pilar.copy(status = StatusPilar.EXCLUIDA)
        viewModel.viewModelScope.launch {
            viewModel.atualizar(pilarAtualizado)
            Toast.makeText(context, "Pilar marcado como EXCLUÍDO", Toast.LENGTH_SHORT).show()
        }
    }

    fun concluirPilar(context: Context, viewModel: PilarViewModel, pilar: PilarEntity) {
        val pilarAtualizado = pilar.copy(status = StatusPilar.CONCLUIDA)
        viewModel.viewModelScope.launch {
            viewModel.atualizar(pilarAtualizado)
            Toast.makeText(context, "Pilar marcado como CONCLUÍDO", Toast.LENGTH_SHORT).show()
        }
    }

    fun verificarVencimentoEPassarParaVencido(
        context: Context,
        viewModel: PilarViewModel,
        pilar: PilarEntity
    ) {
        val dataAtual = java.util.Date()
        if (pilar.dataPrazo.before(dataAtual) && pilar.status != StatusPilar.VENCIDA) {
            val pilarAtualizado = pilar.copy(status = StatusPilar.VENCIDA)
            viewModel.viewModelScope.launch {
                viewModel.atualizar(pilarAtualizado)
                Toast.makeText(context, "Pilar vencido atualizado para VENCIDA", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
