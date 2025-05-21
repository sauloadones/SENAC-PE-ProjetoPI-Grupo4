package com.example.appsenkaspi

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.cardview.widget.CardView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PilarAdapter(
  private val onClickPilar: (PilarEntity) -> Unit,
  private val verificarSubpilares: suspend (Int) -> Boolean
) : ListAdapter<PilarEntity, PilarAdapter.PilarViewHolder>(PilarDiffCallback()) {

  inner class PilarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val textNomePilar: TextView = itemView.findViewById(R.id.textNomePilar)
    private val cardPilar: CardView = itemView.findViewById(R.id.cardPilar)
    private val iconeSubpilares: ImageView = itemView.findViewById(R.id.iconeSubpilares)

    fun bind(pilar: PilarEntity, position: Int) {
      textNomePilar.text = "${position + 1}º Pilar"

      cardPilar.setOnClickListener {
        onClickPilar(pilar)
      }

      // Verificar se o pilar tem subpilares e mostrar o ícone
      CoroutineScope(Dispatchers.Main).launch {
        val temSubpilares = verificarSubpilares(pilar.id)
        iconeSubpilares.visibility = if (temSubpilares) View.VISIBLE else View.GONE
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
