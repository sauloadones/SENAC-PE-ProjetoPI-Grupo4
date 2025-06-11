package com.example.appsenkaspi.ui.acao

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
import com.example.appsenkaspi.R
import com.example.appsenkaspi.data.local.entity.AcaoEntity
import com.example.appsenkaspi.domain.model.AcaoComStatus


class AcaoAdapter(
    private val onClick: (AcaoEntity) -> Unit
) : ListAdapter<AcaoComStatus, AcaoAdapter.ViewHolder>(AcaoDiffCallback()) {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val nomeTv = view.findViewById<TextView>(R.id.textTituloAcao)
        private val statusTv = view.findViewById<TextView>(R.id.textFracaoAcao)
        private val progressoBar = view.findViewById<ProgressBar>(R.id.progressoAcaoItem)
        private val textoPorcentagem = view.findViewById<TextView>(R.id.txtPorcentagemAcao)
        private val seta = view.findViewById<ImageView>(R.id.iconArrowAcao)

        fun bind(acaoComStatus: AcaoComStatus) {
            val acao = acaoComStatus.acao
            nomeTv.text = acao.nome

            val total = acaoComStatus.totalAtividades
            val concluidas = acaoComStatus.ativasConcluidas
            val pct = if (total > 0) concluidas * 100 / total else 0

            // Texto "X/Y Atividade(s) Concluída(s)"
            statusTv.text = "$concluidas/$total ${if (total == 1) "Atividade" else "Atividades"} Concluída${if (concluidas == 1) "" else "s"}"

            // Barra de progresso
            progressoBar.max = 100
            progressoBar.progress = pct

            // Porcentagem ao lado da barra
            textoPorcentagem.text = "$pct%"

            // Ícone
            seta.setColorFilter(Color.GREEN)

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
