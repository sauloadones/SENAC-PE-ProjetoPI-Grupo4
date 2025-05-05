package com.example.appsenkaspi.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.appsenkaspi.Notificacao.NotificacaoComRequisicaoEFuncionario

import com.example.appsenkaspi.R
import com.example.appsenkaspi.StatusNotificacao

class NotificacaoAdapter(
    private val listener: OnItemClickListener
) : ListAdapter<NotificacaoComRequisicaoEFuncionario, NotificacaoAdapter.ViewHolder>(DIFF_CALLBACK) {

    interface OnItemClickListener {
        fun onItemClick(notificacao: NotificacaoComRequisicaoEFuncionario)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titulo = itemView.findViewById<TextView>(R.id.textoTitulo)
        private val subtitulo = itemView.findViewById<TextView>(R.id.textoMensagem)

        fun bind(item: NotificacaoComRequisicaoEFuncionario) {
            val contexto = itemView.context

            titulo.text = when (item.notificacao.tipo.name) {
                "PEDIDO_CRIACAO_ATIVIDADE" -> "Solicitação de Nova Atividade"
                "PEDIDO_CRIACAO_ACAO" -> "Solicitação de Nova Ação"
                "PEDIDO_CONFIRMACAO_ATIVIDADE" -> "Aprovação de Atividade"
                "PEDIDO_EDICAO_ATIVIDADE" -> "Edição de Atividade"
                "PEDIDO_EDICAO_ACAO" -> "Edição de Ação"
                "CRIACAO_ACAO_ACEITA" -> "Pedido de Ação Aprovado"
                "CRIACAO_ATIVIDADE_ACEITA" -> "Pedido de Atividade Aprovado"
                "EDICAO_ACAO_ACEITA" -> "Edição de Ação Aprovada"
                "EDICAO_ATIVIDADE_ACEITA" -> "Edição de Atividade Aprovada"
                "CONFIRMACAO_ATIVIDADE_ACEITA" -> "Confirmação Aprovada"
                else -> item.notificacao.tipo.name
            }

            subtitulo.text = item.notificacao.mensagem

            // Estilo de leitura
            if (item.notificacao.status == StatusNotificacao.LIDA) {
                itemView.setBackgroundColor(ContextCompat.getColor(contexto, R.color.cinza_escuro))
                titulo.setTextColor(Color.LTGRAY)
            } else {
                itemView.setBackgroundColor(ContextCompat.getColor(contexto, R.color.cinza_claro))
                titulo.setTextColor(Color.WHITE)
            }

            itemView.setOnClickListener {
                listener.onItemClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notificacao, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<NotificacaoComRequisicaoEFuncionario>() {
            override fun areItemsTheSame(
                oldItem: NotificacaoComRequisicaoEFuncionario,
                newItem: NotificacaoComRequisicaoEFuncionario
            ): Boolean = oldItem.notificacao.id == newItem.notificacao.id

            override fun areContentsTheSame(
                oldItem: NotificacaoComRequisicaoEFuncionario,
                newItem: NotificacaoComRequisicaoEFuncionario
            ): Boolean = oldItem == newItem
        }
    }
}
