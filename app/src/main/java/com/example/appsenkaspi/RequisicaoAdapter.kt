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
  private val modoCoordenador: Boolean,
  private val onClick: (RequisicaoEntity) -> Unit = {}
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

    val titulo = when (requisicao.tipo) {
      TipoRequisicao.CRIAR_ATIVIDADE -> "Criação de Atividade"
      TipoRequisicao.EDITAR_ATIVIDADE -> "Edição de Atividade"
      TipoRequisicao.COMPLETAR_ATIVIDADE -> "Conclusão de Atividade"
      TipoRequisicao.CRIAR_ACAO -> "Criação de Ação"
      TipoRequisicao.EDITAR_ACAO -> "Edição de Ação"
      TipoRequisicao.ATIVIDADE_PARA_VENCER -> "Atividade Proxima do Prazo"
      else -> "Requisição"
    }

    holder.textoTitulo.text = titulo

    if (modoCoordenador) {
      // Visual do Coordenador: sempre pendente
      holder.iconStatus.setImageResource(R.drawable.ic_help)
      holder.iconStatus.setColorFilter(Color.parseColor("#FFC107")) // Amarelo
      holder.textoMensagem.text = "Solicitação pendente de aprovação"
      holder.itemView.setOnClickListener { onClick(requisicao) }

    } else {
      // Visual do Apoio: usa mensagem personalizada se existir
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
      holder.itemView.setOnClickListener(null) // sem ação no modo apoio
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
