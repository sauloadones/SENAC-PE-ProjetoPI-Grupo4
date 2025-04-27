package com.example.appsenkaspi

import androidx.recyclerview.widget.DiffUtil

class PilarDiffCallback : DiffUtil.ItemCallback<PilarEntity>() {
    override fun areItemsTheSame(oldItem: PilarEntity, newItem: PilarEntity): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: PilarEntity, newItem: PilarEntity): Boolean {
        return oldItem == newItem
    }
}
