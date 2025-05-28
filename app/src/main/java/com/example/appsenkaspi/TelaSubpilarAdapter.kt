package com.example.appsenkaspi

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.appsenkaspi.R
import com.example.appsenkaspi.SubpilarEntity

class TelaSubpilarAdapter(
  private val onClick: (SubpilarEntity) -> Unit,
  private val progressoMap: Map<Int, Float> // subpilarId → progresso (0f a 1f)
) : ListAdapter<SubpilarEntity, TelaSubpilarAdapter.SubpilarViewHolder>(DIFF_CALLBACK) {

  inner class SubpilarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val tvTitulo: TextView = itemView.findViewById(R.id.tvSubpilarTitulo)
    private val tvNome: TextView = itemView.findViewById(R.id.tvNomeSubpilar)
    private val progressoBar: ProgressBar = itemView.findViewById(R.id.progressoSubpilar)
    private val percentual: TextView = itemView.findViewById(R.id.tvPercentual)
    private val iconeSeta: ImageView = itemView.findViewById(R.id.iconeSeta)

    fun bind(subpilar: SubpilarEntity, posicao: Int) {
      val numero = "${posicao + 1}º Subpilar"
      tvTitulo.text = numero
      tvNome.text = subpilar.nome

      val progresso = progressoMap[subpilar.id] ?: 0f
      val progressoPercentual = (progresso * 100).toInt()

      progressoBar.progress = progressoPercentual
      percentual.text = "$progressoPercentual%"

      itemView.setOnClickListener { onClick(subpilar) }
      iconeSeta.setOnClickListener { onClick(subpilar) }
    }

  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubpilarViewHolder {
    val view = LayoutInflater.from(parent.context)
      .inflate(R.layout.item_subpilar_card, parent, false)
    return SubpilarViewHolder(view)
  }

  override fun onBindViewHolder(holder: SubpilarViewHolder, position: Int) {
    val subpilar = getItem(position)
    holder.bind(subpilar, position)
  }

  companion object {
    private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<SubpilarEntity>() {
      override fun areItemsTheSame(oldItem: SubpilarEntity, newItem: SubpilarEntity): Boolean {
        return oldItem.id == newItem.id
      }

      override fun areContentsTheSame(oldItem: SubpilarEntity, newItem: SubpilarEntity): Boolean {
        return oldItem == newItem
      }
    }
  }
}

