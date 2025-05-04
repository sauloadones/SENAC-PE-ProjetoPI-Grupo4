package com.example.appsenkaspi

import androidx.recyclerview.widget.DiffUtil

class AtividadeDiffCallback : DiffUtil.ItemCallback<AtividadeEntity>() {
    override fun areItemsTheSame(oldItem: AtividadeEntity, newItem: AtividadeEntity): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: AtividadeEntity, newItem: AtividadeEntity): Boolean {
        return oldItem == newItem
    }
}
