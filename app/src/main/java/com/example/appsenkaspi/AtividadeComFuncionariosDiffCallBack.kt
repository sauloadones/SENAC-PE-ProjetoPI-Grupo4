package com.example.appsenkaspi

import androidx.recyclerview.widget.DiffUtil

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
