package com.example.appsenkaspi

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.appsenkaspi.R
import com.example.appsenkaspi.RequisicaoEntity
import com.example.appsenkaspi.StatusRequisicao
import com.example.appsenkaspi.TipoRequisicao

class RequisicaoAdapter(
  private val funcionarioIdLogado: Int,
  var modoCoordenador: Boolean,
  var onItemClick: (RequisicaoEntity) -> Unit = {}
) : ListAdapter<RequisicaoEntity, RequisicaoAdapter.ViewHolder>(DIFF_CALLBACK) {

  inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val iconStatus: ImageView = itemView.findViewById(R.id.iconStatus)
    val textoTitulo: TextView = itemView.findViewById(R.id.textoTitulo)
    val textoMensagem: TextView = itemView.findViewById(R.id.textoMensagem)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val view = LayoutInflater.from(parent.context)
      .inflate(R.layout.item_notificacao, parent, false)
    return ViewHolder(view)
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val requisicao = getItem(position)

    // 🔔 Mostrar ATIVIDADE_PARA_VENCER apenas para o responsável
    if (requisicao.tipo == TipoRequisicao.ATIVIDADE_PARA_VENCER) {
      if (requisicao.solicitanteId != funcionarioIdLogado) {
        holder.itemView.visibility = View.GONE
        holder.itemView.layoutParams = RecyclerView.LayoutParams(0, 0)
        return
      }

      holder.textoTitulo.text = "Prazo Próximo"
      holder.textoMensagem.text = requisicao.mensagemResposta ?: "Atividade próxima do vencimento"
      holder.iconStatus.setImageResource(R.drawable.ic_info)
      holder.iconStatus.setColorFilter(Color.parseColor("#2196F3"))
      holder.itemView.setOnClickListener(null)
      return
    }

    val titulo = when (requisicao.tipo) {
      TipoRequisicao.CRIAR_ATIVIDADE -> "Criação de Atividade"
      TipoRequisicao.EDITAR_ATIVIDADE -> "Edição de Atividade"
      TipoRequisicao.COMPLETAR_ATIVIDADE -> "Conclusão de Atividade"
      TipoRequisicao.CRIAR_ACAO -> "Criação de Ação"
      TipoRequisicao.EDITAR_ACAO -> "Edição de Ação"
      else -> "Requisição"
    }

    holder.textoTitulo.text = titulo

    if (modoCoordenador) {
      holder.iconStatus.setImageResource(R.drawable.ic_help)
      holder.iconStatus.setColorFilter(Color.parseColor("#FFC107"))
      holder.textoMensagem.text = requisicao.mensagemResposta?.takeIf { it.isNotBlank() }
        ?: "Solicitação pendente de aprovação"
      holder.itemView.setOnClickListener { onItemClick(requisicao) }
    } else {
      val (icone, cor) = when (requisicao.status) {
        StatusRequisicao.ACEITA -> R.drawable.ic_check_circle to "#4CAF50"
        StatusRequisicao.RECUSADA -> R.drawable.ic_cancel to "#F44336"
        StatusRequisicao.PENDENTE -> R.drawable.ic_help to "#FFC107"
      }

      val mensagem = requisicao.mensagemResposta?.takeIf { it.isNotBlank() } ?: when (requisicao.status) {
        StatusRequisicao.ACEITA -> "Sua solicitação foi aprovada pelo coordenador."
        StatusRequisicao.RECUSADA -> "Sua solicitação foi recusada pelo coordenador."
        StatusRequisicao.PENDENTE -> "Sua solicitação está aguardando aprovação."
      }

      holder.iconStatus.setImageResource(icone)
      holder.iconStatus.setColorFilter(Color.parseColor(cor))
      holder.textoMensagem.text = mensagem
      holder.itemView.setOnClickListener(null)
    }
  }

  companion object {
    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<RequisicaoEntity>() {
      override fun areItemsTheSame(oldItem: RequisicaoEntity, newItem: RequisicaoEntity): Boolean {
        return oldItem.id == newItem.id
      }

      override fun areContentsTheSame(oldItem: RequisicaoEntity, newItem: RequisicaoEntity): Boolean {
        return oldItem == newItem
      }
    }
  }
}
