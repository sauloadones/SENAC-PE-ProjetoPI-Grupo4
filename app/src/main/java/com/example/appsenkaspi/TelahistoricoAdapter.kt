package com.example.appsenkaspi

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class TelaHistoricoAdapter(
    private var listaPilares: List<PilarEntity>,
    private val onClickPilar: (PilarEntity) -> Unit
) : RecyclerView.Adapter<TelaHistoricoAdapter.HistoricoViewHolder>() {

    inner class HistoricoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textTitulo: TextView = itemView.findViewById(R.id.textTitulo)
        val statusOverlay: ImageView = itemView.findViewById(R.id.statusOverlay)
        val textStatus: TextView = itemView.findViewById(R.id.textStatus)
        val textData: TextView = itemView.findViewById(R.id.textData)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoricoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pilar_historico, parent, false)
        return HistoricoViewHolder(view)
    }

    override fun getItemCount() = listaPilares.size

    override fun onBindViewHolder(holder: HistoricoViewHolder, position: Int) {
        val pilar = listaPilares[position]

        holder.textTitulo.text = pilar.nome ?: "Sem título"

        val dataFormatada = formatarData(
            when (pilar.status) {
                StatusPilar.EXCLUIDO -> pilar.dataExclusao
                StatusPilar.CONCLUIDO, StatusPilar.VENCIDO -> pilar.dataPrazo
                else -> null
            }
        )

        when (pilar.status) {
            StatusPilar.CONCLUIDO -> {
                holder.statusOverlay.visibility = View.VISIBLE
                holder.statusOverlay.setImageResource(R.drawable.bverde)
                holder.textStatus.text = "Concluído"
                holder.textData.text = dataFormatada?.let { "Concluído em: $it" } ?: ""
            }
            StatusPilar.EXCLUIDO -> {
                holder.statusOverlay.visibility = View.VISIBLE
                holder.statusOverlay.setImageResource(R.drawable.bvermelho)
                holder.textStatus.text = "Excluído"
                holder.textData.text = dataFormatada?.let { "Excluído em: $it" } ?: ""
            }
            StatusPilar.VENCIDO -> {
                holder.statusOverlay.visibility = View.VISIBLE
                holder.statusOverlay.setImageResource(R.drawable.blaranja)
                holder.textStatus.text = "Vencido"
                holder.textData.text = dataFormatada?.let { "Venceu em: $it" } ?: ""
            }
            else -> {
                holder.statusOverlay.visibility = View.GONE
                holder.textStatus.text = ""
                holder.textData.text = ""
            }
        }

        holder.itemView.setOnClickListener {
            onClickPilar(pilar)
        }
    }

    fun atualizarLista(novaLista: List<PilarEntity>) {
        listaPilares = novaLista
        notifyDataSetChanged()
    }

    private fun formatarData(data: Date?): String? {
        return data?.let {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            sdf.format(it)
        }
    }
}
