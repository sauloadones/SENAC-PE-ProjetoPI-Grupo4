package com.example.appsenkaspi

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appsenkaspi.R

class PilaresAdapter(private val pilares: List<String>) :
    RecyclerView.Adapter<PilaresAdapter.PilarViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PilarViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pilar, parent, false)
        return PilarViewHolder(view)
    }

    override fun onBindViewHolder(holder: PilarViewHolder, position: Int) {
        holder.bind(pilares[position])
    }

    override fun getItemCount(): Int = pilares.size

    inner class PilarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nomePilar: TextView = itemView.findViewById(R.id.nomePilar)

        fun bind(pilar: String) {
            nomePilar.text = pilar
        }
    }
}
