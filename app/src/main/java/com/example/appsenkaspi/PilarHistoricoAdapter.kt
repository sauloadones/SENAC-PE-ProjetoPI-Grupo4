package com.example.appsenkaspi

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PilarHistoricoAdapter(
    private var lista: List<PilarEntity>
) : RecyclerView.Adapter<PilarHistoricoAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titulo: TextView = view.findViewById(R.id.textTitulo)
        val status: TextView = view.findViewById(R.id.textStatus)
        val statusOverlay: ImageView = view.findViewById(R.id.statusOverlay)
        val dataTexto: TextView = view.findViewById(R.id.textData)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pilar_historico, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pilar = lista[position]
        holder.titulo.text = pilar.nome

        when (pilar.status) {
            StatusPilar.CONCLUIDA -> {
                holder.status.text = "Concluído"
                holder.statusOverlay.setImageResource(R.drawable.bverde)
                holder.dataTexto.text = "Concluído em: ${pilar.dataPrazo}"
            }
            StatusPilar.EXCLUIDA -> {
                holder.status.text = "Excluído"
                holder.statusOverlay.setImageResource(R.drawable.bvermelho)
                holder.dataTexto.text = "Excluído em: ${pilar.dataPrazo}"
            }
            StatusPilar.VENCIDA -> {
                holder.status.text = "Vencido"
                holder.statusOverlay.setImageResource(R.drawable.blaranja)
                holder.dataTexto.text = "Vencido em: ${pilar.dataPrazo}"
            }
            else -> {
                holder.status.text = ""
                holder.dataTexto.text = ""
            }
        }
    }

    override fun getItemCount(): Int = lista.size

    fun atualizarLista(novaLista: List<PilarEntity>) {
        lista = novaLista
        notifyDataSetChanged()
    }
}
