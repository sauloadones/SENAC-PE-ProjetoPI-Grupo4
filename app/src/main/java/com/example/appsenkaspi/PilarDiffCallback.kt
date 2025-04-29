package com.example.appsenkaspi

import androidx.recyclerview.widget.DiffUtil

class PilarDiffCallback : DiffUtil.ItemCallback<PilarEntity>() {

    override fun areItemsTheSame(oldItem: PilarEntity, newItem: PilarEntity): Boolean {
        // Comparando pelo ID
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: PilarEntity, newItem: PilarEntity): Boolean {
        // Comparando campos relevantes
        return oldItem.nome == newItem.nome &&
                oldItem.descricao == newItem.descricao &&
                oldItem.dataInicio == newItem.dataInicio &&
                oldItem.dataPrazo == newItem.dataPrazo
    }
}
