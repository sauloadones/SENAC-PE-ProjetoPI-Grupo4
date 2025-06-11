package com.example.appsenkaspi.ui.atividade

import androidx.recyclerview.widget.DiffUtil
import com.example.appsenkaspi.domain.model.AtividadeComFuncionarios

class AtividadeComFuncionariosDiffCallback : DiffUtil.ItemCallback<AtividadeComFuncionarios>() {
    override fun areItemsTheSame(
        oldItem: AtividadeComFuncionarios,
        newItem: AtividadeComFuncionarios
    ): Boolean {
        // Compara os IDs únicos da atividade
        return oldItem.atividade.id == newItem.atividade.id
    }

    override fun areContentsTheSame(
        oldItem: AtividadeComFuncionarios,
        newItem: AtividadeComFuncionarios
    ): Boolean {
        // Compara todo o conteúdo da atividade e lista de responsáveis
        return oldItem == newItem
    }
}
