package com.example.appsenkaspi

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.appsenkaspi.data.AcaoComStatus

class AcaoAdapter(
    // ✅ Callback correto para clique na ação
    private val onClick: (AcaoEntity) -> Unit
) : ListAdapter<AcaoComStatus, AcaoAdapter.ViewHolder>(AcaoDiffCallback()) {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val nomeTv = view.findViewById<TextView>(R.id.textTituloAcao)
        private val statusTv = view.findViewById<TextView>(R.id.textFracaoAcao)
        private val progresso = view.findViewById<ProgressBar>(R.id.progressoAcaoItem)
        private val seta = view.findViewById<ImageView>(R.id.iconArrowAcao)

        fun bind(acaoComStatus: AcaoComStatus) {
            val acao = acaoComStatus.acao
            nomeTv.text = acao.nome

            val total = acaoComStatus.totalAtividades
            val concluidas = acaoComStatus.ativasConcluidas
            val pct = if (total > 0) concluidas * 100 / total else 0

            if(total> 1) {
                statusTv.text = "$concluidas/$total Atividade Concluidas"
            }else {
                statusTv.text = "$concluidas/$total Atividade Concluida"

            }
            progresso.max = 100
            progresso.progress = pct

            seta.setColorFilter(Color.GREEN)

            // ✅ Clique no card chama o callback passado no construtor
            itemView.setOnClickListener {
                onClick(acao)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_acao, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bind(getItem(position))
    }
}

class AcaoDiffCallback : DiffUtil.ItemCallback<AcaoComStatus>() {
    override fun areItemsTheSame(oldItem: AcaoComStatus, newItem: AcaoComStatus): Boolean {
        return oldItem.acao.id == newItem.acao.id
    }

    override fun areContentsTheSame(oldItem: AcaoComStatus, newItem: AcaoComStatus): Boolean {
        return oldItem == newItem
    }
}
