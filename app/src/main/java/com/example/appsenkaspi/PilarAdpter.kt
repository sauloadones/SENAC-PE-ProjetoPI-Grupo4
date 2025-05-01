package com.example.appsenkaspi

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.cardview.widget.CardView

class PilarAdapter(
    private val onClickPilar: (PilarEntity) -> Unit
) : ListAdapter<PilarEntity, PilarAdapter.PilarViewHolder>(PilarDiffCallback()) {

    inner class PilarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textNomePilar: TextView = itemView.findViewById(R.id.textNomePilar)
        private val cardPilar: CardView = itemView.findViewById(R.id.cardPilar)

        fun bind(pilar: PilarEntity, position: Int) {
            textNomePilar.text = "${position + 1}º Pilar" // ✅ Agora mostra no estilo 1º, 2º, 3º
            cardPilar.setOnClickListener {
                onClickPilar(pilar)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PilarViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pilar, parent, false)
        return PilarViewHolder(view)
    }

    override fun onBindViewHolder(holder: PilarViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }
}
