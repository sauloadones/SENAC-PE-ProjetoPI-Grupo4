package com.example.appsenkaspi.ui.atividade

import androidx.recyclerview.widget.DiffUtil
import com.example.appsenkaspi.data.local.entity.AtividadeEntity

class AtividadeDiffCallback : DiffUtil.ItemCallback<AtividadeEntity>() {
    override fun areItemsTheSame(oldItem: AtividadeEntity, newItem: AtividadeEntity): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: AtividadeEntity, newItem: AtividadeEntity): Boolean {
        return oldItem == newItem
    }
}
