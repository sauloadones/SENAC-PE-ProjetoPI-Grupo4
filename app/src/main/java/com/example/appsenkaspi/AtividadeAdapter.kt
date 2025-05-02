package com.example.appsenkaspi

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.appsenkaspi.Converters.PrioridadeAtividade
import com.example.appsenkaspi.Converters.StatusAtividade

class AtividadeAdapter :
    ListAdapter<AtividadeEntity, AtividadeAdapter.ViewHolder>(AtividadeDiffCallback()) {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titulo = view.findViewById<TextView>(R.id.tituloAtividade)
        val status = view.findViewById<TextView>(R.id.textStatusAtividade)
        val icone = view.findViewById<ImageView>(R.id.iconeAtividade)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val item = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_atividade, parent, false)
        return ViewHolder(item)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val atividade = getItem(position)

        holder.titulo.text = atividade.nome

        holder.status.text = when (atividade.status) {
            StatusAtividade.PENDENTE -> "Pendente"
            StatusAtividade.EM_ANDAMENTO -> "Em andamento"
            StatusAtividade.CONCLUIDA -> "Concluída"
            StatusAtividade.EXCLUIDA -> "Cancelada"
            StatusAtividade.VENCIDA -> "Vencida"
        }

        val cor = when (atividade.prioridade) {
            PrioridadeAtividade.BAIXA -> Color.parseColor("#4CAF50")   // verde
            PrioridadeAtividade.MEDIA -> Color.parseColor("#FFC107")   // amarelo
            PrioridadeAtividade.ALTA -> Color.parseColor("#F44336")    // vermelho
        }

        holder.icone.setColorFilter(cor)
    }
}
