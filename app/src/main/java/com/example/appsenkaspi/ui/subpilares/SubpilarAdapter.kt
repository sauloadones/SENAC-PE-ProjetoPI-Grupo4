package com.example.appsenkaspi.ui.subpilares

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appsenkaspi.R
import com.example.appsenkaspi.ui.pilar.CriarPilarFragment

class SubpilarAdapter(private val subpilares: List<CriarPilarFragment.SubpilarTemp>) :
  RecyclerView.Adapter<SubpilarAdapter.SubpilarViewHolder>() {

  class SubpilarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val textNomeSubpilar: TextView = itemView.findViewById(R.id.textNomeSubpilar)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubpilarViewHolder {
    val view = LayoutInflater.from(parent.context).inflate(R.layout.item_subpilar, parent, false)
    return SubpilarViewHolder(view)
  }

  override fun onBindViewHolder(holder: SubpilarViewHolder, position: Int) {
    holder.textNomeSubpilar.text = subpilares[position].nome
  }

  override fun getItemCount(): Int = subpilares.size
}
